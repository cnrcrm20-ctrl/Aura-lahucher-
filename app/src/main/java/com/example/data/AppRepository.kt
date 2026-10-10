package com.example.data

import android.app.role.RoleManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Process
import android.os.UserHandle
import android.os.UserManager
import android.provider.Settings
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppRepository(
    private val context: Context,
    private val preferences: LauncherPreferences,
    private val scope: CoroutineScope
) {
    private val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
    private val userManager = context.getSystemService(Context.USER_SERVICE) as? UserManager
    private val packageManager = context.packageManager

    private val _installedApps = MutableStateFlow<List<AppItem>>(emptyList())
    val installedApps: StateFlow<List<AppItem>> = _installedApps.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var launcherAppsCallback: LauncherApps.Callback? = null
    private var packageBroadcastReceiver: BroadcastReceiver? = null

    init {
        registerPackageListeners()
        loadInstalledApps()
    }

    fun refreshApps() {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        scope.launch {
            _isLoading.value = true
            val apps = withContext(Dispatchers.Default) {
                queryLaunchableApps()
            }
            _installedApps.value = apps
            _isLoading.value = false

            // If dock is empty, initialize with sensible defaults
            initializeDefaultDockIfEmpty(apps)
        }
    }

    private fun queryLaunchableApps(): List<AppItem> {
        val ownPackageName = context.packageName
        val appList = mutableListOf<AppItem>()

        try {
            val profiles = userManager?.userProfiles ?: listOf(Process.myUserHandle())
            for (profile in profiles) {
                val activityList = launcherApps?.getActivityList(null, profile)
                if (!activityList.isNullOrEmpty()) {
                    for (activityInfo in activityList) {
                        if (activityInfo.applicationInfo.packageName == ownPackageName) {
                            continue
                        }
                        val appItem = createAppItemFromLauncherInfo(activityInfo, profile)
                        appList.add(appItem)
                    }
                }
            }
        } catch (e: Exception) {
            // Fallback to PackageManager query
            appList.clear()
        }

        // Fallback if LauncherApps returned empty
        if (appList.isEmpty()) {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(
                    intent,
                    PackageManager.ResolveInfoFlags.of(0)
                )
            } else {
                packageManager.queryIntentActivities(intent, 0)
            }

            for (resolveInfo in resolveInfos) {
                val pkgName = resolveInfo.activityInfo.packageName
                if (pkgName == ownPackageName) continue

                val label = try {
                    resolveInfo.loadLabel(packageManager).toString()
                } catch (e: Exception) {
                    pkgName
                }
                val drawable = try {
                    resolveInfo.loadIcon(packageManager)
                } catch (e: Exception) {
                    null
                }
                val isSystem = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                val imageBitmap = drawable?.toSafeImageBitmap()
                appList.add(
                    AppItem(
                        id = "$pkgName/${resolveInfo.activityInfo.name}",
                        packageName = pkgName,
                        activityName = resolveInfo.activityInfo.name,
                        label = label,
                        userHandle = Process.myUserHandle(),
                        isSystemApp = isSystem,
                        installTime = 0L,
                        imageBitmap = imageBitmap
                    )
                )
            }
        }

        // Sort alphabetically by label ignoring case
        return appList.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.label })
    }

    private fun createAppItemFromLauncherInfo(
        info: LauncherActivityInfo,
        profile: UserHandle
    ): AppItem {
        val packageName = info.applicationInfo.packageName
        val activityName = info.name
        val label = info.label?.toString() ?: packageName
        val density = context.resources.displayMetrics.densityDpi
        val drawable: Drawable? = try {
            info.getBadgedIcon(density) ?: info.getIcon(density)
        } catch (e: Exception) {
            try {
                info.applicationInfo.loadIcon(packageManager)
            } catch (e2: Exception) {
                null
            }
        }

        val isSystem = (info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        val imageBitmap = drawable?.toSafeImageBitmap()

        return AppItem(
            id = "$packageName/$activityName",
            packageName = packageName,
            activityName = activityName,
            label = label,
            userHandle = profile,
            isSystemApp = isSystem,
            installTime = info.firstInstallTime,
            imageBitmap = imageBitmap
        )
    }

    private fun initializeDefaultDockIfEmpty(apps: List<AppItem>) {
        if (preferences.dockPackageNames.value.isEmpty() && apps.isNotEmpty()) {
            val prioritizedKeywords = listOf("dialer", "phone", "message", "sms", "chrome", "browser", "camera")
            val chosenPackages = mutableListOf<String>()

            for (keyword in prioritizedKeywords) {
                val match = apps.firstOrNull { app ->
                    (app.packageName.contains(keyword, ignoreCase = true) ||
                     app.label.contains(keyword, ignoreCase = true)) &&
                     !chosenPackages.contains(app.packageName)
                }
                if (match != null) {
                    chosenPackages.add(match.packageName)
                    if (chosenPackages.size >= LauncherPreferences.MAX_DOCK_ITEMS) break
                }
            }

            // Fill remaining slots if fewer than 4 found
            if (chosenPackages.size < 4) {
                for (app in apps) {
                    if (!chosenPackages.contains(app.packageName)) {
                        chosenPackages.add(app.packageName)
                        if (chosenPackages.size >= 4) break
                    }
                }
            }

            if (chosenPackages.isNotEmpty()) {
                preferences.setDockPackages(chosenPackages)
            }
        }
    }

    fun launchApp(app: AppItem): Result<Unit> {
        return try {
            val componentName = ComponentName(app.packageName, app.activityName)
            if (launcherApps != null) {
                launcherApps.startMainActivity(componentName, app.userHandle, null, null)
                Result.success(Unit)
            } else {
                val intent = packageManager.getLaunchIntentForPackage(app.packageName)?.apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                if (intent != null) {
                    context.startActivity(intent)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("No launch intent available"))
                }
            }
        } catch (e: Exception) {
            // Fallback attempt via packageManager launch intent
            try {
                val intent = packageManager.getLaunchIntentForPackage(app.packageName)?.apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                if (intent != null) {
                    context.startActivity(intent)
                    Result.success(Unit)
                } else {
                    Result.failure(e)
                }
            } catch (fallbackError: Exception) {
                Result.failure(fallbackError)
            }
        }
    }

    fun openAppInfo(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Ignore failure
        }
    }

    fun requestUninstall(packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Ignore failure
        }
    }

    fun openDefaultLauncherSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(RoleManager::class.java)
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
                if (!roleManager.isRoleHeld(RoleManager.ROLE_HOME)) {
                    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try {
                        context.startActivity(intent)
                        return
                    } catch (e: Exception) {
                        // Fall back to general settings
                    }
                }
            }
        }

        try {
            val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e2: Exception) {
                val intent = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        }
    }

    fun isDefaultLauncher(): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
            val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.resolveActivity(
                    intent,
                    PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
                )
            } else {
                packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            }
            resolveInfo?.activityInfo?.packageName == context.packageName
        } catch (e: Exception) {
            false
        }
    }

    private fun registerPackageListeners() {
        // Register LauncherApps.Callback
        launcherAppsCallback = object : LauncherApps.Callback() {
            override fun onPackageAdded(packageName: String, user: UserHandle) {
                loadInstalledApps()
            }

            override fun onPackageRemoved(packageName: String, user: UserHandle) {
                loadInstalledApps()
            }

            override fun onPackageChanged(packageName: String, user: UserHandle) {
                loadInstalledApps()
            }

            override fun onPackagesAvailable(packageNames: Array<out String>, user: UserHandle, replacing: Boolean) {
                loadInstalledApps()
            }

            override fun onPackagesUnavailable(packageNames: Array<out String>, user: UserHandle, replacing: Boolean) {
                loadInstalledApps()
            }
        }
        launcherApps?.registerCallback(launcherAppsCallback)

        // Also register broadcast receiver for package changes
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }

        packageBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                loadInstalledApps()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(packageBroadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(packageBroadcastReceiver, filter)
        }
    }

    fun cleanup() {
        launcherAppsCallback?.let {
            launcherApps?.unregisterCallback(it)
        }
        packageBroadcastReceiver?.let {
            try {
                context.unregisterReceiver(it)
            } catch (e: Exception) {
                // Ignore if not registered
            }
        }
    }
}

/**
 * Safely converts an Android Drawable to a Jetpack Compose ImageBitmap.
 */
fun Drawable.toSafeImageBitmap(): ImageBitmap? {
    return try {
        val width = intrinsicWidth.takeIf { it > 0 } ?: 128
        val height = intrinsicHeight.takeIf { it > 0 } ?: 128
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}

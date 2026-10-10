package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AccentOption
import com.example.data.AnimationSpeed
import com.example.data.AppItem
import com.example.data.AppRepository
import com.example.data.ClockAlignmentOption
import com.example.data.GestureAction
import com.example.data.GlassIntensityOption
import com.example.data.IconShapeOption
import com.example.data.IconSizeOption
import com.example.data.LauncherPreferences
import com.example.data.ParticleIntensity
import com.example.data.WallpaperBlurOption
import com.example.data.WallpaperOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LauncherUiState(
    val allApps: List<AppItem> = emptyList(),
    val filteredApps: List<AppItem> = emptyList(),
    val dockApps: List<AppItem> = emptyList(),
    val homeQuickApps: List<AppItem> = emptyList(),
    val searchQuery: String = "",
    val selectedLetterFilter: Char? = null,
    val isDrawerOpen: Boolean = false,
    val isSearchOpen: Boolean = false,
    val isStudioOpen: Boolean = false,
    val showStartupIntro: Boolean = false,
    val selectedAppForOptions: AppItem? = null,
    val isDefaultLauncher: Boolean = false,
    val gridColumns: Int = 4,
    val showClock: Boolean = true,
    val clockAlignment: ClockAlignmentOption = ClockAlignmentOption.CENTER,
    val accentOption: AccentOption = AccentOption.CYAN,
    val iconSizeOption: IconSizeOption = IconSizeOption.MEDIUM,
    val iconShapeOption: IconShapeOption = IconShapeOption.CIRCLE,
    val showAppLabels: Boolean = true,
    val minimalMode: Boolean = false,
    val glassIntensity: GlassIntensityOption = GlassIntensityOption.FROSTED,
    val glassShimmerEnabled: Boolean = true,
    val wallpaperOption: WallpaperOption = WallpaperOption.JELLYFISH,
    val wallpaperBlurOption: WallpaperBlurOption = WallpaperBlurOption.NONE,
    val customWallpaperUri: String? = null,
    val livingWallpaperEnabled: Boolean = true,
    val particleIntensity: ParticleIntensity = ParticleIntensity.AMBIENT,
    val animationSpeed: AnimationSpeed = AnimationSpeed.CALM,
    val swipeUpAction: GestureAction = GestureAction.SEARCH,
    val swipeDownAction: GestureAction = GestureAction.NOTIFICATIONS,
    val hapticFeedbackEnabled: Boolean = true,
    val showGestureBar: Boolean = true,
    val startupAnimationEnabled: Boolean = false,
    val lowPowerMode: Boolean = false,
    val reduceMotion: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = LauncherPreferences(application)
    private val repository = AppRepository(application, preferences, viewModelScope)

    private val _uiState = MutableStateFlow(
        LauncherUiState(
            gridColumns = preferences.gridColumns.value,
            showClock = preferences.showClock.value,
            clockAlignment = preferences.clockAlignment.value,
            accentOption = preferences.accentOption.value,
            iconSizeOption = preferences.iconSizeOption.value,
            iconShapeOption = preferences.iconShapeOption.value,
            showAppLabels = preferences.showAppLabels.value,
            minimalMode = preferences.minimalMode.value,
            glassIntensity = preferences.glassIntensity.value,
            glassShimmerEnabled = preferences.glassShimmerEnabled.value,
            wallpaperOption = preferences.wallpaperOption.value,
            wallpaperBlurOption = preferences.wallpaperBlurOption.value,
            customWallpaperUri = preferences.customWallpaperUri.value,
            livingWallpaperEnabled = preferences.livingWallpaperEnabled.value,
            particleIntensity = preferences.particleIntensity.value,
            animationSpeed = preferences.animationSpeed.value,
            swipeUpAction = preferences.swipeUpAction.value,
            swipeDownAction = preferences.swipeDownAction.value,
            hapticFeedbackEnabled = preferences.hapticFeedbackEnabled.value,
            showGestureBar = preferences.showGestureBar.value,
            startupAnimationEnabled = preferences.startupAnimationEnabled.value,
            showStartupIntro = preferences.startupAnimationEnabled.value,
            lowPowerMode = preferences.lowPowerMode.value,
            reduceMotion = preferences.reduceMotion.value,
            isDefaultLauncher = repository.isDefaultLauncher()
        )
    )
    val uiState: StateFlow<LauncherUiState> = _uiState.asStateFlow()

    init {
        // Collect installed apps
        viewModelScope.launch {
            repository.installedApps.collect { apps ->
                updateAppsList(apps)
            }
        }

        // Collect loading state
        viewModelScope.launch {
            repository.isLoading.collect { loading ->
                _uiState.update { it.copy(isLoading = loading) }
            }
        }

        // Collect dock changes
        viewModelScope.launch {
            preferences.dockPackageNames.collect { dockPackages ->
                val installed = _uiState.value.allApps
                val dockApps = dockPackages.mapNotNull { pkg ->
                    installed.firstOrNull { it.packageName == pkg }
                }
                val dockSet = dockPackages.toSet()
                val homeQuick = installed.filterNot { dockSet.contains(it.packageName) }.take(8)
                _uiState.update { it.copy(dockApps = dockApps, homeQuickApps = homeQuick) }
            }
        }
    }

    private fun updateAppsList(apps: List<AppItem>) {
        val current = _uiState.value
        val dockPackages = preferences.dockPackageNames.value
        val dockApps = dockPackages.mapNotNull { pkg ->
            apps.firstOrNull { it.packageName == pkg }
        }
        val dockSet = dockPackages.toSet()
        val homeQuick = apps.filterNot { dockSet.contains(it.packageName) }.take(8)

        val filtered = computeFilteredApps(apps, current.searchQuery, current.selectedLetterFilter)

        _uiState.update {
            it.copy(
                allApps = apps,
                filteredApps = filtered,
                dockApps = dockApps,
                homeQuickApps = homeQuick
            )
        }
    }

    private fun computeFilteredApps(
        apps: List<AppItem>,
        query: String,
        letter: Char?
    ): List<AppItem> {
        return apps.filter { app ->
            val matchesSearch = if (query.isBlank()) {
                true
            } else {
                app.label.contains(query, ignoreCase = true) ||
                app.packageName.contains(query, ignoreCase = true)
            }
            val matchesLetter = if (letter == null) {
                true
            } else {
                app.initial == letter
            }
            matchesSearch && matchesLetter
        }
    }

    fun onSearchQueryChange(query: String) {
        val newLetter = if (query.isNotBlank()) null else _uiState.value.selectedLetterFilter
        val filtered = computeFilteredApps(_uiState.value.allApps, query, newLetter)
        _uiState.update {
            it.copy(
                searchQuery = query,
                selectedLetterFilter = newLetter,
                filteredApps = filtered
            )
        }
    }

    fun onClearSearch() {
        val filtered = computeFilteredApps(_uiState.value.allApps, "", _uiState.value.selectedLetterFilter)
        _uiState.update {
            it.copy(
                searchQuery = "",
                filteredApps = filtered
            )
        }
    }

    fun onLetterFilterSelect(letter: Char?) {
        val activeLetter = if (_uiState.value.selectedLetterFilter == letter) null else letter
        val filtered = computeFilteredApps(_uiState.value.allApps, "", activeLetter)
        _uiState.update {
            it.copy(
                selectedLetterFilter = activeLetter,
                searchQuery = "",
                filteredApps = filtered
            )
        }
    }

    fun openSearch() {
        _uiState.update { it.copy(isSearchOpen = true) }
    }

    fun closeSearch() {
        val filtered = computeFilteredApps(_uiState.value.allApps, "", null)
        _uiState.update {
            it.copy(
                isSearchOpen = false,
                searchQuery = "",
                selectedLetterFilter = null,
                filteredApps = filtered
            )
        }
    }

    fun openDrawer(autoFocusSearch: Boolean = false) {
        _uiState.update { it.copy(isDrawerOpen = true) }
    }

    fun closeDrawer() {
        val filtered = computeFilteredApps(_uiState.value.allApps, "", null)
        _uiState.update {
            it.copy(
                isDrawerOpen = false,
                searchQuery = "",
                selectedLetterFilter = null,
                filteredApps = filtered
            )
        }
    }

    fun openStudio() {
        _uiState.update { it.copy(isStudioOpen = true) }
    }

    fun closeStudio() {
        _uiState.update { it.copy(isStudioOpen = false) }
    }

    fun finishStartupIntro() {
        _uiState.update { it.copy(showStartupIntro = false) }
    }

    fun onSwipeUp() {
        when (_uiState.value.swipeUpAction) {
            GestureAction.SEARCH -> openSearch()
            GestureAction.ALL_APPS -> openDrawer()
            else -> openSearch()
        }
    }

    fun onSwipeDown(context: Context) {
        when (_uiState.value.swipeDownAction) {
            GestureAction.NOTIFICATIONS -> expandNotificationShade(context)
            GestureAction.SEARCH -> openSearch()
            GestureAction.ALL_APPS -> openDrawer()
            GestureAction.NONE -> {}
        }
    }

    private fun expandNotificationShade(context: Context) {
        try {
            val statusBarService = context.getSystemService("statusbar")
            val statusBarManager = Class.forName("android.app.StatusBarManager")
            val expand = statusBarManager.getMethod("expandNotificationsPanel")
            expand.invoke(statusBarService)
        } catch (e: Exception) {
            try {
                val statusBarService = context.getSystemService("statusbar")
                val statusBarManager = Class.forName("android.app.StatusBarManager")
                val expand = statusBarManager.getMethod("expand")
                expand.invoke(statusBarService)
            } catch (e2: Exception) {
                openSearch()
            }
        }
    }

    fun showAppOptions(app: AppItem) {
        _uiState.update { it.copy(selectedAppForOptions = app) }
    }

    fun hideAppOptions() {
        _uiState.update { it.copy(selectedAppForOptions = null) }
    }

    fun launchApp(app: AppItem) {
        val result = repository.launchApp(app)
        if (result.isFailure) {
            _uiState.update { it.copy(errorMessage = "Unable to launch ${app.label}") }
        }
    }

    fun toggleDockPin(packageName: String) {
        preferences.toggleDockPin(packageName)
        hideAppOptions()
    }

    fun isAppInDock(app: AppItem): Boolean {
        return preferences.isPinnedToDock(app.packageName)
    }

    fun openAppInfo(app: AppItem) {
        hideAppOptions()
        repository.openAppInfo(app.packageName)
    }

    fun uninstallApp(app: AppItem) {
        hideAppOptions()
        repository.requestUninstall(app.packageName)
    }

    fun openDefaultHomeSettings() {
        repository.openDefaultLauncherSettings()
    }

    fun checkDefaultLauncherStatus() {
        _uiState.update { it.copy(isDefaultLauncher = repository.isDefaultLauncher()) }
    }

    fun setGridColumns(columns: Int) {
        preferences.setGridColumns(columns)
        _uiState.update { it.copy(gridColumns = columns) }
    }

    fun setShowClock(show: Boolean) {
        preferences.setShowClock(show)
        _uiState.update { it.copy(showClock = show) }
    }

    fun setClockAlignment(align: ClockAlignmentOption) {
        preferences.setClockAlignment(align)
        _uiState.update { it.copy(clockAlignment = align) }
    }

    fun setAccentOption(accent: AccentOption) {
        preferences.setAccentOption(accent)
        _uiState.update { it.copy(accentOption = accent) }
    }

    fun setIconSizeOption(size: IconSizeOption) {
        preferences.setIconSizeOption(size)
        _uiState.update { it.copy(iconSizeOption = size) }
    }

    fun setIconShapeOption(shape: IconShapeOption) {
        preferences.setIconShapeOption(shape)
        _uiState.update { it.copy(iconShapeOption = shape) }
    }

    fun setShowAppLabels(show: Boolean) {
        preferences.setShowAppLabels(show)
        _uiState.update { it.copy(showAppLabels = show) }
    }

    fun setMinimalMode(enabled: Boolean) {
        preferences.setMinimalMode(enabled)
        _uiState.update { it.copy(minimalMode = enabled) }
    }

    fun setGlassIntensity(intensity: GlassIntensityOption) {
        preferences.setGlassIntensity(intensity)
        _uiState.update { it.copy(glassIntensity = intensity) }
    }

    fun setGlassShimmerEnabled(enabled: Boolean) {
        preferences.setGlassShimmerEnabled(enabled)
        _uiState.update { it.copy(glassShimmerEnabled = enabled) }
    }

    fun setWallpaperOption(option: WallpaperOption) {
        preferences.setWallpaperOption(option)
        _uiState.update { it.copy(wallpaperOption = option) }
    }

    fun setWallpaperBlurOption(option: WallpaperBlurOption) {
        preferences.setWallpaperBlurOption(option)
        _uiState.update { it.copy(wallpaperBlurOption = option) }
    }

    fun setCustomWallpaperUri(uri: String?) {
        preferences.setCustomWallpaperUri(uri)
        _uiState.update {
            it.copy(
                customWallpaperUri = uri,
                wallpaperOption = if (uri != null) WallpaperOption.CUSTOM else it.wallpaperOption
            )
        }
    }

    fun setLivingWallpaperEnabled(enabled: Boolean) {
        preferences.setLivingWallpaperEnabled(enabled)
        _uiState.update { it.copy(livingWallpaperEnabled = enabled) }
    }

    fun setParticleIntensity(intensity: ParticleIntensity) {
        preferences.setParticleIntensity(intensity)
        _uiState.update { it.copy(particleIntensity = intensity) }
    }

    fun setAnimationSpeed(speed: AnimationSpeed) {
        preferences.setAnimationSpeed(speed)
        _uiState.update { it.copy(animationSpeed = speed) }
    }

    fun setSwipeUpAction(action: GestureAction) {
        preferences.setSwipeUpAction(action)
        _uiState.update { it.copy(swipeUpAction = action) }
    }

    fun setSwipeDownAction(action: GestureAction) {
        preferences.setSwipeDownAction(action)
        _uiState.update { it.copy(swipeDownAction = action) }
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        preferences.setHapticFeedbackEnabled(enabled)
        _uiState.update { it.copy(hapticFeedbackEnabled = enabled) }
    }

    fun setShowGestureBar(show: Boolean) {
        preferences.setShowGestureBar(show)
        _uiState.update { it.copy(showGestureBar = show) }
    }

    fun setStartupAnimationEnabled(enabled: Boolean) {
        preferences.setStartupAnimationEnabled(enabled)
        _uiState.update { it.copy(startupAnimationEnabled = enabled) }
    }

    fun setLowPowerMode(enabled: Boolean) {
        preferences.setLowPowerMode(enabled)
        _uiState.update { it.copy(lowPowerMode = enabled) }
    }

    fun setReduceMotion(enabled: Boolean) {
        preferences.setReduceMotion(enabled)
        _uiState.update { it.copy(reduceMotion = enabled) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onHomePressed() {
        val filtered = computeFilteredApps(_uiState.value.allApps, "", null)
        _uiState.update {
            it.copy(
                isStudioOpen = false,
                isSearchOpen = false,
                isDrawerOpen = false,
                selectedAppForOptions = null,
                searchQuery = "",
                selectedLetterFilter = null,
                filteredApps = filtered,
                isDefaultLauncher = repository.isDefaultLauncher()
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.cleanup()
    }
}

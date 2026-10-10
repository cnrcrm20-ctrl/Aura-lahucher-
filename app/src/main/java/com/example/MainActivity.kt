package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppDrawerScreen
import com.example.ui.AppOptionsSheet
import com.example.ui.AuraLogoAnimation
import com.example.ui.AuraSearchSheet
import com.example.ui.AuraStudioScreen
import com.example.ui.HomeScreen
import com.example.ui.LauncherViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val snackbarHostState = remember { SnackbarHostState() }
                val context = LocalContext.current

                // Display error snackbars
                LaunchedEffect(uiState.errorMessage) {
                    uiState.errorMessage?.let { error ->
                        snackbarHostState.showSnackbar(error)
                        viewModel.clearError()
                    }
                }

                // Handle Back button
                BackHandler(
                    enabled = uiState.isDrawerOpen ||
                              uiState.isSearchOpen ||
                              uiState.isStudioOpen ||
                              uiState.selectedAppForOptions != null
                ) {
                    when {
                        uiState.selectedAppForOptions != null -> viewModel.hideAppOptions()
                        uiState.isStudioOpen -> viewModel.closeStudio()
                        uiState.isSearchOpen -> viewModel.closeSearch()
                        uiState.isDrawerOpen -> viewModel.closeDrawer()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { _ ->
                    if (uiState.showStartupIntro) {
                        AuraLogoAnimation(
                            accentColor = uiState.accentOption.color,
                            isInteractivePreview = false,
                            onFinished = { viewModel.finishStartupIntro() }
                        )
                    } else if (uiState.isStudioOpen) {
                        AuraStudioScreen(
                            uiState = uiState,
                            allApps = uiState.allApps,
                            onBack = { viewModel.closeStudio() },
                            onAccentSelect = { viewModel.setAccentOption(it) },
                            onIconSizeSelect = { viewModel.setIconSizeOption(it) },
                            onIconShapeSelect = { viewModel.setIconShapeOption(it) },
                            onShowLabelsToggle = { viewModel.setShowAppLabels(it) },
                            onMinimalModeToggle = { viewModel.setMinimalMode(it) },
                            onGridColumnsSelect = { viewModel.setGridColumns(it) },
                            onGlassIntensitySelect = { viewModel.setGlassIntensity(it) },
                            onGlassShimmerToggle = { viewModel.setGlassShimmerEnabled(it) },
                            onClockAlignmentSelect = { viewModel.setClockAlignment(it) },
                            onShowClockToggle = { viewModel.setShowClock(it) },
                            onWallpaperSelect = { viewModel.setWallpaperOption(it) },
                            onWallpaperBlurSelect = { viewModel.setWallpaperBlurOption(it) },
                            onCustomWallpaperUriSelect = { viewModel.setCustomWallpaperUri(it) },
                            onLivingWallpaperToggle = { viewModel.setLivingWallpaperEnabled(it) },
                            onParticleIntensitySelect = { viewModel.setParticleIntensity(it) },
                            onAnimationSpeedSelect = { viewModel.setAnimationSpeed(it) },
                            onSwipeUpSelect = { viewModel.setSwipeUpAction(it) },
                            onSwipeDownSelect = { viewModel.setSwipeDownAction(it) },
                            onHapticFeedbackToggle = { viewModel.setHapticFeedbackEnabled(it) },
                            onShowGestureBarToggle = { viewModel.setShowGestureBar(it) },
                            onStartupAnimationToggle = { viewModel.setStartupAnimationEnabled(it) },
                            onLowPowerToggle = { viewModel.setLowPowerMode(it) },
                            onReduceMotionToggle = { viewModel.setReduceMotion(it) },
                            onToggleDockPackage = { viewModel.toggleDockPin(it) },
                            onSetDefaultLauncher = { viewModel.openDefaultHomeSettings() }
                        )
                    } else if (uiState.isSearchOpen) {
                        AuraSearchSheet(
                            query = uiState.searchQuery,
                            filteredApps = uiState.filteredApps,
                            allApps = uiState.allApps,
                            recentApps = uiState.homeQuickApps,
                            accentColor = uiState.accentOption.color,
                            glassIntensity = uiState.glassIntensity,
                            showAppLabels = uiState.showAppLabels,
                            gridColumns = uiState.gridColumns,
                            iconShape = uiState.iconShapeOption,
                            onQueryChange = { viewModel.onSearchQueryChange(it) },
                            onClearQuery = { viewModel.onClearSearch() },
                            onAppClick = { viewModel.launchApp(it) },
                            onAppLongClick = { viewModel.showAppOptions(it) },
                            onClose = { viewModel.closeSearch() }
                        )
                    } else if (uiState.isDrawerOpen) {
                        AppDrawerScreen(
                            uiState = uiState,
                            onBackToHome = { viewModel.closeDrawer() },
                            onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                            onClearSearch = { viewModel.onClearSearch() },
                            onLetterFilterSelect = { viewModel.onLetterFilterSelect(it) },
                            onAppClick = { viewModel.launchApp(it) },
                            onAppLongClick = { viewModel.showAppOptions(it) },
                            focusSearchOnOpen = false
                        )
                    } else {
                        HomeScreen(
                            uiState = uiState,
                            onOpenSearch = { viewModel.openSearch() },
                            onOpenStudio = { viewModel.openStudio() },
                            onSwipeUp = { viewModel.onSwipeUp() },
                            onSwipeDown = { viewModel.onSwipeDown(context) },
                            onAppClick = { viewModel.launchApp(it) },
                            onAppLongClick = { viewModel.showAppOptions(it) },
                            onSetDefaultLauncher = { viewModel.openDefaultHomeSettings() }
                        )
                    }

                    // App Options Bottom Sheet
                    uiState.selectedAppForOptions?.let { app ->
                        AppOptionsSheet(
                            app = app,
                            isPinnedInDock = viewModel.isAppInDock(app),
                            onDismiss = { viewModel.hideAppOptions() },
                            onOpenApp = {
                                viewModel.hideAppOptions()
                                viewModel.launchApp(app)
                            },
                            onToggleDockPin = { viewModel.toggleDockPin(app.packageName) },
                            onOpenAppInfo = { viewModel.openAppInfo(app) },
                            onUninstall = { viewModel.uninstallApp(app) }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        viewModel.onHomePressed()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkDefaultLauncherStatus()
    }
}

package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LauncherPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    // Dock
    private val _dockPackageNames = MutableStateFlow(loadDockPackages())
    val dockPackageNames: StateFlow<List<String>> = _dockPackageNames.asStateFlow()

    // Grid Columns
    private val _gridColumns = MutableStateFlow(prefs.getInt(KEY_GRID_COLUMNS, 4))
    val gridColumns: StateFlow<Int> = _gridColumns.asStateFlow()

    // Clock
    private val _showClock = MutableStateFlow(prefs.getBoolean(KEY_SHOW_CLOCK, true))
    val showClock: StateFlow<Boolean> = _showClock.asStateFlow()

    private val _clockAlignment = MutableStateFlow(
        runCatching { ClockAlignmentOption.valueOf(prefs.getString(KEY_CLOCK_ALIGNMENT, ClockAlignmentOption.CENTER.name)!!) }
            .getOrDefault(ClockAlignmentOption.CENTER)
    )
    val clockAlignment: StateFlow<ClockAlignmentOption> = _clockAlignment.asStateFlow()

    // Appearance
    private val _accentOption = MutableStateFlow(
        runCatching { AccentOption.valueOf(prefs.getString(KEY_ACCENT, AccentOption.CYAN.name)!!) }
            .getOrDefault(AccentOption.CYAN)
    )
    val accentOption: StateFlow<AccentOption> = _accentOption.asStateFlow()

    private val _iconSizeOption = MutableStateFlow(
        runCatching { IconSizeOption.valueOf(prefs.getString(KEY_ICON_SIZE, IconSizeOption.MEDIUM.name)!!) }
            .getOrDefault(IconSizeOption.MEDIUM)
    )
    val iconSizeOption: StateFlow<IconSizeOption> = _iconSizeOption.asStateFlow()

    private val _iconShapeOption = MutableStateFlow(
        runCatching { IconShapeOption.valueOf(prefs.getString(KEY_ICON_SHAPE, IconShapeOption.CIRCLE.name)!!) }
            .getOrDefault(IconShapeOption.CIRCLE)
    )
    val iconShapeOption: StateFlow<IconShapeOption> = _iconShapeOption.asStateFlow()

    private val _showAppLabels = MutableStateFlow(prefs.getBoolean(KEY_SHOW_APP_LABELS, true))
    val showAppLabels: StateFlow<Boolean> = _showAppLabels.asStateFlow()

    private val _minimalMode = MutableStateFlow(prefs.getBoolean(KEY_MINIMAL_MODE, false))
    val minimalMode: StateFlow<Boolean> = _minimalMode.asStateFlow()

    private val _glassIntensity = MutableStateFlow(
        runCatching { GlassIntensityOption.valueOf(prefs.getString(KEY_GLASS_INTENSITY, GlassIntensityOption.FROSTED.name)!!) }
            .getOrDefault(GlassIntensityOption.FROSTED)
    )
    val glassIntensity: StateFlow<GlassIntensityOption> = _glassIntensity.asStateFlow()

    private val _glassShimmerEnabled = MutableStateFlow(prefs.getBoolean(KEY_GLASS_SHIMMER, true))
    val glassShimmerEnabled: StateFlow<Boolean> = _glassShimmerEnabled.asStateFlow()

    // Wallpaper & Living Motion
    private val _wallpaperOption = MutableStateFlow(
        runCatching { WallpaperOption.valueOf(prefs.getString(KEY_WALLPAPER_OPTION, WallpaperOption.JELLYFISH.name)!!) }
            .getOrDefault(WallpaperOption.JELLYFISH)
    )
    val wallpaperOption: StateFlow<WallpaperOption> = _wallpaperOption.asStateFlow()

    private val _wallpaperBlurOption = MutableStateFlow(
        runCatching { WallpaperBlurOption.valueOf(prefs.getString(KEY_WALLPAPER_BLUR, WallpaperBlurOption.NONE.name)!!) }
            .getOrDefault(WallpaperBlurOption.NONE)
    )
    val wallpaperBlurOption: StateFlow<WallpaperBlurOption> = _wallpaperBlurOption.asStateFlow()

    private val _customWallpaperUri = MutableStateFlow(prefs.getString(KEY_CUSTOM_WALLPAPER_URI, null))
    val customWallpaperUri: StateFlow<String?> = _customWallpaperUri.asStateFlow()

    private val _livingWallpaperEnabled = MutableStateFlow(prefs.getBoolean(KEY_LIVING_WALLPAPER_ENABLED, true))
    val livingWallpaperEnabled: StateFlow<Boolean> = _livingWallpaperEnabled.asStateFlow()

    private val _particleIntensity = MutableStateFlow(
        runCatching { ParticleIntensity.valueOf(prefs.getString(KEY_PARTICLE_INTENSITY, ParticleIntensity.AMBIENT.name)!!) }
            .getOrDefault(ParticleIntensity.AMBIENT)
    )
    val particleIntensity: StateFlow<ParticleIntensity> = _particleIntensity.asStateFlow()

    private val _animationSpeed = MutableStateFlow(
        runCatching { AnimationSpeed.valueOf(prefs.getString(KEY_ANIMATION_SPEED, AnimationSpeed.CALM.name)!!) }
            .getOrDefault(AnimationSpeed.CALM)
    )
    val animationSpeed: StateFlow<AnimationSpeed> = _animationSpeed.asStateFlow()

    // Gestures & Haptics
    private val _swipeUpAction = MutableStateFlow(
        runCatching { GestureAction.valueOf(prefs.getString(KEY_SWIPE_UP, GestureAction.SEARCH.name)!!) }
            .getOrDefault(GestureAction.SEARCH)
    )
    val swipeUpAction: StateFlow<GestureAction> = _swipeUpAction.asStateFlow()

    private val _swipeDownAction = MutableStateFlow(
        runCatching { GestureAction.valueOf(prefs.getString(KEY_SWIPE_DOWN, GestureAction.NOTIFICATIONS.name)!!) }
            .getOrDefault(GestureAction.NOTIFICATIONS)
    )
    val swipeDownAction: StateFlow<GestureAction> = _swipeDownAction.asStateFlow()

    private val _hapticFeedbackEnabled = MutableStateFlow(prefs.getBoolean(KEY_HAPTIC_FEEDBACK, true))
    val hapticFeedbackEnabled: StateFlow<Boolean> = _hapticFeedbackEnabled.asStateFlow()

    private val _showGestureBar = MutableStateFlow(prefs.getBoolean(KEY_SHOW_GESTURE_BAR, true))
    val showGestureBar: StateFlow<Boolean> = _showGestureBar.asStateFlow()

    // Aura Identity & Startup
    private val _startupAnimationEnabled = MutableStateFlow(prefs.getBoolean(KEY_STARTUP_ANIMATION, false))
    val startupAnimationEnabled: StateFlow<Boolean> = _startupAnimationEnabled.asStateFlow()

    // Performance
    private val _lowPowerMode = MutableStateFlow(prefs.getBoolean(KEY_LOW_POWER_MODE, false))
    val lowPowerMode: StateFlow<Boolean> = _lowPowerMode.asStateFlow()

    private val _reduceMotion = MutableStateFlow(prefs.getBoolean(KEY_REDUCE_MOTION, false))
    val reduceMotion: StateFlow<Boolean> = _reduceMotion.asStateFlow()

    private fun loadDockPackages(): List<String> {
        val raw = prefs.getString(KEY_DOCK_PACKAGES, null)
        return if (!raw.isNullOrBlank()) {
            raw.split(",").filter { it.isNotBlank() }
        } else {
            emptyList()
        }
    }

    fun setDockPackages(packages: List<String>) {
        val serialized = packages.take(MAX_DOCK_ITEMS).joinToString(",")
        prefs.edit().putString(KEY_DOCK_PACKAGES, serialized).apply()
        _dockPackageNames.value = packages.take(MAX_DOCK_ITEMS)
    }

    fun toggleDockPin(packageName: String): Boolean {
        val current = _dockPackageNames.value.toMutableList()
        val isNowPinned = if (current.contains(packageName)) {
            current.remove(packageName)
            false
        } else {
            if (current.size < MAX_DOCK_ITEMS) {
                current.add(packageName)
                true
            } else {
                return false
            }
        }
        setDockPackages(current)
        return isNowPinned
    }

    fun isPinnedToDock(packageName: String): Boolean {
        return _dockPackageNames.value.contains(packageName)
    }

    fun setGridColumns(columns: Int) {
        val clamped = columns.coerceIn(3, 6)
        prefs.edit().putInt(KEY_GRID_COLUMNS, clamped).apply()
        _gridColumns.value = clamped
    }

    fun setShowClock(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_CLOCK, show).apply()
        _showClock.value = show
    }

    fun setClockAlignment(option: ClockAlignmentOption) {
        prefs.edit().putString(KEY_CLOCK_ALIGNMENT, option.name).apply()
        _clockAlignment.value = option
    }

    fun setAccentOption(option: AccentOption) {
        prefs.edit().putString(KEY_ACCENT, option.name).apply()
        _accentOption.value = option
    }

    fun setIconSizeOption(option: IconSizeOption) {
        prefs.edit().putString(KEY_ICON_SIZE, option.name).apply()
        _iconSizeOption.value = option
    }

    fun setIconShapeOption(option: IconShapeOption) {
        prefs.edit().putString(KEY_ICON_SHAPE, option.name).apply()
        _iconShapeOption.value = option
    }

    fun setShowAppLabels(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_APP_LABELS, show).apply()
        _showAppLabels.value = show
    }

    fun setMinimalMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MINIMAL_MODE, enabled).apply()
        _minimalMode.value = enabled
    }

    fun setGlassIntensity(intensity: GlassIntensityOption) {
        prefs.edit().putString(KEY_GLASS_INTENSITY, intensity.name).apply()
        _glassIntensity.value = intensity
    }

    fun setGlassShimmerEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_GLASS_SHIMMER, enabled).apply()
        _glassShimmerEnabled.value = enabled
    }

    fun setWallpaperOption(option: WallpaperOption) {
        prefs.edit().putString(KEY_WALLPAPER_OPTION, option.name).apply()
        _wallpaperOption.value = option
    }

    fun setWallpaperBlurOption(option: WallpaperBlurOption) {
        prefs.edit().putString(KEY_WALLPAPER_BLUR, option.name).apply()
        _wallpaperBlurOption.value = option
    }

    fun setCustomWallpaperUri(uri: String?) {
        prefs.edit().putString(KEY_CUSTOM_WALLPAPER_URI, uri).apply()
        _customWallpaperUri.value = uri
        if (uri != null) {
            setWallpaperOption(WallpaperOption.CUSTOM)
        }
    }

    fun setLivingWallpaperEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LIVING_WALLPAPER_ENABLED, enabled).apply()
        _livingWallpaperEnabled.value = enabled
    }

    fun setParticleIntensity(intensity: ParticleIntensity) {
        prefs.edit().putString(KEY_PARTICLE_INTENSITY, intensity.name).apply()
        _particleIntensity.value = intensity
    }

    fun setAnimationSpeed(speed: AnimationSpeed) {
        prefs.edit().putString(KEY_ANIMATION_SPEED, speed.name).apply()
        _animationSpeed.value = speed
    }

    fun setSwipeUpAction(action: GestureAction) {
        prefs.edit().putString(KEY_SWIPE_UP, action.name).apply()
        _swipeUpAction.value = action
    }

    fun setSwipeDownAction(action: GestureAction) {
        prefs.edit().putString(KEY_SWIPE_DOWN, action.name).apply()
        _swipeDownAction.value = action
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC_FEEDBACK, enabled).apply()
        _hapticFeedbackEnabled.value = enabled
    }

    fun setShowGestureBar(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_GESTURE_BAR, show).apply()
        _showGestureBar.value = show
    }

    fun setStartupAnimationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_STARTUP_ANIMATION, enabled).apply()
        _startupAnimationEnabled.value = enabled
    }

    fun setLowPowerMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LOW_POWER_MODE, enabled).apply()
        _lowPowerMode.value = enabled
    }

    fun setReduceMotion(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_REDUCE_MOTION, enabled).apply()
        _reduceMotion.value = enabled
    }

    companion object {
        private const val PREFS_NAME = "aura_launcher_preferences"
        private const val KEY_DOCK_PACKAGES = "dock_packages"
        private const val KEY_GRID_COLUMNS = "grid_columns"
        private const val KEY_SHOW_CLOCK = "show_clock"
        private const val KEY_CLOCK_ALIGNMENT = "clock_alignment"
        private const val KEY_ACCENT = "accent_color"
        private const val KEY_ICON_SIZE = "icon_size"
        private const val KEY_ICON_SHAPE = "icon_shape"
        private const val KEY_SHOW_APP_LABELS = "show_app_labels"
        private const val KEY_MINIMAL_MODE = "minimal_mode"
        private const val KEY_GLASS_INTENSITY = "glass_intensity"
        private const val KEY_GLASS_SHIMMER = "glass_shimmer"
        private const val KEY_WALLPAPER_OPTION = "wallpaper_option"
        private const val KEY_WALLPAPER_BLUR = "wallpaper_blur"
        private const val KEY_CUSTOM_WALLPAPER_URI = "custom_wallpaper_uri"
        private const val KEY_LIVING_WALLPAPER_ENABLED = "living_wallpaper_enabled"
        private const val KEY_PARTICLE_INTENSITY = "particle_intensity"
        private const val KEY_ANIMATION_SPEED = "animation_speed"
        private const val KEY_SWIPE_UP = "swipe_up_action"
        private const val KEY_SWIPE_DOWN = "swipe_down_action"
        private const val KEY_HAPTIC_FEEDBACK = "haptic_feedback"
        private const val KEY_SHOW_GESTURE_BAR = "show_gesture_bar"
        private const val KEY_STARTUP_ANIMATION = "startup_animation"
        private const val KEY_LOW_POWER_MODE = "low_power_mode"
        private const val KEY_REDUCE_MOTION = "reduce_motion"
        const val MAX_DOCK_ITEMS = 5
    }
}

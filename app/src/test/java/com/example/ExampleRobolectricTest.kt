package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AccentOption
import com.example.data.AnimationSpeed
import com.example.data.IconShapeOption
import com.example.data.LauncherPreferences
import com.example.data.ParticleIntensity
import com.example.data.WallpaperBlurOption
import com.example.data.WallpaperOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Aura", appName)
    }

    @Test
    fun `launcher preferences dock management`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val preferences = LauncherPreferences(context)

        assertFalse(preferences.isPinnedToDock("com.example.testapp"))
        val pinned = preferences.toggleDockPin("com.example.testapp")
        assertTrue(pinned)
        assertTrue(preferences.isPinnedToDock("com.example.testapp"))

        val unpinned = preferences.toggleDockPin("com.example.testapp")
        assertFalse(unpinned)
        assertFalse(preferences.isPinnedToDock("com.example.testapp"))
    }

    @Test
    fun `launcher preferences phase 2 personalization hub`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val preferences = LauncherPreferences(context)

        // Test Accent
        preferences.setAccentOption(AccentOption.EMERALD)
        assertEquals(AccentOption.EMERALD, preferences.accentOption.value)

        // Test Wallpaper
        preferences.setWallpaperOption(WallpaperOption.AMOLED)
        assertEquals(WallpaperOption.AMOLED, preferences.wallpaperOption.value)

        // Test Living Wallpaper & Particles
        preferences.setLivingWallpaperEnabled(true)
        assertTrue(preferences.livingWallpaperEnabled.value)
        preferences.setParticleIntensity(ParticleIntensity.LUSH)
        assertEquals(ParticleIntensity.LUSH, preferences.particleIntensity.value)
        preferences.setAnimationSpeed(AnimationSpeed.SLOW)
        assertEquals(AnimationSpeed.SLOW, preferences.animationSpeed.value)

        // Test Minimal Mode
        preferences.setMinimalMode(true)
        assertTrue(preferences.minimalMode.value)
    }

    @Test
    fun `launcher preferences phase 3 glass and system experience`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val preferences = LauncherPreferences(context)

        // Test Icon Shape
        preferences.setIconShapeOption(IconShapeOption.SQUIRCLE)
        assertEquals(IconShapeOption.SQUIRCLE, preferences.iconShapeOption.value)

        // Test Wallpaper Blur
        preferences.setWallpaperBlurOption(WallpaperBlurOption.SOFT)
        assertEquals(WallpaperBlurOption.SOFT, preferences.wallpaperBlurOption.value)

        // Test Haptic Feedback
        preferences.setHapticFeedbackEnabled(false)
        assertFalse(preferences.hapticFeedbackEnabled.value)

        // Test Gesture Bar Indicator
        preferences.setShowGestureBar(true)
        assertTrue(preferences.showGestureBar.value)

        // Test Glass Shimmer
        preferences.setGlassShimmerEnabled(true)
        assertTrue(preferences.glassShimmerEnabled.value)
    }
}

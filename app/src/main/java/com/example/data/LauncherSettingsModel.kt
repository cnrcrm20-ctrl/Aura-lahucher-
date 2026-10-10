package com.example.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AccentOption(val label: String, val color: Color) {
    CYAN("Aura Cyan", Color(0xFF38BDF8)),
    AQUA("Electric Aqua", Color(0xFF22D3EE)),
    INDIGO("Abyssal Indigo", Color(0xFF818CF8)),
    EMERALD("Bioluminescent Emerald", Color(0xFF34D399)),
    CORAL("Deep Sea Coral", Color(0xFFFB7185))
}

enum class WallpaperOption(val label: String, val description: String) {
    JELLYFISH("Bioluminescent Jellyfish", "Reference ocean creature with living particle glow"),
    ABYSSAL("Deep Ocean Abyssal", "Minimal dark ocean gradient with ambient particles"),
    AMOLED("AMOLED Obsidian", "Pure black energy-saving canvas with subtle dust motes"),
    CUSTOM("Custom Photo", "Select an image from device gallery")
}

enum class ParticleIntensity(val label: String, val count: Int) {
    OFF("Off", 0),
    SUBTLE("Subtle (15)", 15),
    AMBIENT("Ambient (30)", 30),
    LUSH("Lush (50)", 50)
}

enum class AnimationSpeed(val label: String, val multiplier: Float) {
    SLOW("Gentle Drift", 0.6f),
    CALM("Calm Fluid", 1.0f),
    NORMAL("Normal", 1.4f)
}

enum class IconSizeOption(val label: String, val dpSize: Dp) {
    SMALL("Small (44dp)", 44.dp),
    MEDIUM("Medium (52dp)", 52.dp),
    LARGE("Large (60dp)", 60.dp)
}

enum class IconShapeOption(val label: String, val cornerRadiusPercent: Int) {
    CIRCLE("Circle", 50),
    SQUIRCLE("Squircle", 28),
    ROUNDED_SQUARE("Rounded", 18)
}

enum class WallpaperBlurOption(val label: String, val blurDp: Dp) {
    NONE("Sharp / None", 0.dp),
    SOFT("Soft Glass (8dp)", 8.dp),
    DEEP("Deep Glass (16dp)", 16.dp)
}

enum class ClockAlignmentOption(val label: String) {
    CENTER("Center Aligned"),
    LEFT("Left Aligned"),
    MINIMAL("Minimal Inline")
}

enum class GlassIntensityOption(val label: String, val alpha: Float, val borderAlpha: Float) {
    SUBTLE("Subtle Glass", 0.35f, 0.12f),
    FROSTED("Frosted Glass", 0.60f, 0.20f),
    DEEP("Deep Abyssal Glass", 0.85f, 0.30f)
}

enum class GestureAction(val label: String) {
    SEARCH("Open Aura Search"),
    NOTIFICATIONS("Expand Notification Shade"),
    ALL_APPS("Browse All Apps"),
    NONE("None")
}

package com.example.ui

import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.example.R
import com.example.data.AnimationSpeed
import com.example.data.ParticleIntensity
import com.example.data.WallpaperBlurOption
import com.example.data.WallpaperOption
import kotlin.math.sin
import kotlin.random.Random

private class LivingParticle(
    var x: Float,
    var y: Float,
    val radius: Float,
    val speed: Float,
    val baseAlpha: Float,
    var alpha: Float,
    val swayOffset: Float,
    val swaySpeed: Float
)

@Composable
fun LivingWallpaper(
    wallpaperOption: WallpaperOption,
    customWallpaperUri: String?,
    livingEnabled: Boolean,
    particleIntensity: ParticleIntensity,
    animationSpeed: AnimationSpeed,
    accentColor: Color,
    lowPowerMode: Boolean,
    reduceMotion: Boolean,
    wallpaperBlurOption: WallpaperBlurOption = WallpaperBlurOption.NONE,
    modifier: Modifier = Modifier
) {
    val imageModifier = if (wallpaperBlurOption != WallpaperBlurOption.NONE) {
        Modifier
            .fillMaxSize()
            .blur(wallpaperBlurOption.blurDp)
    } else {
        Modifier.fillMaxSize()
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Base Wallpaper Image
        when (wallpaperOption) {
            WallpaperOption.JELLYFISH -> {
                Image(
                    painter = painterResource(id = R.drawable.wp_jellyfish_ocean),
                    contentDescription = "Jellyfish Ocean Wallpaper",
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier
                )
            }
            WallpaperOption.ABYSSAL -> {
                Image(
                    painter = painterResource(id = R.drawable.wp_abyssal_deep),
                    contentDescription = "Deep Abyssal Ocean Wallpaper",
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier
                )
            }
            WallpaperOption.AMOLED -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF03070D),
                                    Color(0xFF010204),
                                    Color(0xFF000000)
                                )
                            )
                        )
                )
            }
            WallpaperOption.CUSTOM -> {
                if (!customWallpaperUri.isNullOrBlank()) {
                    AsyncImage(
                        model = Uri.parse(customWallpaperUri),
                        contentDescription = "Custom Wallpaper",
                        contentScale = ContentScale.Crop,
                        modifier = imageModifier
                    )
                } else {
                    // Fallback to jellyfish
                    Image(
                        painter = painterResource(id = R.drawable.wp_jellyfish_ocean),
                        contentDescription = "Fallback Wallpaper",
                        contentScale = ContentScale.Crop,
                        modifier = imageModifier
                    )
                }
            }
        }

        // 2. Subtle Dark Ocean Vignette (Ensures UI & Icon Readability)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x7303070E), // Top subtle darkening for clock
                            Color(0x3303070E), // Mid clarity for jellyfish
                            Color(0x8C020408), // Bottom darkening for dock
                            Color(0xB3000000)  // Navigation safe area
                        )
                    )
                )
        )

        // 3. Living Underwater Particles & Ambient Caustics
        val shouldAnimate = livingEnabled &&
                !lowPowerMode &&
                !reduceMotion &&
                particleIntensity != ParticleIntensity.OFF

        if (shouldAnimate) {
            LivingParticleCanvas(
                particleCount = particleIntensity.count,
                speedMultiplier = animationSpeed.multiplier,
                accentColor = accentColor
            )
        }
    }
}

@Composable
private fun LivingParticleCanvas(
    particleCount: Int,
    speedMultiplier: Float,
    accentColor: Color
) {
    val particles = remember(particleCount) {
        mutableStateListOf<LivingParticle>().apply {
            val random = Random(42)
            repeat(particleCount) {
                add(
                    LivingParticle(
                        x = random.nextFloat(),
                        y = random.nextFloat(),
                        radius = 1.0f + random.nextFloat() * 2.2f,
                        speed = (0.0003f + random.nextFloat() * 0.0007f),
                        baseAlpha = 0.25f + random.nextFloat() * 0.55f,
                        alpha = 0.3f,
                        swayOffset = random.nextFloat() * 6.28f,
                        swaySpeed = 0.5f + random.nextFloat() * 1.5f
                    )
                )
            }
        }
    }

    // Slow ambient breathing pulse
    val infiniteTransition = rememberInfiniteTransition(label = "AmbientBreathing")
    val ambientPulse by infiniteTransition.animateFloat(
        initialValue = 0.02f,
        targetValue = 0.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    // Frame-based smooth movement loop
    LaunchedEffect(speedMultiplier, particleCount) {
        var lastTime = 0L
        while (true) {
            withFrameNanos { timeNanos ->
                if (lastTime == 0L) {
                    lastTime = timeNanos
                    return@withFrameNanos
                }
                val delta = (timeNanos - lastTime) / 1_000_000_000f
                lastTime = timeNanos

                val timeSec = timeNanos / 1_000_000_000.0

                for (p in particles) {
                    p.y -= (p.speed * speedMultiplier * delta * 60f)
                    p.x += (sin((timeSec + p.swayOffset) * p.swaySpeed).toFloat() * 0.0004f * speedMultiplier)

                    // Recycle particle if it drifts above top of screen
                    if (p.y < -0.05f) {
                        p.y = 1.05f
                        p.x = Random.nextFloat()
                    }
                    if (p.x < 0f) p.x = 1f
                    if (p.x > 1f) p.x = 0f

                    // Subtle twinkling
                    p.alpha = (p.baseAlpha * (0.7f + 0.3f * sin(timeSec * 2.0 + p.swayOffset).toFloat()))
                        .coerceIn(0.1f, 0.85f)
                }
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Subtle ambient light bloom near the center-top
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accentColor.copy(alpha = ambientPulse),
                    Color.Transparent
                ),
                center = Offset(width * 0.5f, height * 0.35f),
                radius = width * 0.8f
            ),
            radius = width * 0.8f,
            center = Offset(width * 0.5f, height * 0.35f)
        )

        // Draw particles
        for (p in particles) {
            val px = p.x * width
            val py = p.y * height
            val pColor = accentColor.copy(alpha = p.alpha)

            // Outer soft glow halo
            drawCircle(
                color = pColor.copy(alpha = p.alpha * 0.35f),
                radius = p.radius * 2.5f,
                center = Offset(px, py)
            )

            // Core bright particle
            drawCircle(
                color = pColor,
                radius = p.radius,
                center = Offset(px, py)
            )
        }
    }
}

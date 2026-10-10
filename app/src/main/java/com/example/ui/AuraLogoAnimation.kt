package com.example.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuroraCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AuraLogoAnimation(
    accentColor: Color = AuroraCyan,
    isInteractivePreview: Boolean = false,
    onFinished: () -> Unit = {}
) {
    val ringRotation = remember { Animatable(0f) }
    val ringAlpha = remember { Animatable(0f) }
    val rippleScale = remember { Animatable(0.7f) }
    val rippleAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val containerAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // 1. Fade in ring
        launch {
            ringAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
        }

        // 2. Smooth ring rotation
        launch {
            if (isInteractivePreview) {
                ringRotation.animateTo(
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(4000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
            } else {
                ringRotation.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(2200, easing = FastOutSlowInEasing)
                )
            }
        }

        // 3. Expanding ripple
        launch {
            delay(300)
            rippleAlpha.animateTo(0.5f, tween(300))
            launch {
                rippleScale.animateTo(1.4f, tween(1200, easing = FastOutSlowInEasing))
            }
            rippleAlpha.animateTo(0f, tween(900, easing = FastOutSlowInEasing))
        }

        // 4. Reveal Aura text
        launch {
            delay(500)
            textAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
        }

        // 5. If full startup screen, transition into home
        if (!isInteractivePreview) {
            delay(2000)
            containerAlpha.animateTo(0f, tween(400, easing = FastOutSlowInEasing))
            onFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF04070D).copy(alpha = containerAlpha.value))
            .clickable(enabled = !isInteractivePreview) { onFinished() }
            .testTag("aura_logo_intro"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Software-rendered Aura Ring Canvas
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val baseRadius = size.width * 0.32f

                    // Concentric expanding subtle ripple
                    if (rippleAlpha.value > 0f) {
                        drawCircle(
                            color = accentColor.copy(alpha = rippleAlpha.value * 0.35f),
                            radius = baseRadius * rippleScale.value,
                            center = center,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }

                    // Rotating glowing Aura Ring
                    rotate(ringRotation.value, pivot = center) {
                        // Soft halo ring
                        drawCircle(
                            color = accentColor.copy(alpha = ringAlpha.value * 0.15f),
                            radius = baseRadius,
                            center = center,
                            style = Stroke(width = 6.dp.toPx())
                        )

                        // Thin crisp celestial ring with gradient
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = ringAlpha.value * 0.1f),
                                    accentColor.copy(alpha = ringAlpha.value),
                                    accentColor.copy(alpha = ringAlpha.value * 0.3f),
                                    accentColor.copy(alpha = ringAlpha.value * 0.1f)
                                ),
                                center = center
                            ),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = Offset(center.x - baseRadius, center.y - baseRadius),
                            size = androidx.compose.ui.geometry.Size(baseRadius * 2, baseRadius * 2),
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Ambient light point on the ring
                        val angleRad = Math.toRadians(0.0)
                        val dotX = center.x + (baseRadius * kotlin.math.cos(angleRad)).toFloat()
                        val dotY = center.y + (baseRadius * kotlin.math.sin(angleRad)).toFloat()
                        drawCircle(
                            color = accentColor.copy(alpha = ringAlpha.value),
                            radius = 3.dp.toPx(),
                            center = Offset(dotX, dotY)
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = ringAlpha.value),
                            radius = 1.5.dp.toPx(),
                            center = Offset(dotX, dotY)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Typographic reveal
            Text(
                text = "A U R A",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary.copy(alpha = textAlpha.value),
                fontWeight = FontWeight.Light,
                letterSpacing = 10.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "L A U N C H E R",
                style = MaterialTheme.typography.labelSmall,
                color = accentColor.copy(alpha = textAlpha.value * 0.8f),
                fontWeight = FontWeight.Medium,
                letterSpacing = 6.sp
            )
        }

        // Skip prompt for startup
        if (!isInteractivePreview) {
            Surface(
                onClick = onFinished,
                shape = RoundedCornerShape(16.dp),
                color = Color(0x33FFFFFF),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Tap anywhere to skip",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppItem
import com.example.data.ClockAlignmentOption
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    uiState: LauncherUiState,
    onOpenSearch: () -> Unit,
    onOpenStudio: () -> Unit,
    onSwipeUp: () -> Unit,
    onSwipeDown: () -> Unit,
    onAppClick: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit,
    onSetDefaultLauncher: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var currentTime by remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000L)
        }
    }

    val timeFormatter = remember { SimpleDateFormat("hh:mm", Locale.getDefault()) }
    val amPmFormatter = remember { SimpleDateFormat("a", Locale.getDefault()) }
    val dateFormatter = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()) }

    val accentColor = uiState.accentOption.color
    val iconSize = uiState.iconSizeOption.dpSize
    val iconShape = uiState.iconShapeOption

    // Glass Styling values
    val glassBg = Color(0xFF090E17).copy(alpha = uiState.glassIntensity.alpha)
    val glassBorder = Color.White.copy(alpha = uiState.glassIntensity.borderAlpha)

    // Subtle glass shimmer transition
    val shimmerTransition = rememberInfiniteTransition(label = "GlassShimmer")
    val shimmerOffset by shimmerTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Shimmer"
    )

    // Gesture drag tracking
    var verticalDragAccumulator by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { verticalDragAccumulator = 0f },
                    onDragEnd = {
                        if (verticalDragAccumulator < -40f) {
                            if (uiState.hapticFeedbackEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            onSwipeUp()
                        } else if (verticalDragAccumulator > 40f) {
                            if (uiState.hapticFeedbackEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            onSwipeDown()
                        }
                        verticalDragAccumulator = 0f
                    },
                    onVerticalDrag = { _, dragAmount ->
                        verticalDragAccumulator += dragAmount
                    }
                )
            }
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    if (uiState.hapticFeedbackEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    onOpenStudio()
                }
            )
    ) {
        // 1. Living Ocean Wallpaper Background Layer
        LivingWallpaper(
            wallpaperOption = uiState.wallpaperOption,
            customWallpaperUri = uiState.customWallpaperUri,
            livingEnabled = uiState.livingWallpaperEnabled,
            particleIntensity = uiState.particleIntensity,
            animationSpeed = uiState.animationSpeed,
            accentColor = accentColor,
            lowPowerMode = uiState.lowPowerMode,
            reduceMotion = uiState.reduceMotion,
            wallpaperBlurOption = uiState.wallpaperBlurOption
        )

        // 2. Foreground Minimal UI Layer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Default Home indicator and Studio shortcut
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!uiState.isDefaultLauncher) {
                    Surface(
                        onClick = onSetDefaultLauncher,
                        shape = RoundedCornerShape(20.dp),
                        color = glassBg,
                        border = BorderStroke(1.dp, glassBorder),
                        modifier = Modifier.testTag("default_launcher_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(accentColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Set Default Home",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                IconButton(
                    onClick = {
                        if (uiState.hapticFeedbackEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        onOpenStudio()
                    },
                    modifier = Modifier.testTag("btn_aura_studio")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Aura Studio",
                        tint = TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Minimalist Clock & Date Widget
            if (uiState.showClock) {
                Spacer(modifier = Modifier.height(28.dp))
                ClockWidgetView(
                    currentTime = currentTime,
                    timeFormatter = timeFormatter,
                    amPmFormatter = amPmFormatter,
                    dateFormatter = dateFormatter,
                    alignment = uiState.clockAlignment,
                    accentColor = accentColor
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Floating Glass Search Capsule
            Surface(
                onClick = {
                    if (uiState.hapticFeedbackEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    onOpenSearch()
                },
                shape = RoundedCornerShape(28.dp),
                color = glassBg,
                border = BorderStroke(1.dp, glassBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("home_search_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Search apps, or swipe up…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Optional Quick App Shortcuts Grid (Hidden in Pure Minimal Mode)
            if (!uiState.minimalMode && uiState.homeQuickApps.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.homeQuickApps, key = { it.id }) { app ->
                        AppItemView(
                            app = app,
                            iconSize = iconSize,
                            iconShape = iconShape,
                            showLabel = uiState.showAppLabels,
                            onClick = { onAppClick(app) },
                            onLongClick = {
                                if (uiState.hapticFeedbackEnabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                onAppLongClick(app)
                            }
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // Floating Glass Dock & Bottom Gesture Bar (Bottom Navigation SafeArea)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = glassBg,
                border = BorderStroke(1.dp, glassBorder),
                shadowElevation = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_dock")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    uiState.dockApps.forEach { app ->
                        AppItemView(
                            app = app,
                            iconSize = iconSize,
                            iconShape = iconShape,
                            showLabel = false,
                            onClick = { onAppClick(app) },
                            onLongClick = {
                                if (uiState.hapticFeedbackEnabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                onAppLongClick(app)
                            },
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }
            }

            // Minimalist Bottom Gesture Pill (Visual affordance for swipe up gesture)
            if (uiState.showGestureBar) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.35f))
                        .clickable { onOpenSearch() }
                        .testTag("launcher_gesture_bar")
                )
            }
        }
    }
}

@Composable
private fun ClockWidgetView(
    currentTime: Date,
    timeFormatter: SimpleDateFormat,
    amPmFormatter: SimpleDateFormat,
    dateFormatter: SimpleDateFormat,
    alignment: ClockAlignmentOption,
    accentColor: Color
) {
    val horizontalAlign = when (alignment) {
        ClockAlignmentOption.LEFT -> Alignment.Start
        ClockAlignmentOption.CENTER -> Alignment.CenterHorizontally
        ClockAlignmentOption.MINIMAL -> Alignment.Start
    }

    Column(
        horizontalAlignment = horizontalAlign,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("clock_widget")
    ) {
        when (alignment) {
            ClockAlignmentOption.MINIMAL -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = timeFormatter.format(currentTime),
                        style = MaterialTheme.typography.displayMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Light
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${dateFormatter.format(currentTime)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
            else -> {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = if (alignment == ClockAlignmentOption.CENTER) Arrangement.Center else Arrangement.Start
                ) {
                    Text(
                        text = timeFormatter.format(currentTime),
                        style = MaterialTheme.typography.displayLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraLight,
                        letterSpacing = (-2).sp
                    )
                    Text(
                        text = amPmFormatter.format(currentTime).uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.titleMedium,
                        color = accentColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp, start = 6.dp)
                    )
                }
                Text(
                    text = dateFormatter.format(currentTime),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

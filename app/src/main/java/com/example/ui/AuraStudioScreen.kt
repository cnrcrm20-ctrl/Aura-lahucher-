package com.example.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AccentOption
import com.example.data.AnimationSpeed
import com.example.data.AppItem
import com.example.data.ClockAlignmentOption
import com.example.data.GestureAction
import com.example.data.GlassIntensityOption
import com.example.data.IconShapeOption
import com.example.data.IconSizeOption
import com.example.data.ParticleIntensity
import com.example.data.WallpaperBlurOption
import com.example.data.WallpaperOption
import com.example.ui.theme.AuroraEmerald
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceBorderLight
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraStudioScreen(
    uiState: LauncherUiState,
    allApps: List<AppItem>,
    onBack: () -> Unit,
    onAccentSelect: (AccentOption) -> Unit,
    onIconSizeSelect: (IconSizeOption) -> Unit,
    onIconShapeSelect: (IconShapeOption) -> Unit,
    onShowLabelsToggle: (Boolean) -> Unit,
    onMinimalModeToggle: (Boolean) -> Unit,
    onGridColumnsSelect: (Int) -> Unit,
    onGlassIntensitySelect: (GlassIntensityOption) -> Unit,
    onGlassShimmerToggle: (Boolean) -> Unit,
    onClockAlignmentSelect: (ClockAlignmentOption) -> Unit,
    onShowClockToggle: (Boolean) -> Unit,
    onWallpaperSelect: (WallpaperOption) -> Unit,
    onWallpaperBlurSelect: (WallpaperBlurOption) -> Unit,
    onCustomWallpaperUriSelect: (String?) -> Unit,
    onLivingWallpaperToggle: (Boolean) -> Unit,
    onParticleIntensitySelect: (ParticleIntensity) -> Unit,
    onAnimationSpeedSelect: (AnimationSpeed) -> Unit,
    onSwipeUpSelect: (GestureAction) -> Unit,
    onSwipeDownSelect: (GestureAction) -> Unit,
    onHapticFeedbackToggle: (Boolean) -> Unit,
    onShowGestureBarToggle: (Boolean) -> Unit,
    onStartupAnimationToggle: (Boolean) -> Unit,
    onLowPowerToggle: (Boolean) -> Unit,
    onReduceMotionToggle: (Boolean) -> Unit,
    onToggleDockPackage: (String) -> Unit,
    onSetDefaultLauncher: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Appearance", "Wallpaper", "Gestures", "Identity", "Performance")
    var showXiaomiDialog by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onCustomWallpaperUriSelect(uri.toString())
        }
    }

    val accentColor = uiState.accentOption.color

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("aura_studio_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("btn_back_aura_studio")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "AURA STUDIO",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Phase 3 • System Experience & Customization Hub",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor
                    )
                }
            }

            // Scrollable Category Tabs
            SecondaryScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = accentColor,
                edgePadding = 16.dp,
                divider = { HorizontalDivider(color = SurfaceBorderLight) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == index) accentColor else TextSecondary
                            )
                        }
                    )
                }
            }

            // Tab Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTab) {
                    0 -> appearanceSection(
                        uiState = uiState,
                        accentColor = accentColor,
                        onAccentSelect = onAccentSelect,
                        onIconSizeSelect = onIconSizeSelect,
                        onIconShapeSelect = onIconShapeSelect,
                        onShowLabelsToggle = onShowLabelsToggle,
                        onMinimalModeToggle = onMinimalModeToggle,
                        onGridColumnsSelect = onGridColumnsSelect,
                        onGlassIntensitySelect = onGlassIntensitySelect,
                        onGlassShimmerToggle = onGlassShimmerToggle,
                        onClockAlignmentSelect = onClockAlignmentSelect,
                        onShowClockToggle = onShowClockToggle
                    )
                    1 -> wallpaperSection(
                        uiState = uiState,
                        accentColor = accentColor,
                        onWallpaperSelect = onWallpaperSelect,
                        onWallpaperBlurSelect = onWallpaperBlurSelect,
                        onLaunchPhotoPicker = {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onLivingWallpaperToggle = onLivingWallpaperToggle,
                        onParticleIntensitySelect = onParticleIntensitySelect,
                        onAnimationSpeedSelect = onAnimationSpeedSelect
                    )
                    2 -> gesturesSection(
                        uiState = uiState,
                        allApps = allApps,
                        accentColor = accentColor,
                        onSwipeUpSelect = onSwipeUpSelect,
                        onSwipeDownSelect = onSwipeDownSelect,
                        onHapticFeedbackToggle = onHapticFeedbackToggle,
                        onShowGestureBarToggle = onShowGestureBarToggle,
                        onToggleDockPackage = onToggleDockPackage,
                        onSetDefaultLauncher = onSetDefaultLauncher
                    )
                    3 -> identitySection(
                        accentColor = accentColor,
                        startupAnimationEnabled = uiState.startupAnimationEnabled,
                        onStartupAnimationToggle = onStartupAnimationToggle,
                        onOpenXiaomiSpec = { showXiaomiDialog = true }
                    )
                    4 -> performanceSection(
                        uiState = uiState,
                        accentColor = accentColor,
                        onLowPowerToggle = onLowPowerToggle,
                        onReduceMotionToggle = onReduceMotionToggle
                    )
                }
            }
        }

        // Xiaomi MTZ Specification Modal Dialog
        if (showXiaomiDialog) {
            AlertDialog(
                onDismissRequest = { showXiaomiDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Smartphone,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Xiaomi / Redmi Note 11 Pro MTZ Spec",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Non-Root Boot Animation Packaging Workflow:",
                            style = MaterialTheme.typography.labelLarge,
                            color = accentColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Xiaomi devices (MIUI 13/14 / HyperOS) natively support custom boot animations through the official Themes Engine (.mtz) WITHOUT root access or bootloader unlocking.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Text(
                            text = "1. Resolution: 1080 2400 30 (FHD+ 20:9 format matching Redmi Note 11 Pro)\n" +
                                   "2. desc.txt configuration:\n" +
                                   "   1080 2400 30\n" +
                                   "   p 1 0 part0\n" +
                                   "   p 0 0 part1\n" +
                                   "3. Packaging: Zip into 'bootanimation' with 0 compression (STORE mode), then place in 'AuraTheme.mtz'.\n" +
                                   "4. Applying: Open Xiaomi Themes > Profile > Customize Theme > Boot Animation > Apply.",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = TextPrimary
                        )

                        Text(
                            text = "Note: Replacing /system/media/bootanimation.zip directly is prevented by Android Verified Boot (AVB) and read-only system partitions. The official .mtz Theme workflow above is the legitimate non-root path.",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showXiaomiDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black)
                    ) {
                        Text("Understood")
                    }
                },
                containerColor = SurfaceDark
            )
        }
    }
}

// -------------------------------------------------------------
// 1. APPEARANCE SECTION
// -------------------------------------------------------------
private fun androidx.compose.foundation.lazy.LazyListScope.appearanceSection(
    uiState: LauncherUiState,
    accentColor: Color,
    onAccentSelect: (AccentOption) -> Unit,
    onIconSizeSelect: (IconSizeOption) -> Unit,
    onIconShapeSelect: (IconShapeOption) -> Unit,
    onShowLabelsToggle: (Boolean) -> Unit,
    onMinimalModeToggle: (Boolean) -> Unit,
    onGridColumnsSelect: (Int) -> Unit,
    onGlassIntensitySelect: (GlassIntensityOption) -> Unit,
    onGlassShimmerToggle: (Boolean) -> Unit,
    onClockAlignmentSelect: (ClockAlignmentOption) -> Unit,
    onShowClockToggle: (Boolean) -> Unit
) {
    // Accent Colors
    item {
        StudioCard(title = "ACCENT SYSTEM COLOR", icon = Icons.Default.Palette, accentColor = accentColor) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AccentOption.values().forEach { option ->
                    val isSelected = uiState.accentOption == option
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(option.color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onAccentSelect(option) }
                            .testTag("accent_${option.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Glass Intensity & Shimmer
    item {
        StudioCard(title = "GLASS UI INTENSITY & HIGHLIGHTS", icon = Icons.Default.ColorLens, accentColor = accentColor) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GlassIntensityOption.values().forEach { intensity ->
                    FilterChip(
                        selected = uiState.glassIntensity == intensity,
                        onClick = { onGlassIntensitySelect(intensity) },
                        label = { Text(intensity.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = Color.Black,
                            containerColor = SurfaceDark,
                            labelColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            StudioSwitchRow(
                title = "Ambient Glass Shimmer",
                subtitle = "Subtle light sweep across floating glass controls",
                checked = uiState.glassShimmerEnabled,
                onCheckedChange = onGlassShimmerToggle,
                accentColor = accentColor
            )
        }
    }

    // Minimal Mode & App Labels
    item {
        StudioCard(title = "MINIMALISM & LABELS", icon = Icons.Default.Home, accentColor = accentColor) {
            StudioSwitchRow(
                title = "Pure Minimal Mode",
                subtitle = "Hides home grid shortcuts, presenting only the clock and floating dock",
                checked = uiState.minimalMode,
                onCheckedChange = onMinimalModeToggle,
                accentColor = accentColor
            )
            HorizontalDivider(color = SurfaceBorderLight, modifier = Modifier.padding(vertical = 8.dp))
            StudioSwitchRow(
                title = "Display Application Labels",
                subtitle = "Show text labels under icons",
                checked = uiState.showAppLabels,
                onCheckedChange = onShowLabelsToggle,
                accentColor = accentColor
            )
        }
    }

    // Icon Shape & Scale
    item {
        StudioCard(title = "ICON SHAPE & SCALE", icon = Icons.Default.ViewModule, accentColor = accentColor) {
            Text(
                text = "Icon Geometric Silhouette",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconShapeOption.values().forEach { shape ->
                    FilterChip(
                        selected = uiState.iconShapeOption == shape,
                        onClick = { onIconShapeSelect(shape) },
                        label = { Text(shape.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = Color.Black,
                            containerColor = SurfaceDark,
                            labelColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Icon Size",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconSizeOption.values().forEach { size ->
                    FilterChip(
                        selected = uiState.iconSizeOption == size,
                        onClick = { onIconSizeSelect(size) },
                        label = { Text(size.label.substringBefore(" ")) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = Color.Black,
                            containerColor = SurfaceDark,
                            labelColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Grid Density (${uiState.gridColumns} Columns)",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(3, 4, 5, 6).forEach { cols ->
                    FilterChip(
                        selected = uiState.gridColumns == cols,
                        onClick = { onGridColumnsSelect(cols) },
                        label = { Text("$cols") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = Color.Black,
                            containerColor = SurfaceDark,
                            labelColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    // Clock & Date Widget Appearance
    item {
        StudioCard(title = "CLOCK & DATE WIDGET", icon = Icons.Default.Schedule, accentColor = accentColor) {
            StudioSwitchRow(
                title = "Show Clock Widget",
                subtitle = "Display live time and date on home screen",
                checked = uiState.showClock,
                onCheckedChange = onShowClockToggle,
                accentColor = accentColor
            )
            if (uiState.showClock) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Alignment Style",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ClockAlignmentOption.values().forEach { align ->
                        FilterChip(
                            selected = uiState.clockAlignment == align,
                            onClick = { onClockAlignmentSelect(align) },
                            label = { Text(align.label.substringBefore(" ")) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = accentColor,
                                selectedLabelColor = Color.Black,
                                containerColor = SurfaceDark,
                                labelColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. WALLPAPER & MOTION SECTION
// -------------------------------------------------------------
private fun androidx.compose.foundation.lazy.LazyListScope.wallpaperSection(
    uiState: LauncherUiState,
    accentColor: Color,
    onWallpaperSelect: (WallpaperOption) -> Unit,
    onWallpaperBlurSelect: (WallpaperBlurOption) -> Unit,
    onLaunchPhotoPicker: () -> Unit,
    onLivingWallpaperToggle: (Boolean) -> Unit,
    onParticleIntensitySelect: (ParticleIntensity) -> Unit,
    onAnimationSpeedSelect: (AnimationSpeed) -> Unit
) {
    // Wallpaper Choice
    item {
        StudioCard(title = "OCEAN WALLPAPER PRESETS", icon = Icons.Default.Wallpaper, accentColor = accentColor) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WallpaperOption.values().forEach { opt ->
                    val isSelected = uiState.wallpaperOption == opt
                    Surface(
                        onClick = {
                            if (opt == WallpaperOption.CUSTOM) {
                                onLaunchPhotoPicker()
                            } else {
                                onWallpaperSelect(opt)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) accentColor.copy(alpha = 0.15f) else SurfaceDark,
                        border = BorderStroke(1.dp, if (isSelected) accentColor else SurfaceBorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) accentColor else SurfaceBorder),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = opt.label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = opt.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Wallpaper Diffusion / Glass Blur",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WallpaperBlurOption.values().forEach { blurOpt ->
                    FilterChip(
                        selected = uiState.wallpaperBlurOption == blurOpt,
                        onClick = { onWallpaperBlurSelect(blurOpt) },
                        label = { Text(blurOpt.label.substringBefore(" (")) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = Color.Black,
                            containerColor = SurfaceDark,
                            labelColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    // Living Wallpaper Animation Controls
    item {
        StudioCard(title = "LIVING UNDERWATER ENGINE", icon = Icons.Default.Animation, accentColor = accentColor) {
            StudioSwitchRow(
                title = "Living Ocean Motion",
                subtitle = "Subtle bioluminescent particles and ambient water drift",
                checked = uiState.livingWallpaperEnabled,
                onCheckedChange = onLivingWallpaperToggle,
                accentColor = accentColor
            )

            AnimatedVisibility(visible = uiState.livingWallpaperEnabled) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = "Particle Density",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ParticleIntensity.values().forEach { intensity ->
                            FilterChip(
                                selected = uiState.particleIntensity == intensity,
                                onClick = { onParticleIntensitySelect(intensity) },
                                label = { Text(intensity.label.substringBefore(" ")) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = accentColor,
                                    selectedLabelColor = Color.Black,
                                    containerColor = SurfaceDark,
                                    labelColor = TextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Drift Velocity",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AnimationSpeed.values().forEach { speed ->
                            FilterChip(
                                selected = uiState.animationSpeed == speed,
                                onClick = { onAnimationSpeedSelect(speed) },
                                label = { Text(speed.label.substringBefore(" ")) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = accentColor,
                                    selectedLabelColor = Color.Black,
                                    containerColor = SurfaceDark,
                                    labelColor = TextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. GESTURES & HOME SECTION
// -------------------------------------------------------------
private fun androidx.compose.foundation.lazy.LazyListScope.gesturesSection(
    uiState: LauncherUiState,
    allApps: List<AppItem>,
    accentColor: Color,
    onSwipeUpSelect: (GestureAction) -> Unit,
    onSwipeDownSelect: (GestureAction) -> Unit,
    onHapticFeedbackToggle: (Boolean) -> Unit,
    onShowGestureBarToggle: (Boolean) -> Unit,
    onToggleDockPackage: (String) -> Unit,
    onSetDefaultLauncher: () -> Unit
) {
    // Tactile Feedback & Gesture Indicator
    item {
        StudioCard(title = "HAPTICS & GESTURE AFFORDANCE", icon = Icons.Default.Vibration, accentColor = accentColor) {
            StudioSwitchRow(
                title = "Haptic Tactile Feedback",
                subtitle = "Subtle native vibrations on gestures and long-press interactions",
                checked = uiState.hapticFeedbackEnabled,
                onCheckedChange = onHapticFeedbackToggle,
                accentColor = accentColor
            )
            HorizontalDivider(color = SurfaceBorderLight, modifier = Modifier.padding(vertical = 8.dp))
            StudioSwitchRow(
                title = "Bottom Gesture Bar Indicator",
                subtitle = "Minimalist floating glass bar indicating swipe-up for search",
                checked = uiState.showGestureBar,
                onCheckedChange = onShowGestureBarToggle,
                accentColor = accentColor
            )
        }
    }

    // Gestures Mapping
    item {
        StudioCard(title = "GESTURE MAPPING", icon = Icons.Default.Gesture, accentColor = accentColor) {
            Text(
                text = "Swipe Up on Home Screen",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(GestureAction.SEARCH, GestureAction.ALL_APPS).forEach { action ->
                    FilterChip(
                        selected = uiState.swipeUpAction == action,
                        onClick = { onSwipeUpSelect(action) },
                        label = { Text(action.label.substringAfter("Open ").substringAfter("Browse ")) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = Color.Black,
                            containerColor = SurfaceDark,
                            labelColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Swipe Down on Home Screen",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(GestureAction.NOTIFICATIONS, GestureAction.SEARCH).forEach { action ->
                    FilterChip(
                        selected = uiState.swipeDownAction == action,
                        onClick = { onSwipeDownSelect(action) },
                        label = { Text(action.label.substringAfter("Expand ").substringAfter("Open ")) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = Color.Black,
                            containerColor = SurfaceDark,
                            labelColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    // Dock Apps Manager
    item {
        StudioCard(
            title = "DOCK APPLICATION SELECTION (${uiState.dockApps.size}/5)",
            icon = Icons.Default.ViewModule,
            accentColor = accentColor
        ) {
            Text(
                text = "Select up to 5 apps to pin to your bottom glass dock:",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                allApps.take(12).forEach { app ->
                    val isPinned = uiState.dockApps.any { it.packageName == app.packageName }
                    Surface(
                        onClick = { onToggleDockPackage(app.packageName) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isPinned) accentColor.copy(alpha = 0.12f) else SurfaceDark,
                        border = BorderStroke(1.dp, if (isPinned) accentColor else SurfaceBorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppItemView(
                                app = app,
                                iconSize = 36.dp,
                                iconShape = uiState.iconShapeOption,
                                showLabel = false,
                                onClick = { onToggleDockPackage(app.packageName) },
                                onLongClick = {}
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = app.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (isPinned) Icons.Default.CheckCircle else Icons.Default.Check,
                                contentDescription = null,
                                tint = if (isPinned) accentColor else Color.Transparent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. IDENTITY & XIAOMI COMPATIBILITY SECTION
// -------------------------------------------------------------
private fun androidx.compose.foundation.lazy.LazyListScope.identitySection(
    accentColor: Color,
    startupAnimationEnabled: Boolean,
    onStartupAnimationToggle: (Boolean) -> Unit,
    onOpenXiaomiSpec: () -> Unit
) {
    // Aura Ring Animation Simulation
    item {
        StudioCard(title = "AURA RING LOGO SIMULATION", icon = Icons.Default.PlayArrow, accentColor = accentColor) {
            Text(
                text = "Interactive preview of the software-rendered celestial Aura ring animation:",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF04070D))
                    .border(1.dp, SurfaceBorderLight, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                AuraLogoAnimation(
                    accentColor = accentColor,
                    isInteractivePreview = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            StudioSwitchRow(
                title = "Startup Intro Animation",
                subtitle = "Display the Aura ring transition upon launching",
                checked = startupAnimationEnabled,
                onCheckedChange = onStartupAnimationToggle,
                accentColor = accentColor
            )
        }
    }

    // Xiaomi Redmi Note 11 Pro Theme Compatibility Card
    item {
        StudioCard(
            title = "XIAOMI & REDMI NOTE 11 PRO THEME COMPATIBILITY",
            icon = Icons.Default.Smartphone,
            accentColor = accentColor
        ) {
            Text(
                text = "Can an Aura boot animation run on Redmi Note 11 Pro without root?",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "YES — using Xiaomi's official MIUI/HyperOS Theme Engine (.mtz). The Theme Manager allows custom boot animations without modifying protected /system files or unlocking the bootloader.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onOpenXiaomiSpec,
                border = BorderStroke(1.dp, accentColor),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Xiaomi MTZ Boot Specification", color = accentColor)
            }
        }
    }
}

// -------------------------------------------------------------
// 5. PERFORMANCE SECTION
// -------------------------------------------------------------
private fun androidx.compose.foundation.lazy.LazyListScope.performanceSection(
    uiState: LauncherUiState,
    accentColor: Color,
    onLowPowerToggle: (Boolean) -> Unit,
    onReduceMotionToggle: (Boolean) -> Unit
) {
    item {
        StudioCard(title = "EFFICIENCY & THERMALS", icon = Icons.Default.BatterySaver, accentColor = accentColor) {
            StudioSwitchRow(
                title = "Low-Power Mode",
                subtitle = "Suspends animated particles and optimizes background draw calls",
                checked = uiState.lowPowerMode,
                onCheckedChange = onLowPowerToggle,
                accentColor = accentColor
            )
            HorizontalDivider(color = SurfaceBorderLight, modifier = Modifier.padding(vertical = 8.dp))
            StudioSwitchRow(
                title = "Reduce Motion",
                subtitle = "Disables non-essential movement and transitions",
                checked = uiState.reduceMotion,
                onCheckedChange = onReduceMotionToggle,
                accentColor = accentColor
            )
        }
    }

    item {
        StudioCard(title = "HONEST PLATFORM LIMITATIONS", icon = Icons.Default.Security, accentColor = accentColor) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BulletPoint(
                    title = "System Lock Screen & Notification Shade",
                    desc = "Android OS reserves lock screen and quick settings exclusively to SystemUI. A standard non-root launcher cannot alter them."
                )
                BulletPoint(
                    title = "Live Wallpaper vs Launcher Background",
                    desc = "Aura renders its living ocean natively inside the launcher. This avoids running a separate persistent system background service that drains battery."
                )
                BulletPoint(
                    title = "Zero Invasive Permissions",
                    desc = "Aura requires NO Accessibility Services, NO Device Admin, and NO Root privileges, ensuring zero telemetry and complete privacy."
                )
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER COMPONENTS
// -------------------------------------------------------------
@Composable
private fun StudioCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, SurfaceBorderLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun StudioSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = accentColor,
                checkedTrackColor = accentColor.copy(alpha = 0.25f),
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = SurfaceDark
            )
        )
    }
}

@Composable
private fun BulletPoint(title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "• $title",
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

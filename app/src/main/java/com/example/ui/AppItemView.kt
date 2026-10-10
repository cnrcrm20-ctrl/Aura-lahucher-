package com.example.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppItem
import com.example.data.IconShapeOption
import com.example.ui.theme.AuroraCyan
import com.example.ui.theme.SurfaceCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppItemView(
    app: AppItem,
    iconSize: Dp = 52.dp,
    iconShape: IconShapeOption = IconShapeOption.CIRCLE,
    showLabel: Boolean = true,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipShape: Shape = when (iconShape) {
        IconShapeOption.CIRCLE -> CircleShape
        IconShapeOption.SQUIRCLE -> RoundedCornerShape(percent = 28)
        IconShapeOption.ROUNDED_SQUARE -> RoundedCornerShape(percent = 18)
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .testTag("app_item_${app.packageName}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Icon container
        Box(
            modifier = Modifier.size(iconSize),
            contentAlignment = Alignment.Center
        ) {
            if (app.imageBitmap != null) {
                Image(
                    bitmap = app.imageBitmap,
                    contentDescription = app.label,
                    modifier = Modifier
                        .size(iconSize)
                        .clip(clipShape)
                )
            } else {
                // High-polish letter fallback
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(clipShape)
                        .background(SurfaceCard),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.initial.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = AuroraCyan
                    )
                }
            }
        }

        if (showLabel) {
            Text(
                text = app.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 13.sp,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .padding(horizontal = 2.dp)
            )
        }
    }
}

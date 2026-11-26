package io.kotelliada.marky.core.designsystem.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import io.kotelliada.marky.core.designsystem.theme.MarkyDimens

/**
 * A Margin icon. Drawn at 20 dp ([MarkyDimens.icon]) or 16 dp inside rows and fields
 * ([MarkyDimens.iconSmall]); takes its color from the surrounding content color.
 * Pass a null [contentDescription] only for decorative icons.
 */
@Composable
fun MarkyIcon(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = MarkyDimens.icon,
    tint: Color = LocalContentColor.current,
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        tint = tint,
    )
}

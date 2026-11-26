package io.kotelliada.marky.core.designsystem.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

/**
 * `--mk-shadow-float`: the one Margin shadow, for things that float (menus, dialogs, snackbars).
 * `0 12px 32px -8px` plus `0 2px 6px -2px`, both in shadow-color.
 */
fun Modifier.markyFloatShadow(shape: Shape, color: Color): Modifier = this
    .dropShadow(shape, Shadow(radius = 32.dp, color = color, spread = (-8).dp, offset = DpOffset(0.dp, 12.dp)))
    .dropShadow(shape, Shadow(radius = 6.dp, color = color, spread = (-2).dp, offset = DpOffset(0.dp, 2.dp)))

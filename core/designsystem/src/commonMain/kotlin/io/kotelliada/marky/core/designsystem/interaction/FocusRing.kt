package io.kotelliada.marky.core.designsystem.interaction

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import io.kotelliada.marky.core.designsystem.theme.MarkyDimens

/** Outline offset for controls (buttons, icon buttons, segments, tools, links, copy, checkbox). */
internal val FocusRingOutset = MarkyDimens.focusRing

/** Outline offset for full-width items (rows, menu items): drawn inside the bounds. */
internal val FocusRingInset = -MarkyDimens.focusRing

/**
 * The shared Margin focus ring: `outline: 2px solid var(--mk-focus)` with an [offset]
 * (positive draws outside the bounds, negative inside).
 */
internal fun Modifier.markyFocusRing(
    visible: Boolean,
    shape: Shape,
    color: Color,
    offset: Dp = FocusRingOutset,
): Modifier = if (!visible) this else drawWithContent {
    drawContent()
    val width = MarkyDimens.focusRing.toPx()
    // Distance from the bounds to the middle of the stroke.
    val grow = offset.toPx() + width / 2f
    val outline = shape.createOutline(
        size = Size(size.width + grow * 2f, size.height + grow * 2f),
        layoutDirection = layoutDirection,
        density = this,
    )
    translate(left = -grow, top = -grow) {
        drawOutline(outline = outline, color = color, style = Stroke(width = width))
    }
}

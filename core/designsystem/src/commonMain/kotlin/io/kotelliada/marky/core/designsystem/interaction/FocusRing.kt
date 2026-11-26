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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.isSpecified
import io.kotelliada.marky.core.designsystem.theme.MarkyDimens

/** Outline offset for controls (buttons, icon buttons, segments, tools, links, copy, checkbox). */
internal val FocusRingOutset = MarkyDimens.focusRing

/** Outline offset for full-width items (rows, menu items): drawn inside the bounds. */
internal val FocusRingInset = -MarkyDimens.focusRing

/**
 * The shared Margin focus ring: `outline: 2px solid var(--mk-focus)` with an [offset]
 * (positive draws outside the bounds, negative inside). With offset 0 and another width it also
 * draws Margin's halos (`box-shadow: 0 0 0 3px`), which start at the edge and grow outwards.
 *
 * Material components add a 48 dp minimum touch target around a smaller visual (a 40 dp icon
 * button, a 36 dp button). Pass that [visualSize] so the ring hugs the visual, centered in the
 * touch target, instead of the touch target.
 */
internal fun Modifier.markyFocusRing(
    visible: Boolean,
    shape: Shape,
    color: Color,
    offset: Dp = FocusRingOutset,
    width: Dp = MarkyDimens.focusRing,
    visualSize: DpSize = DpSize.Unspecified,
): Modifier = if (!visible) this else drawWithContent {
    drawContent()
    val strokeWidth = width.toPx()
    val visualWidth = if (visualSize.width.isSpecified) minOf(visualSize.width.toPx(), size.width) else size.width
    val visualHeight = if (visualSize.height.isSpecified) minOf(visualSize.height.toPx(), size.height) else size.height
    // Distance from the visual's bounds to the middle of the stroke.
    val grow = offset.toPx() + strokeWidth / 2f
    val outline = shape.createOutline(
        size = Size(visualWidth + grow * 2f, visualHeight + grow * 2f),
        layoutDirection = layoutDirection,
        density = this,
    )
    translate(left = (size.width - visualWidth) / 2f - grow, top = (size.height - visualHeight) / 2f - grow) {
        drawOutline(outline = outline, color = color, style = Stroke(width = strokeWidth))
    }
}

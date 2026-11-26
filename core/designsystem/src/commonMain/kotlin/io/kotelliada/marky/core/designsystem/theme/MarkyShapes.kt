package io.kotelliada.marky.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape

/** Margin corner radii. Nothing in Marky is fully rounded. */
object MarkyShapes {
    /** Checkbox, inline code, shortcut key, tooltip. */
    val xs = RoundedCornerShape(MarkyDimens.radiusXs)

    /** Toolbar tool, segment option, code block, menu item. */
    val sm = RoundedCornerShape(MarkyDimens.radiusSm)

    /** Buttons, fields, icon buttons, desktop rows, snackbar. */
    val md = RoundedCornerShape(MarkyDimens.radiusMd)

    /** Menu, dialog, empty-state glyph. */
    val lg = RoundedCornerShape(MarkyDimens.radiusLg)
}

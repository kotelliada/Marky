package io.kotelliada.marky.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.kotelliada.marky.core.designsystem.icons.MarkyIcons
import io.kotelliada.marky.core.designsystem.interaction.MarkyInteraction
import io.kotelliada.marky.core.designsystem.interaction.collectMarkyInteraction
import io.kotelliada.marky.core.designsystem.interaction.markyFocusRing
import io.kotelliada.marky.core.designsystem.preview.ForcedInteraction
import io.kotelliada.marky.core.designsystem.preview.MarkyPreviewPanels
import io.kotelliada.marky.core.designsystem.theme.MarkyColors
import io.kotelliada.marky.core.designsystem.theme.MarkyDimens
import io.kotelliada.marky.core.designsystem.theme.MarkyMotion
import io.kotelliada.marky.core.designsystem.theme.MarkyShapes
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme

/**
 * A Margin icon button: 40 dp visual inside a 48 dp touch target. [contentDescription] is
 * required and doubles as the desktop tooltip, with the [shortcut] when there is one.
 */
@Composable
fun MarkyIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shortcut: MarkyShortcut? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interaction = source.collectMarkyInteraction()
    val colors = animatedIconButtonColors(markyIconButtonColors(interaction, enabled, checked = false, MarkyTheme.colors))
    MarkyTooltip(label = contentDescription, shortcut = shortcut, modifier = modifier) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(MarkyDimens.iconButton)
                .markyFocusRing(enabled && interaction.focused, MarkyShapes.md, MarkyTheme.colors.focus, visualSize = IconButtonVisual)
                .pointerHoverIcon(PointerIcon.Hand),
            enabled = enabled,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = colors.container,
                contentColor = colors.content,
                disabledContainerColor = colors.container,
                disabledContentColor = colors.content,
            ),
            interactionSource = source,
            shape = MarkyShapes.md,
        ) {
            MarkyIcon(icon = icon, contentDescription = contentDescription)
        }
    }
}

/**
 * An icon button that toggles, such as Pin. When [checked] it turns accent and shows
 * [checkedIcon] (the filled glyph).
 */
@Composable
fun MarkyIconToggleButton(
    icon: ImageVector,
    checkedIcon: ImageVector,
    contentDescription: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shortcut: MarkyShortcut? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interaction = source.collectMarkyInteraction()
    val colors = animatedIconButtonColors(markyIconButtonColors(interaction, enabled, checked, MarkyTheme.colors))
    MarkyTooltip(label = contentDescription, shortcut = shortcut, modifier = modifier) {
        IconToggleButton(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
                .size(MarkyDimens.iconButton)
                .markyFocusRing(enabled && interaction.focused, MarkyShapes.md, MarkyTheme.colors.focus, visualSize = IconButtonVisual)
                .pointerHoverIcon(PointerIcon.Hand),
            enabled = enabled,
            colors = IconButtonDefaults.iconToggleButtonColors(
                containerColor = colors.container,
                contentColor = colors.content,
                disabledContainerColor = colors.container,
                disabledContentColor = colors.content,
                checkedContainerColor = colors.container,
                checkedContentColor = colors.content,
            ),
            interactionSource = source,
            shape = MarkyShapes.md,
        ) {
            MarkyIcon(icon = if (checked) checkedIcon else icon, contentDescription = contentDescription)
        }
    }
}

private val IconButtonVisual = DpSize(MarkyDimens.iconButton, MarkyDimens.iconButton)

@Immutable
internal data class MarkyIconButtonColors(val container: Color, val content: Color)

/**
 * text-2 at rest, text on hover and press over a state layer, accent when checked
 * (checked keeps the state layer but stays accent), text-disabled with no layer when disabled.
 */
internal fun markyIconButtonColors(
    interaction: MarkyInteraction,
    enabled: Boolean,
    checked: Boolean,
    c: MarkyColors,
): MarkyIconButtonColors {
    if (!enabled) return MarkyIconButtonColors(Color.Transparent, c.textDisabled)
    val container = when {
        interaction.pressed -> c.statePressed
        interaction.hovered -> c.stateHover
        else -> Color.Transparent
    }
    val content = when {
        checked -> c.accent
        interaction.pressed || interaction.hovered -> c.text
        else -> c.text2
    }
    return MarkyIconButtonColors(container, content)
}

@Composable
private fun animatedIconButtonColors(target: MarkyIconButtonColors): MarkyIconButtonColors {
    val spec = tween<Color>(MarkyMotion.DURATION_FAST_MS, easing = MarkyMotion.ease)
    return MarkyIconButtonColors(
        container = animateColorAsState(target.container, spec).value,
        content = animateColorAsState(target.content, spec).value,
    )
}

// ---------- Previews: default, hover, pressed, focused, on, disabled ----------

@Composable
private fun IconButtonStates(icon: ImageVector, label: String, withOn: Boolean) {
    MarkyPreviewPanels {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(MarkyInteraction.Rest, MarkyInteraction.Hovered, MarkyInteraction.Pressed, MarkyInteraction.Focused).forEach {
                ForcedInteraction(it) { MarkyIconButton(icon = icon, contentDescription = label, onClick = {}) }
            }
            if (withOn) {
                MarkyIconToggleButton(
                    icon = MarkyIcons.Pin,
                    checkedIcon = MarkyIcons.PinFilled,
                    contentDescription = "Unpin",
                    checked = true,
                    onCheckedChange = {},
                )
            }
            MarkyIconButton(icon = icon, contentDescription = label, onClick = {}, enabled = false)
        }
    }
}

@Preview(name = "Pin · default, hover, pressed, focused, on, disabled", widthDp = 760)
@Composable
private fun PinIconButtonPreview() = IconButtonStates(MarkyIcons.Pin, "Pin to top", withOn = true)

@Preview(name = "Import · default, hover, pressed, focused, disabled", widthDp = 760)
@Composable
private fun ImportIconButtonPreview() = IconButtonStates(MarkyIcons.Import, "Import .md file", withOn = false)

@Preview(name = "More · default, hover, pressed, focused, disabled", widthDp = 760)
@Composable
private fun MoreIconButtonPreview() = IconButtonStates(MarkyIcons.More, "More actions", withOn = false)

@Preview(name = "Pin toggle · off and on", widthDp = 760)
@Composable
private fun PinTogglePreview() {
    MarkyPreviewPanels {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MarkyIconToggleButton(MarkyIcons.Pin, MarkyIcons.PinFilled, "Pin to top", checked = false, onCheckedChange = {})
                MarkyIconToggleButton(MarkyIcons.Pin, MarkyIcons.PinFilled, "Unpin", checked = true, onCheckedChange = {})
                ForcedInteraction(MarkyInteraction.Hovered) {
                    MarkyIconToggleButton(MarkyIcons.Pin, MarkyIcons.PinFilled, "Unpin", checked = true, onCheckedChange = {})
                }
            }
        }
    }
}

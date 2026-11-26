package io.kotelliada.marky.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.kotelliada.marky.core.designsystem.icons.CheckHeavy
import io.kotelliada.marky.core.designsystem.interaction.MarkyInteraction
import io.kotelliada.marky.core.designsystem.interaction.collectMarkyInteraction
import io.kotelliada.marky.core.designsystem.interaction.markyFocusRing
import io.kotelliada.marky.core.designsystem.preview.ForcedInteraction
import io.kotelliada.marky.core.designsystem.preview.MarkyPreviewPanels
import io.kotelliada.marky.core.designsystem.theme.MarkyColors
import io.kotelliada.marky.core.designsystem.theme.MarkyMotion
import io.kotelliada.marky.core.designsystem.theme.MarkyShapes
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme

/**
 * A Markdown task (`- [ ]`). The whole line is the touch target. Checked items stay readable:
 * the text dims to text-2, never struck through. [enabled] false is for read-only contexts
 * such as search snippets and exported previews.
 *
 * Built on `toggleable` rather than Material's Checkbox, which is fixed at 20 dp with a 2 dp
 * stroke; Margin's box is 18 dp with a 1.5 dp stroke, radius-xs and a hover halo.
 */
@Composable
fun MarkyChecklistItem(
    text: AnnotatedString,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = MarkyTheme.typography.bodyL,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interaction = source.collectMarkyInteraction()
    val colors = MarkyTheme.colors
    val target = markyCheckboxColors(interaction, checked, enabled, colors)
    val spec = tween<Color>(MarkyMotion.DURATION_FAST_MS, easing = MarkyMotion.ease)
    val fill = animateColorAsState(target.fill, spec).value
    val border = animateColorAsState(target.border, spec).value
    val halo = animateColorAsState(target.halo, spec).value
    val tickAlpha = animateFloatAsState(if (checked) 1f else 0f, tween(MarkyMotion.DURATION_FAST_MS, easing = MarkyMotion.ease))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = source,
                indication = null,
            )
            .then(if (enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 3.dp)
                .size(18.dp)
                .markyFocusRing(halo != Color.Transparent, MarkyShapes.xs, halo, offset = 0.dp, width = 4.dp)
                .markyFocusRing(enabled && interaction.focused, MarkyShapes.xs, colors.focus)
                .background(fill, MarkyShapes.xs)
                .border(1.5.dp, border, MarkyShapes.xs),
            contentAlignment = Alignment.Center,
        ) {
            MarkyIcon(
                icon = CheckHeavy,
                contentDescription = null,
                size = 12.dp,
                tint = colors.onAccent,
                modifier = Modifier.graphicsLayer { alpha = tickAlpha.value },
            )
        }
        MarkyText(
            text = text,
            style = textStyle,
            color = if (checked) colors.text2 else colors.text,
            modifier = Modifier.weight(1f),
        )
    }
}

/** [MarkyChecklistItem] for plain text. */
@Composable
fun MarkyChecklistItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = MarkyTheme.typography.bodyL,
    interactionSource: MutableInteractionSource? = null,
) = MarkyChecklistItem(AnnotatedString(text), checked, onCheckedChange, modifier, enabled, textStyle, interactionSource)

@Immutable
internal data class MarkyCheckboxColors(val fill: Color, val border: Color, val halo: Color)

/** Disabled drops the halo and greys the box; checked fills accent; hover darkens the border. */
internal fun markyCheckboxColors(
    interaction: MarkyInteraction,
    checked: Boolean,
    enabled: Boolean,
    c: MarkyColors,
): MarkyCheckboxColors {
    if (!enabled) {
        return if (checked) MarkyCheckboxColors(c.textDisabled, c.textDisabled, Color.Transparent)
        else MarkyCheckboxColors(c.bg, c.textDisabled, Color.Transparent)
    }
    val halo = when {
        interaction.pressed -> c.statePressed
        interaction.hovered -> c.stateHover
        else -> Color.Transparent
    }
    return when {
        checked -> MarkyCheckboxColors(c.accent, c.accent, halo)
        interaction.hovered -> MarkyCheckboxColors(c.bg, c.text2, halo)
        else -> MarkyCheckboxColors(c.bg, c.text3, halo)
    }
}

// ---------- Previews: unchecked and checked × default, hover, pressed, focused, disabled ----------

@Composable
private fun ChecklistStates(checked: Boolean) {
    val label = if (checked) "Book flights" else "Tram 28 pass"
    MarkyPreviewPanels {
        Column {
            listOf(MarkyInteraction.Rest, MarkyInteraction.Hovered, MarkyInteraction.Pressed, MarkyInteraction.Focused).forEach {
                ForcedInteraction(it) { MarkyChecklistItem(text = label, checked = checked, onCheckedChange = {}) }
            }
            MarkyChecklistItem(text = label, checked = checked, onCheckedChange = {}, enabled = false)
        }
    }
}

@Preview(name = "Unchecked · default, hover, pressed, focused, disabled", widthDp = 760)
@Composable
private fun ChecklistUncheckedPreview() = ChecklistStates(checked = false)

@Preview(name = "Checked · default, hover, pressed, focused, disabled", widthDp = 760)
@Composable
private fun ChecklistCheckedPreview() = ChecklistStates(checked = true)

@Preview(name = "Long text wraps beside the box", widthDp = 760)
@Composable
private fun ChecklistLongPreview() {
    MarkyPreviewPanels {
        MarkyChecklistItem(text = "Switch to Edit and look at the source, then come back and tick this box", checked = false, onCheckedChange = {})
    }
}

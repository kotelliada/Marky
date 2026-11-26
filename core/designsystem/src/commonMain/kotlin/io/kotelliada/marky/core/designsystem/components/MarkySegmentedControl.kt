package io.kotelliada.marky.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.kotelliada.marky.core.designsystem.interaction.LocalForcedInteraction
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
 * Switches a note between Edit and Preview (desktop may add Split).
 *
 * Built on `selectable` rather than Material's SegmentedButton: Margin's options sit inside one
 * bordered container with 2 dp padding and gaps, while SegmentedButton draws an outline per
 * segment and adds a check icon.
 */
@Composable
fun MarkySegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    MarkySegmentedControlImpl(options, selectedIndex, onSelect, modifier, enabled, forcedIndex = -1)
}

/** [forcedIndex] lets a preview show one option hovered, pressed or focused. */
@Composable
internal fun MarkySegmentedControlImpl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    forcedIndex: Int,
) {
    val colors = MarkyTheme.colors
    val forced = LocalForcedInteraction.current
    Row(
        modifier = modifier
            .border(1.dp, colors.border, MarkyShapes.md)
            .background(colors.bg, MarkyShapes.md)
            .padding(2.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        options.forEachIndexed { index, option ->
            CompositionLocalProvider(LocalForcedInteraction provides forced.takeIf { index == forcedIndex }) {
                Segment(
                    text = option,
                    selected = index == selectedIndex,
                    enabled = enabled,
                    onClick = { onSelect(index) },
                )
            }
        }
    }
}

@Composable
private fun Segment(text: String, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val source = remember { MutableInteractionSource() }
    val interaction = source.collectMarkyInteraction()
    val target = markySegmentColors(interaction, selected, enabled, MarkyTheme.colors)
    val spec = tween<Color>(MarkyMotion.DURATION_FAST_MS, easing = MarkyMotion.ease)
    val container = animateColorAsState(target.container, spec).value
    val content = animateColorAsState(target.content, spec).value
    Box(
        modifier = Modifier
            .markyFocusRing(enabled && interaction.focused, MarkyShapes.sm, MarkyTheme.colors.focus)
            .background(container, MarkyShapes.sm)
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = source,
                indication = null,
            )
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        MarkyText(text = text, style = MarkyTheme.typography.control, color = content, maxLines = 1)
    }
}

@Immutable
internal data class MarkySegmentColors(val container: Color, val content: Color)

/** Selected beats pressed and hover; disabled beats everything (selected keeps a surface fill). */
internal fun markySegmentColors(
    interaction: MarkyInteraction,
    selected: Boolean,
    enabled: Boolean,
    c: MarkyColors,
): MarkySegmentColors = when {
    !enabled -> MarkySegmentColors(if (selected) c.surface else Color.Transparent, c.textDisabled)
    selected -> MarkySegmentColors(c.surface2, c.text)
    interaction.pressed -> MarkySegmentColors(c.statePressed, if (interaction.hovered) c.text else c.text3)
    interaction.hovered -> MarkySegmentColors(c.stateHover, c.text)
    else -> MarkySegmentColors(Color.Transparent, c.text3)
}

// ---------- Previews ----------

private val editPreview = listOf("Edit", "Preview")

@Preview(name = "Preview selected", widthDp = 760)
@Composable
private fun SegmentedPreviewSelectedPreview() {
    MarkyPreviewPanels { MarkySegmentedControl(editPreview, selectedIndex = 1, onSelect = {}) }
}

@Preview(name = "Edit selected", widthDp = 760)
@Composable
private fun SegmentedEditSelectedPreview() {
    MarkyPreviewPanels { MarkySegmentedControl(editPreview, selectedIndex = 0, onSelect = {}) }
}

@Preview(name = "Hover, pressed and focused on an option", widthDp = 760)
@Composable
private fun SegmentedInteractionPreview() {
    MarkyPreviewPanels {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ForcedInteraction(MarkyInteraction.Hovered) { MarkySegmentedControlImpl(editPreview, 1, {}, forcedIndex = 0) }
            ForcedInteraction(MarkyInteraction.Pressed) { MarkySegmentedControlImpl(editPreview, 1, {}, forcedIndex = 0) }
            ForcedInteraction(MarkyInteraction.Focused) { MarkySegmentedControlImpl(editPreview, 1, {}, forcedIndex = 1) }
        }
    }
}

@Preview(name = "Disabled", widthDp = 760)
@Composable
private fun SegmentedDisabledPreview() {
    MarkyPreviewPanels { MarkySegmentedControl(editPreview, selectedIndex = 1, onSelect = {}, enabled = false) }
}

@Preview(name = "Desktop · three options", widthDp = 760)
@Composable
private fun SegmentedThreeOptionsPreview() {
    MarkyPreviewPanels { MarkySegmentedControl(listOf("Edit", "Preview", "Split"), selectedIndex = 2, onSelect = {}) }
}

package io.kotelliada.marky.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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

/** Margin button variants. One primary per screen; danger only in the delete confirmation. */
enum class MarkyButtonVariant { Primary, Secondary, Ghost, Danger }

/** L (44, bottom bar) or M (36, dialogs, empty states, desktop). */
enum class MarkyButtonSize { Large, Medium }

/**
 * A Margin button. Fill the width (the bottom bar's New note) with `Modifier.fillMaxWidth()`.
 */
@Composable
fun MarkyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: MarkyButtonVariant = MarkyButtonVariant.Primary,
    size: MarkyButtonSize = MarkyButtonSize.Medium,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interaction = source.collectMarkyInteraction()
    val target = markyButtonColors(variant, interaction, enabled, MarkyTheme.colors)
    val container = animateColorAsState(target.container, tween(MarkyMotion.DURATION_FAST_MS, easing = MarkyMotion.ease)).value
    val content = animateColorAsState(target.content, tween(MarkyMotion.DURATION_FAST_MS, easing = MarkyMotion.ease)).value
    val colors = ButtonDefaults.buttonColors(
        containerColor = container,
        contentColor = content,
        disabledContainerColor = container,
        disabledContentColor = content,
    )
    val height = if (size == MarkyButtonSize.Large) MarkyDimens.buttonHeightLarge else MarkyDimens.buttonHeightMedium
    val buttonModifier = modifier
        .height(height)
        .markyFocusRing(enabled && interaction.focused, MarkyShapes.md, MarkyTheme.colors.focus, visualSize = DpSize(Dp.Unspecified, height))
        .pointerHoverIcon(PointerIcon.Hand)
    val padding = PaddingValues(
        horizontal = when {
            variant == MarkyButtonVariant.Ghost -> 10.dp
            size == MarkyButtonSize.Large -> 18.dp
            else -> 14.dp
        },
    )
    val border = BorderStroke(1.dp, target.border)
    val label: @Composable () -> Unit = { ButtonContent(text, icon, variant, size) }
    when (variant) {
        MarkyButtonVariant.Secondary -> OutlinedButton(
            onClick = onClick, modifier = buttonModifier, enabled = enabled, shape = MarkyShapes.md,
            colors = colors, border = border, contentPadding = padding, interactionSource = source,
        ) { label() }
        MarkyButtonVariant.Ghost -> TextButton(
            onClick = onClick, modifier = buttonModifier, enabled = enabled, shape = MarkyShapes.md,
            colors = colors, border = border, contentPadding = padding, interactionSource = source,
        ) { label() }
        MarkyButtonVariant.Primary, MarkyButtonVariant.Danger -> Button(
            onClick = onClick, modifier = buttonModifier, enabled = enabled, shape = MarkyShapes.md,
            colors = colors, border = border, contentPadding = padding, interactionSource = source,
        ) { label() }
    }
}

@Composable
private fun ButtonContent(text: String, icon: ImageVector?, variant: MarkyButtonVariant, size: MarkyButtonSize) {
    val base = if (size == MarkyButtonSize.Large) MarkyTheme.typography.labelL else MarkyTheme.typography.labelM
    val filled = variant == MarkyButtonVariant.Primary || variant == MarkyButtonVariant.Danger
    if (icon != null) {
        MarkyIcon(icon = icon, contentDescription = null, size = 18.dp)
        Spacer(Modifier.width(MarkyDimens.space2))
    }
    MarkyText(
        text = text,
        style = if (filled) base.copy(fontWeight = FontWeight.SemiBold) else base,
        maxLines = 1,
        softWrap = false,
    )
}

@Immutable
internal data class MarkyButtonColors(val container: Color, val content: Color, val border: Color)

/** Resolves a button's colors. Disabled wins over pressed, pressed over hover. */
internal fun markyButtonColors(
    variant: MarkyButtonVariant,
    interaction: MarkyInteraction,
    enabled: Boolean,
    c: MarkyColors,
): MarkyButtonColors = when (variant) {
    MarkyButtonVariant.Primary -> when {
        !enabled -> MarkyButtonColors(c.surface2, c.textDisabled, Color.Transparent)
        interaction.pressed -> MarkyButtonColors(c.accentPressed, c.onAccent, Color.Transparent)
        interaction.hovered -> MarkyButtonColors(c.accentHover, c.onAccent, Color.Transparent)
        else -> MarkyButtonColors(c.accent, c.onAccent, Color.Transparent)
    }
    MarkyButtonVariant.Danger -> when {
        !enabled -> MarkyButtonColors(c.surface2, c.textDisabled, Color.Transparent)
        interaction.pressed -> MarkyButtonColors(c.dangerPressed, c.onDanger, Color.Transparent)
        interaction.hovered -> MarkyButtonColors(c.dangerHover, c.onDanger, Color.Transparent)
        else -> MarkyButtonColors(c.danger, c.onDanger, Color.Transparent)
    }
    MarkyButtonVariant.Secondary -> when {
        !enabled -> MarkyButtonColors(c.bg, c.textDisabled, c.border)
        interaction.pressed -> MarkyButtonColors(c.surface2, c.text, c.borderStrong)
        interaction.hovered -> MarkyButtonColors(c.surface, c.text, c.borderStrong)
        else -> MarkyButtonColors(c.bg, c.text, c.borderStrong)
    }
    MarkyButtonVariant.Ghost -> when {
        !enabled -> MarkyButtonColors(Color.Transparent, c.textDisabled, Color.Transparent)
        interaction.pressed -> MarkyButtonColors(c.statePressed, c.accent, Color.Transparent)
        interaction.hovered -> MarkyButtonColors(c.stateHover, c.accent, Color.Transparent)
        else -> MarkyButtonColors(Color.Transparent, c.accent, Color.Transparent)
    }
}

// ---------- Previews: every variant in every state, then sizes ----------

private val sampleLabel = mapOf(
    MarkyButtonVariant.Primary to "New note",
    MarkyButtonVariant.Secondary to "Cancel",
    MarkyButtonVariant.Ghost to "Choose file",
    MarkyButtonVariant.Danger to "Delete",
)

@Preview(name = "Primary · default, hover, pressed, focused, disabled", widthDp = 1100)
@Composable
private fun PrimaryButtonPreview() = VariantStatesWide(MarkyButtonVariant.Primary)

@Preview(name = "Secondary · default, hover, pressed, focused, disabled", widthDp = 1100)
@Composable
private fun SecondaryButtonPreview() = VariantStatesWide(MarkyButtonVariant.Secondary)

@Preview(name = "Ghost · default, hover, pressed, focused, disabled", widthDp = 1100)
@Composable
private fun GhostButtonPreview() = VariantStatesWide(MarkyButtonVariant.Ghost)

@Preview(name = "Danger · default, hover, pressed, focused, disabled", widthDp = 1100)
@Composable
private fun DangerButtonPreview() = VariantStatesWide(MarkyButtonVariant.Danger)

@Composable
private fun VariantStatesWide(variant: MarkyButtonVariant) {
    MarkyPreviewPanels(panelWidth = 540.dp) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(MarkyInteraction.Rest, MarkyInteraction.Hovered, MarkyInteraction.Pressed, MarkyInteraction.Focused).forEach {
                ForcedInteraction(it) { MarkyButton(text = sampleLabel.getValue(variant), onClick = {}, variant = variant) }
            }
            MarkyButton(text = sampleLabel.getValue(variant), onClick = {}, variant = variant, enabled = false)
        }
    }
}

@Preview(name = "Sizes · L block with icon, M with icons", widthDp = 760)
@Composable
private fun ButtonSizesPreview() {
    MarkyPreviewPanels {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MarkyButton(text = "New note", onClick = {}, size = MarkyButtonSize.Large, icon = MarkyIcons.Plus, modifier = Modifier.fillMaxWidth())
            MarkyButton(text = "Import .md file", onClick = {}, variant = MarkyButtonVariant.Secondary, icon = MarkyIcons.Import)
            MarkyButton(text = "New note", onClick = {}, icon = MarkyIcons.Plus)
            MarkyButton(text = "Edit note", onClick = {}, variant = MarkyButtonVariant.Secondary, icon = MarkyIcons.Edit)
        }
    }
}

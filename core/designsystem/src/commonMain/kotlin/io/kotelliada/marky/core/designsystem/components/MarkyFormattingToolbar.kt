package io.kotelliada.marky.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.kotelliada.marky.core.designsystem.icons.MarkyIcons
import io.kotelliada.marky.core.designsystem.interaction.MarkyInteraction
import io.kotelliada.marky.core.designsystem.interaction.collectMarkyInteraction
import io.kotelliada.marky.core.designsystem.interaction.markyFocusRing
import io.kotelliada.marky.core.designsystem.preview.ForcedInteraction
import io.kotelliada.marky.core.designsystem.preview.MarkyPreviewPanels
import io.kotelliada.marky.core.designsystem.resources.Res
import io.kotelliada.marky.core.designsystem.resources.formatting
import io.kotelliada.marky.core.designsystem.resources.tool_bold
import io.kotelliada.marky.core.designsystem.resources.tool_bulleted_list
import io.kotelliada.marky.core.designsystem.resources.tool_checklist
import io.kotelliada.marky.core.designsystem.resources.tool_code_block
import io.kotelliada.marky.core.designsystem.resources.tool_heading
import io.kotelliada.marky.core.designsystem.resources.tool_italic
import io.kotelliada.marky.core.designsystem.resources.tool_link
import io.kotelliada.marky.core.designsystem.resources.tool_numbered_list
import io.kotelliada.marky.core.designsystem.resources.tool_quote
import io.kotelliada.marky.core.designsystem.theme.MarkyColors
import io.kotelliada.marky.core.designsystem.theme.MarkyDimens
import io.kotelliada.marky.core.designsystem.theme.MarkyMotion
import io.kotelliada.marky.core.designsystem.theme.MarkyShapes
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** The formatting tools, in toolbar order: inline styles, lists, other blocks. */
enum class MarkyFormattingTool { Heading, Bold, Italic, BulletedList, NumberedList, Checklist, Quote, CodeBlock, Link }

/** Above the keyboard on phones (hairline on top), top of the editor on desktop (hairline below). */
enum class MarkyToolbarPlacement { AboveKeyboard, Top }

private class ToolSpec(val icon: ImageVector, val label: StringResource, val shortcut: MarkyShortcut? = null)

private fun MarkyFormattingTool.spec(): ToolSpec = when (this) {
    MarkyFormattingTool.Heading -> ToolSpec(MarkyIcons.Heading, Res.string.tool_heading)
    MarkyFormattingTool.Bold -> ToolSpec(MarkyIcons.Bold, Res.string.tool_bold, MarkyShortcut.ctrl('b'))
    MarkyFormattingTool.Italic -> ToolSpec(MarkyIcons.Italic, Res.string.tool_italic, MarkyShortcut.ctrl('i'))
    MarkyFormattingTool.BulletedList -> ToolSpec(MarkyIcons.List, Res.string.tool_bulleted_list)
    MarkyFormattingTool.NumberedList -> ToolSpec(MarkyIcons.OrderedList, Res.string.tool_numbered_list)
    MarkyFormattingTool.Checklist -> ToolSpec(MarkyIcons.Task, Res.string.tool_checklist)
    MarkyFormattingTool.Quote -> ToolSpec(MarkyIcons.Quote, Res.string.tool_quote)
    MarkyFormattingTool.CodeBlock -> ToolSpec(MarkyIcons.Code, Res.string.tool_code_block)
    MarkyFormattingTool.Link -> ToolSpec(MarkyIcons.Link, Res.string.tool_link, MarkyShortcut.ctrl('k'))
}

private val ToolGroups = listOf(
    listOf(MarkyFormattingTool.Heading, MarkyFormattingTool.Bold, MarkyFormattingTool.Italic),
    listOf(MarkyFormattingTool.BulletedList, MarkyFormattingTool.NumberedList, MarkyFormattingTool.Checklist),
    listOf(MarkyFormattingTool.Quote, MarkyFormattingTool.CodeBlock, MarkyFormattingTool.Link),
)

/**
 * The formatting toolbar. [activeTools] are the formats the selection already has.
 * [enabled] is false while the title field has focus (formatting applies to the body only).
 * Scrolls sideways when it runs out of room.
 */
@Composable
fun MarkyFormattingToolbar(
    onToolClick: (MarkyFormattingTool) -> Unit,
    modifier: Modifier = Modifier,
    activeTools: Set<MarkyFormattingTool> = emptySet(),
    enabled: Boolean = true,
    placement: MarkyToolbarPlacement = MarkyToolbarPlacement.AboveKeyboard,
) {
    val colors = MarkyTheme.colors
    val formatting = stringResource(Res.string.formatting)
    val top = placement == MarkyToolbarPlacement.Top
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(MarkyDimens.toolbarHeight)
            .background(colors.surface)
            .drawBehind {
                val y = if (top) size.height - 0.5.dp.toPx() else 0.5.dp.toPx()
                drawLine(colors.border, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
            }
            .semantics { contentDescription = formatting },
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = if (top) 12.dp else 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ToolGroups.forEachIndexed { index, group ->
                if (index > 0) {
                    Box(Modifier.padding(horizontal = 3.dp).width(1.dp).height(20.dp).background(colors.border))
                }
                group.forEach { tool ->
                    val spec = tool.spec()
                    MarkyToolButton(
                        icon = spec.icon,
                        contentDescription = stringResource(spec.label),
                        onClick = { onToolClick(tool) },
                        active = tool in activeTools,
                        enabled = enabled,
                        shortcut = spec.shortcut,
                    )
                }
            }
        }
    }
}

/** One 36 dp toolbar tool. [active]: the selection already has this format. */
@Composable
fun MarkyToolButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    enabled: Boolean = true,
    shortcut: MarkyShortcut? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interaction = source.collectMarkyInteraction()
    val target = markyToolColors(interaction, active, enabled, MarkyTheme.colors)
    val spec = tween<Color>(MarkyMotion.DURATION_FAST_MS, easing = MarkyMotion.ease)
    val container = animateColorAsState(target.container, spec).value
    val content = animateColorAsState(target.content, spec).value
    MarkyTooltip(label = contentDescription, shortcut = shortcut, modifier = modifier) {
        IconToggleButton(
            checked = active,
            onCheckedChange = { onClick() },
            modifier = Modifier
                .size(36.dp)
                .markyFocusRing(enabled && interaction.focused, MarkyShapes.sm, MarkyTheme.colors.focus, visualSize = DpSize(36.dp, 36.dp))
                .pointerHoverIcon(PointerIcon.Hand),
            enabled = enabled,
            colors = IconButtonDefaults.iconToggleButtonColors(
                containerColor = container,
                contentColor = content,
                disabledContainerColor = container,
                disabledContentColor = content,
                checkedContainerColor = container,
                checkedContentColor = content,
            ),
            interactionSource = source,
            shape = MarkyShapes.sm,
        ) {
            MarkyIcon(icon = icon, contentDescription = contentDescription)
        }
    }
}

@Immutable
internal data class MarkyToolColors(val container: Color, val content: Color)

/** Disabled beats active, active beats pressed and hover. */
internal fun markyToolColors(interaction: MarkyInteraction, active: Boolean, enabled: Boolean, c: MarkyColors): MarkyToolColors = when {
    !enabled -> MarkyToolColors(Color.Transparent, c.textDisabled)
    active -> MarkyToolColors(c.accentSoft, c.accent)
    interaction.pressed -> MarkyToolColors(c.statePressed, c.text)
    interaction.hovered -> MarkyToolColors(c.stateHover, c.text)
    else -> MarkyToolColors(Color.Transparent, c.text)
}

// ---------- Previews ----------

@Preview(name = "Toolbar · default", widthDp = 760)
@Composable
private fun ToolbarDefaultPreview() {
    MarkyPreviewPanels(padding = 0.dp) { MarkyFormattingToolbar(onToolClick = {}) }
}

@Preview(name = "Toolbar · italic active", widthDp = 760)
@Composable
private fun ToolbarActivePreview() {
    MarkyPreviewPanels(padding = 0.dp) { MarkyFormattingToolbar(onToolClick = {}, activeTools = setOf(MarkyFormattingTool.Italic)) }
}

@Preview(name = "Toolbar · disabled while the title has focus", widthDp = 760)
@Composable
private fun ToolbarDisabledPreview() {
    MarkyPreviewPanels(padding = 0.dp) { MarkyFormattingToolbar(onToolClick = {}, enabled = false) }
}

@Preview(name = "Toolbar · desktop, top of the editor", widthDp = 880)
@Composable
private fun ToolbarTopPreview() {
    MarkyPreviewPanels(panelWidth = 420.dp, padding = 0.dp) {
        MarkyFormattingToolbar(onToolClick = {}, placement = MarkyToolbarPlacement.Top)
    }
}

@Preview(name = "Tool · default, hover, pressed, focused, active, disabled", widthDp = 760)
@Composable
private fun ToolStatesPreview() {
    MarkyPreviewPanels {
        Row {
            listOf(MarkyInteraction.Rest, MarkyInteraction.Hovered, MarkyInteraction.Pressed, MarkyInteraction.Focused).forEach {
                ForcedInteraction(it) {
                    Box(Modifier.padding(end = 12.dp)) { MarkyToolButton(MarkyIcons.Bold, "Bold", onClick = {}) }
                }
            }
            Box(Modifier.padding(end = 12.dp)) { MarkyToolButton(MarkyIcons.Bold, "Bold", onClick = {}, active = true) }
            MarkyToolButton(MarkyIcons.Bold, "Bold", onClick = {}, enabled = false)
        }
    }
}

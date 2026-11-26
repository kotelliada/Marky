package io.kotelliada.marky.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import io.kotelliada.marky.core.designsystem.icons.MarkyIcons
import io.kotelliada.marky.core.designsystem.interaction.FocusRingInset
import io.kotelliada.marky.core.designsystem.interaction.MarkyInteraction
import io.kotelliada.marky.core.designsystem.interaction.collectMarkyInteraction
import io.kotelliada.marky.core.designsystem.interaction.markyFocusRing
import io.kotelliada.marky.core.designsystem.preview.ForcedInteraction
import io.kotelliada.marky.core.designsystem.preview.MarkyPreviewPanels
import io.kotelliada.marky.core.designsystem.theme.MarkyColors
import io.kotelliada.marky.core.designsystem.theme.MarkyMotion
import io.kotelliada.marky.core.designsystem.theme.MarkyShapes
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme
import io.kotelliada.marky.core.designsystem.theme.markyFloatShadow

/**
 * The note's More actions menu, anchored below the end of the element it is placed next to
 * (put it in the same Box as the More button).
 *
 * Built on Popup rather than Material's DropdownMenu: DropdownMenu clips its own surface (so the
 * Margin float shadow can't be drawn), adds fixed 8 dp vertical padding where Margin uses 6, and
 * its items reserve a 24 dp icon slot and a 48 dp height where Margin uses 20 and 40.
 */
@Composable
fun MarkyMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 4.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!expanded) return
    val density = LocalDensity.current
    val provider = remember(offset, density) { MenuPositionProvider(offset, density) }
    Popup(
        popupPositionProvider = provider,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        val inspecting = LocalInspectionMode.current
        val progress = remember { Animatable(if (inspecting) 1f else 0f) }
        LaunchedEffect(Unit) { progress.animateTo(1f, tween(MarkyMotion.DURATION_BASE_MS, easing = MarkyMotion.ease)) }
        // Room for the float shadow: a popup window clips whatever is drawn outside it.
        Box(Modifier.padding(ShadowRoom)) {
            MarkyMenuPanel(
                modifier = modifier.graphicsLayer {
                    val p = progress.value
                    alpha = p
                    scaleX = 0.96f + 0.04f * p
                    scaleY = 0.96f + 0.04f * p
                    transformOrigin = TransformOrigin(1f, 0f)
                },
                content = content,
            )
        }
    }
}

private val ShadowRoom = PaddingValues(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 36.dp)

/** End-aligned below the anchor; flips above when there's no room; stays inside the window. */
private class MenuPositionProvider(private val offset: DpOffset, private val density: Density) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset = with(density) {
        val roomStart = 24.dp.roundToPx()
        val roomTop = 12.dp.roundToPx()
        val roomBottom = 36.dp.roundToPx()
        val dx = offset.x.roundToPx()
        val dy = offset.y.roundToPx()
        val panelWidth = popupContentSize.width - roomStart * 2
        val panelHeight = popupContentSize.height - roomTop - roomBottom
        val panelX = if (layoutDirection == LayoutDirection.Ltr) anchorBounds.right - panelWidth - dx else anchorBounds.left + dx
        val below = anchorBounds.bottom + dy
        val panelY = if (below + panelHeight <= windowSize.height) below else (anchorBounds.top - dy - panelHeight).coerceAtLeast(0)
        IntOffset(
            x = panelX.coerceIn(0, (windowSize.width - panelWidth).coerceAtLeast(0)) - roomStart,
            y = panelY - roomTop,
        )
    }
}

/** `.mk-menu`: float fill, 1 dp border, radius-lg, float shadow, min width 216, 6 dp padding. */
@Composable
internal fun MarkyMenuPanel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val colors = MarkyTheme.colors
    Column(
        modifier = modifier
            .markyFloatShadow(MarkyShapes.lg, colors.shadowColor)
            .background(colors.float, MarkyShapes.lg)
            .border(1.dp, colors.border, MarkyShapes.lg)
            .width(IntrinsicSize.Max)
            .widthIn(min = 216.dp)
            .padding(6.dp),
        content = content,
    )
}

/**
 * A menu item: 40 dp, 20 dp icon in text-2, label-m. [isDestructive] (Delete note) uses the
 * danger color, with danger-soft on hover and press; it goes last, below a [MarkyMenuDivider].
 */
@Composable
fun MarkyMenuItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false,
    enabled: Boolean = true,
    shortcut: MarkyShortcut? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interaction = source.collectMarkyInteraction()
    val colors = MarkyTheme.colors
    val itemColors = markyMenuItemColors(interaction, isDestructive, enabled, colors)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(itemColors.container, MarkyShapes.sm)
            .markyFocusRing(enabled && interaction.focused, MarkyShapes.sm, colors.focus, offset = FocusRingInset)
            .clickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .then(if (enabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MarkyIcon(icon = icon, contentDescription = null, tint = itemColors.icon)
        MarkyText(
            text = text,
            style = MarkyTheme.typography.labelM,
            color = itemColors.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (shortcut != null) MarkyShortcutKey(shortcut)
    }
}

/** The separator above the destructive item. */
@Composable
fun MarkyMenuDivider(modifier: Modifier = Modifier) {
    Box(
        modifier
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(MarkyTheme.colors.border),
    )
}

internal data class MarkyMenuItemColors(val container: Color, val icon: Color, val label: Color)

internal fun markyMenuItemColors(interaction: MarkyInteraction, destructive: Boolean, enabled: Boolean, c: MarkyColors): MarkyMenuItemColors {
    if (!enabled) return MarkyMenuItemColors(Color.Transparent, c.textDisabled, c.textDisabled)
    val active = interaction.pressed || interaction.hovered
    return if (destructive) {
        MarkyMenuItemColors(if (active) c.dangerSoft else Color.Transparent, c.danger, c.danger)
    } else {
        val container = when {
            interaction.pressed -> c.statePressed
            interaction.hovered -> c.stateHover
            else -> Color.Transparent
        }
        MarkyMenuItemColors(container, c.text2, c.text)
    }
}

// ---------- Previews ----------

@Preview(name = "Menu · pinned note", widthDp = 760)
@Composable
private fun MenuPinnedPreview() {
    MarkyPreviewPanels {
        MarkyMenuPanel {
            MarkyMenuItem("Unpin", MarkyIcons.Unpin, onClick = {})
            MarkyMenuItem("Export as .md", MarkyIcons.Export, onClick = {})
            MarkyMenuDivider()
            MarkyMenuItem("Delete note", MarkyIcons.Trash, onClick = {}, isDestructive = true)
        }
    }
}

@Preview(name = "Menu · unpinned note, hover on Export", widthDp = 760)
@Composable
private fun MenuHoverPreview() {
    MarkyPreviewPanels {
        MarkyMenuPanel {
            MarkyMenuItem("Pin to top", MarkyIcons.Pin, onClick = {})
            ForcedInteraction(MarkyInteraction.Hovered) { MarkyMenuItem("Export as .md", MarkyIcons.Export, onClick = {}) }
            MarkyMenuDivider()
            MarkyMenuItem("Delete note", MarkyIcons.Trash, onClick = {}, isDestructive = true)
        }
    }
}

@Preview(name = "Menu · pressed on Delete", widthDp = 760)
@Composable
private fun MenuPressedDeletePreview() {
    MarkyPreviewPanels {
        MarkyMenuPanel {
            MarkyMenuItem("Pin to top", MarkyIcons.Pin, onClick = {})
            MarkyMenuItem("Export as .md", MarkyIcons.Export, onClick = {})
            MarkyMenuDivider()
            ForcedInteraction(MarkyInteraction.Pressed) { MarkyMenuItem("Delete note", MarkyIcons.Trash, onClick = {}, isDestructive = true) }
        }
    }
}

@Composable
private fun ItemStates(destructive: Boolean) {
    val text = if (destructive) "Delete note" else "Export as .md"
    val icon = if (destructive) MarkyIcons.Trash else MarkyIcons.Export
    MarkyPreviewPanels {
        Column(Modifier.width(200.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(MarkyInteraction.Rest, MarkyInteraction.Hovered, MarkyInteraction.Pressed, MarkyInteraction.Focused).forEach {
                ForcedInteraction(it) { MarkyMenuItem(text, icon, onClick = {}, isDestructive = destructive) }
            }
            MarkyMenuItem(text, icon, onClick = {}, isDestructive = destructive, enabled = false)
        }
    }
}

@Preview(name = "Item · default, hover, pressed, focused, disabled", widthDp = 760)
@Composable
private fun MenuItemStatesPreview() = ItemStates(destructive = false)

@Preview(name = "Danger item · default, hover, pressed, focused, disabled", widthDp = 760)
@Composable
private fun MenuDangerItemStatesPreview() = ItemStates(destructive = true)

@Preview(name = "Menu item with shortcut (desktop)", widthDp = 760)
@Composable
private fun MenuShortcutPreview() {
    MarkyPreviewPanels {
        MarkyMenuPanel {
            MarkyMenuItem("Pin to top", MarkyIcons.Pin, onClick = {}, shortcut = MarkyShortcut.ctrl('p', shift = true))
            MarkyMenuItem("Export as .md", MarkyIcons.Export, onClick = {}, shortcut = MarkyShortcut.ctrl('s', shift = true))
            MarkyMenuDivider()
            MarkyMenuItem(
                "Delete note",
                MarkyIcons.Trash,
                onClick = {},
                isDestructive = true,
                shortcut = MarkyShortcut(MarkyShortcut.Key.Backspace, ctrl = true),
            )
        }
    }
}

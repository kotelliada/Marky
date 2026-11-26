package io.kotelliada.marky.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.kotelliada.marky.core.designsystem.platform.hasHoverTooltips
import io.kotelliada.marky.core.designsystem.preview.MarkyPreviewPanels
import io.kotelliada.marky.core.designsystem.theme.MarkyShapes
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme
import kotlinx.coroutines.delay

/** Hover time before a tooltip appears. */
internal const val TooltipHoverDelayMs = 500L

/**
 * Desktop tooltip for [content]: appears after 500 ms of hover or at once on keyboard focus,
 * and shows the [shortcut] when there is one. On Android and iOS it renders [content] only.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkyTooltip(
    label: String,
    modifier: Modifier = Modifier,
    shortcut: MarkyShortcut? = null,
    content: @Composable () -> Unit,
) {
    if (!hasHoverTooltips) {
        Box(modifier) { content() }
        return
    }
    val state = rememberTooltipState(isPersistent = true)
    var hovered by remember { mutableStateOf(false) }
    var focused by remember { mutableStateOf(false) }
    LaunchedEffect(hovered, focused) {
        if (hovered || focused) {
            if (!focused) delay(TooltipHoverDelayMs)
            state.show()
        } else {
            state.dismiss()
        }
    }
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above, 4.dp),
        tooltip = { MarkyTooltipBubble(label = label, shortcutLabel = shortcut?.let { markyShortcutLabel(it) }) },
        state = state,
        modifier = modifier,
        enableUserInput = false,
    ) {
        Box(
            Modifier
                .onFocusChanged { focused = it.hasFocus }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            when (awaitPointerEvent().type) {
                                PointerEventType.Enter -> hovered = true
                                PointerEventType.Exit -> hovered = false
                                PointerEventType.Press -> hovered = false
                            }
                        }
                    }
                },
        ) { content() }
    }
}

/** `.mk-tooltip`: inverse, radius-xs, 4 × 8 padding, Plex Sans 500 12/16. */
@Composable
internal fun MarkyTooltipBubble(label: String, shortcutLabel: String?, modifier: Modifier = Modifier) {
    val colors = MarkyTheme.colors
    Row(
        modifier = modifier
            .background(colors.inverse, MarkyShapes.xs)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MarkyText(
            text = label,
            style = MarkyTheme.typography.caption.copy(fontFamily = MarkyTheme.typography.sans, fontWeight = FontWeight.Medium),
            color = colors.onInverse,
            maxLines = 1,
        )
        if (shortcutLabel != null) MarkyShortcutKeyText(text = shortcutLabel, inverse = true)
    }
}

@Preview(name = "Tooltip · icon button with shortcut", widthDp = 760)
@Composable
private fun TooltipWithShortcutPreview() {
    MarkyPreviewPanels { MarkyTooltipBubble(label = "Import .md file", shortcutLabel = "Ctrl O") }
}

@Preview(name = "Tooltip · toolbar tool", widthDp = 760)
@Composable
private fun TooltipToolPreview() {
    MarkyPreviewPanels { MarkyTooltipBubble(label = "Bold", shortcutLabel = "Ctrl B") }
}

@Preview(name = "Tooltip · no shortcut", widthDp = 760)
@Composable
private fun TooltipNoShortcutPreview() {
    MarkyPreviewPanels { MarkyTooltipBubble(label = "More actions", shortcutLabel = null) }
}

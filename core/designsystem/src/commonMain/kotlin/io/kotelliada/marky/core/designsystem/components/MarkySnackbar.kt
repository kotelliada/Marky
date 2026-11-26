package io.kotelliada.marky.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kotelliada.marky.core.designsystem.icons.MarkyIcons
import io.kotelliada.marky.core.designsystem.interaction.MarkyInteraction
import io.kotelliada.marky.core.designsystem.interaction.collectMarkyInteraction
import io.kotelliada.marky.core.designsystem.interaction.markyFocusRing
import io.kotelliada.marky.core.designsystem.preview.ForcedInteraction
import io.kotelliada.marky.core.designsystem.preview.MarkyPreviewPanels
import io.kotelliada.marky.core.designsystem.theme.MarkyMotion
import io.kotelliada.marky.core.designsystem.theme.MarkyShapes
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme
import io.kotelliada.marky.core.designsystem.theme.markyFloatShadow
import kotlinx.coroutines.delay

/** What a Marky snackbar shows. [isError] adds the alert icon and keeps it up for 6 s instead of 4 s. */
class MarkySnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    val isError: Boolean = false,
) : SnackbarVisuals {
    // MarkySnackbarHost times the snackbar itself (4 s / 6 s, paused while hovered or focused).
    override val duration: SnackbarDuration = SnackbarDuration.Indefinite
    override val withDismissAction: Boolean = false
}

/** Shows a Margin snackbar and suspends until it is dismissed or its action is pressed. */
suspend fun SnackbarHostState.showMarkySnackbar(
    message: String,
    actionLabel: String? = null,
    isError: Boolean = false,
): SnackbarResult = showSnackbar(MarkySnackbarVisuals(message, actionLabel, isError))

/** 4 s, errors 6 s. */
internal fun markySnackbarTimeoutMillis(isError: Boolean): Long = if (isError) 6_000L else 4_000L

/**
 * Hosts snackbars from [hostState]. Placement is the screen's job: phone 12 dp above the bottom
 * bar or screen edge; desktop bottom left of the note pane, at most 440 dp wide.
 * A snackbar stays while it is hovered or focused, then gets its full time again.
 */
@Composable
fun MarkySnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    val data = hostState.currentSnackbarData ?: return
    key(data) {
        val visuals = data.visuals
        val isError = (visuals as? MarkySnackbarVisuals)?.isError ?: false
        val hold = remember { MutableInteractionSource() }
        val hovered = hold.collectIsHoveredAsState().value
        val focused = remember { androidx.compose.runtime.mutableStateOf(false) }
        val paused = hovered || focused.value
        LaunchedEffect(paused) {
            if (!paused) {
                delay(markySnackbarTimeoutMillis(isError))
                data.dismiss()
            }
        }
        MarkySnackbar(
            message = visuals.message,
            actionLabel = visuals.actionLabel,
            isError = isError,
            onActionClick = data::performAction,
            modifier = modifier
                .hoverable(hold)
                .onFocusChanged { focused.value = it.hasFocus },
        )
    }
}

/** `.mk-snack`: inverse fill, on-inverse body-s text, radius-md, min 48 high, optional action. */
@Composable
fun MarkySnackbar(
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    isError: Boolean = false,
    onActionClick: () -> Unit = {},
) {
    val colors = MarkyTheme.colors
    val inspecting = LocalInspectionMode.current
    val progress = remember { Animatable(if (inspecting) 1f else 0f) }
    LaunchedEffect(Unit) { progress.animateTo(1f, tween(MarkyMotion.DURATION_SLOW_MS, easing = MarkyMotion.ease)) }
    Row(
        modifier = modifier
            .graphicsLayer {
                val p = progress.value
                alpha = p
                translationY = (1f - p) * 8.dp.toPx()
            }
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .markyFloatShadow(MarkyShapes.md, colors.shadowColor)
            .background(colors.inverse, MarkyShapes.md)
            .padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (isError) MarkyIcon(icon = MarkyIcons.Alert, contentDescription = null, size = 18.dp, tint = colors.onInverse)
        MarkyText(text = message, style = MarkyTheme.typography.bodyS, color = colors.onInverse, modifier = Modifier.weight(1f))
        if (actionLabel != null) SnackbarAction(actionLabel, onActionClick)
    }
}

@Composable
private fun SnackbarAction(label: String, onClick: () -> Unit) {
    val source = remember { MutableInteractionSource() }
    val interaction = source.collectMarkyInteraction()
    val colors = MarkyTheme.colors
    val layer = when {
        interaction.pressed -> colors.onInverse.copy(alpha = 0.18f).compositeOver(colors.inverse)
        interaction.hovered -> colors.onInverse.copy(alpha = 0.10f).compositeOver(colors.inverse)
        else -> Color.Transparent
    }
    // Not a Material TextButton: those enforce a 58 dp minimum width; the action hugs its label.
    Box(
        modifier = Modifier
            .height(32.dp)
            .markyFocusRing(interaction.focused, MarkyShapes.sm, colors.focus)
            .background(layer, MarkyShapes.sm)
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick)
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        MarkyText(
            text = label,
            style = MarkyTheme.typography.bodyS.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 20.sp),
            color = colors.inverseAccent,
            maxLines = 1,
        )
    }
}

// ---------- Previews ----------

@Preview(name = "Import failed, with action", widthDp = 760)
@Composable
private fun SnackbarErrorPreview() {
    MarkyPreviewPanels {
        MarkySnackbar(message = "Couldn’t import “scan.pdf”. Choose a Markdown (.md) file.", actionLabel = "Choose file", isError = true)
    }
}

@Preview(name = "Export finished", widthDp = 760)
@Composable
private fun SnackbarExportedPreview() {
    MarkyPreviewPanels { MarkySnackbar(message = "Exported “Lisbon trip.md”") }
}

@Preview(name = "Action · hover", widthDp = 760)
@Composable
private fun SnackbarActionHoverPreview() {
    MarkyPreviewPanels { ForcedInteraction(MarkyInteraction.Hovered) { MarkySnackbar(message = "Note deleted", actionLabel = "OK") } }
}

@Preview(name = "Action · focused", widthDp = 760)
@Composable
private fun SnackbarActionFocusedPreview() {
    MarkyPreviewPanels { ForcedInteraction(MarkyInteraction.Focused) { MarkySnackbar(message = "Note deleted", actionLabel = "OK") } }
}


package io.kotelliada.marky.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.kotelliada.marky.core.designsystem.platform.MarkyDialogScrim
import io.kotelliada.marky.core.designsystem.platform.markyDialogProperties
import io.kotelliada.marky.core.designsystem.preview.MarkyPreviewPanels
import io.kotelliada.marky.core.designsystem.theme.MarkyMotion
import io.kotelliada.marky.core.designsystem.theme.MarkyShapes
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme
import io.kotelliada.marky.core.designsystem.theme.markyFloatShadow

/**
 * The confirmation dialog (Marky has one: delete). Name the note in [title], in curly quotes.
 * Cancel is secondary, the confirm action is danger, both right aligned.
 * Esc, back and tapping the scrim call [onDismiss].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkyDialog(
    title: String,
    body: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MarkyTheme.colors
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = markyDialogProperties(colors.scrim),
    ) {
        MarkyDialogScrim(colors.scrim)
        val inspecting = LocalInspectionMode.current
        val progress = remember { Animatable(if (inspecting) 1f else 0f) }
        LaunchedEffect(Unit) { progress.animateTo(1f, tween(MarkyMotion.DURATION_BASE_MS, easing = MarkyMotion.ease)) }
        // 24 dp inset from the screen edge; also leaves room for the float shadow.
        Box(Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
            MarkyDialogPanel(
                title = title,
                body = body,
                confirmLabel = confirmLabel,
                dismissLabel = dismissLabel,
                onConfirm = onConfirm,
                onDismiss = onDismiss,
                modifier = modifier.graphicsLayer {
                    val p = progress.value
                    alpha = p
                    scaleX = 0.96f + 0.04f * p
                    scaleY = 0.96f + 0.04f * p
                },
            )
        }
    }
}

/** `.mk-dialog`: float fill, radius-lg, 20 dp padding, max width 320, float shadow. */
@Composable
internal fun MarkyDialogPanel(
    title: String,
    body: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MarkyTheme.colors
    Column(
        modifier = modifier
            .widthIn(max = 320.dp)
            .fillMaxWidth()
            .markyFloatShadow(MarkyShapes.lg, colors.shadowColor)
            .background(colors.float, MarkyShapes.lg)
            .padding(20.dp)
            .semantics { paneTitle = title },
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MarkyText(text = title, style = MarkyTheme.typography.titleM, color = colors.text)
        MarkyText(text = body, style = MarkyTheme.typography.bodyM, color = colors.text2)
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        ) {
            MarkyButton(text = dismissLabel, onClick = onDismiss, variant = MarkyButtonVariant.Secondary)
            MarkyButton(text = confirmLabel, onClick = onConfirm, variant = MarkyButtonVariant.Danger)
        }
    }
}

@Preview(name = "Delete confirmation over the scrim", widthDp = 760)
@Composable
private fun DialogPreview() {
    MarkyPreviewPanels {
        Box(Modifier.fillMaxWidth().height(300.dp).background(MarkyTheme.colors.bg, MarkyShapes.md)) {
            MarkyNoteRow(
                title = "Lisbon trip",
                preview = "Flights booked for 12 Dec. Hotel is near Príncipe Real.",
                timeLabel = "3h",
                onClick = {},
                modifier = Modifier.padding(16.dp),
            )
            Box(
                Modifier.matchParentSize().background(MarkyTheme.colors.scrim, MarkyShapes.md).padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                MarkyDialogPanel(
                    title = "Delete “Lisbon trip”?",
                    body = "The note is removed from this device. This can’t be undone.",
                    confirmLabel = "Delete",
                    dismissLabel = "Cancel",
                    onConfirm = {},
                    onDismiss = {},
                )
            }
        }
    }
}

@Preview(name = "Long note name wraps", widthDp = 760)
@Composable
private fun DialogLongTitlePreview() {
    MarkyPreviewPanels {
        MarkyDialogPanel(
            title = "Delete “Notes from the product offsite in Porto, day two afternoon”?",
            body = "The note is removed from this device. This can’t be undone.",
            confirmLabel = "Delete",
            dismissLabel = "Cancel",
            onConfirm = {},
            onDismiss = {},
        )
    }
}

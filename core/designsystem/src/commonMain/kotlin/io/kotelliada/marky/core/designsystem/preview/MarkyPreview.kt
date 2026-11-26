package io.kotelliada.marky.core.designsystem.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.kotelliada.marky.core.designsystem.components.MarkyText
import io.kotelliada.marky.core.designsystem.interaction.LocalForcedInteraction
import io.kotelliada.marky.core.designsystem.interaction.MarkyInteraction
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme

/**
 * Renders [content] twice, in a Light and a Dark panel side by side, the way
 * design-system.html shows every component. Preview-only.
 */
@Composable
internal fun MarkyPreviewPanels(
    panelWidth: Dp = 360.dp,
    padding: Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    Row {
        PreviewPanel(darkTheme = false, width = panelWidth, padding = padding, content = content)
        PreviewPanel(darkTheme = true, width = panelWidth, padding = padding, content = content)
    }
}

@Composable
private fun PreviewPanel(darkTheme: Boolean, width: Dp, padding: Dp, content: @Composable () -> Unit) {
    MarkyTheme(darkTheme = darkTheme) {
        Column(
            modifier = Modifier
                .width(width)
                .background(MarkyTheme.colors.bg)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MarkyText(
                text = if (darkTheme) "DARK" else "LIGHT",
                style = MarkyTheme.typography.overline,
                color = MarkyTheme.colors.text3,
            )
            content()
        }
    }
}

/** Shows [content] as if it were hovered, pressed or focused. Preview-only. */
@Composable
internal fun ForcedInteraction(interaction: MarkyInteraction, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalForcedInteraction provides interaction, content = content)
}

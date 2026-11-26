package io.kotelliada.marky.core.designsystem.icons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.kotelliada.marky.core.designsystem.components.MarkyIcon
import io.kotelliada.marky.core.designsystem.preview.MarkyPreviewPanels
import io.kotelliada.marky.core.designsystem.theme.MarkyDimens
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme

@Preview(name = "Icons · 20 dp, text-2", widthDp = 760)
@Composable
private fun IconsPreview() {
    MarkyPreviewPanels {
        IconGrid { icon -> MarkyIcon(icon, contentDescription = null, tint = MarkyTheme.colors.text2) }
    }
}

@Preview(name = "Icons · 16 dp, text", widthDp = 760)
@Composable
private fun IconsSmallPreview() {
    MarkyPreviewPanels {
        IconGrid { icon -> MarkyIcon(icon, contentDescription = null, size = MarkyDimens.iconSmall, tint = MarkyTheme.colors.text) }
    }
}

@Preview(name = "Pin · off and on (accent, filled)", widthDp = 760)
@Composable
private fun PinStatesPreview() {
    MarkyPreviewPanels {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MarkyIcon(MarkyIcons.Pin, contentDescription = null, tint = MarkyTheme.colors.text2)
            MarkyIcon(MarkyIcons.PinFilled, contentDescription = null, tint = MarkyTheme.colors.accent)
            MarkyIcon(CheckHeavy, contentDescription = null, size = 12.dp, tint = MarkyTheme.colors.accent)
        }
    }
}

@Composable
private fun IconGrid(cell: @Composable (androidx.compose.ui.graphics.vector.ImageVector) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MarkyIcons.All.chunked(8).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) { row.forEach { cell(it) } }
        }
    }
}

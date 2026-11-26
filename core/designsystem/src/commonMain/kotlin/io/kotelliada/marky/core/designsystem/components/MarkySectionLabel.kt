package io.kotelliada.marky.core.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.kotelliada.marky.core.designsystem.preview.MarkyPreviewPanels
import io.kotelliada.marky.core.designsystem.theme.MarkyDimens
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme

/** Section label (`.mk-overline`): "Pinned" above the list, "3 results" above search results. */
@Composable
fun MarkySectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = MarkySectionLabelDefaults.PhonePadding,
) {
    MarkyText(
        text = text.uppercase(),
        style = MarkyTheme.typography.overline,
        color = MarkyTheme.colors.text3,
        maxLines = 1,
        modifier = modifier.fillMaxWidth().padding(contentPadding),
    )
}

object MarkySectionLabelDefaults {
    /** Phone list: 16 top, 20 gutter, 8 bottom. */
    val PhonePadding = PaddingValues(start = MarkyDimens.gutterPhone, top = 16.dp, end = MarkyDimens.gutterPhone, bottom = 8.dp)

    /** Desktop sidebar list: 14 top, 12 sides, 6 bottom. */
    val SidebarPadding = PaddingValues(start = 12.dp, top = 14.dp, end = 12.dp, bottom = 6.dp)
}

@Preview(name = "Section label · phone", widthDp = 760)
@Composable
private fun SectionLabelPhonePreview() {
    MarkyPreviewPanels(padding = 0.dp) { MarkySectionLabel("Pinned") }
}

@Preview(name = "Section label · desktop sidebar, result count", widthDp = 760)
@Composable
private fun SectionLabelSidebarPreview() {
    MarkyPreviewPanels(padding = 0.dp) { MarkySectionLabel("3 results", contentPadding = MarkySectionLabelDefaults.SidebarPadding) }
}

package io.kotelliada.marky.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kotelliada.marky.core.designsystem.icons.MarkyIcons
import io.kotelliada.marky.core.designsystem.preview.MarkyPreviewPanels
import io.kotelliada.marky.core.designsystem.theme.MarkyDimens
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme

/** Regular for screens; Small (40 dp glyph, title-s, body-s) for the desktop sidebar. */
enum class MarkyEmptyStateSize { Regular, Small }

/**
 * An empty state: says what is missing and what to do next. The [glyph] is a Markdown or
 * keyboard character (#, 0, ¶, ↵) in a dashed frame. Small states take no actions.
 */
@Composable
fun MarkyEmptyState(
    glyph: String,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    size: MarkyEmptyStateSize = MarkyEmptyStateSize.Regular,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    EmptyStateLayout(title, body, modifier, size, actions) {
        val small = size == MarkyEmptyStateSize.Small
        MarkyText(
            text = glyph,
            style = MarkyTheme.typography.code.copy(
                fontWeight = FontWeight.Medium,
                fontSize = if (small) 18.sp else 24.sp,
                lineHeight = if (small) 18.sp else 24.sp,
            ),
            color = MarkyTheme.colors.text3,
            maxLines = 1,
        )
    }
}

/** [MarkyEmptyState] with an icon glyph (the Search screen's magnifier). */
@Composable
fun MarkyEmptyState(
    glyph: ImageVector,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    size: MarkyEmptyStateSize = MarkyEmptyStateSize.Regular,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    EmptyStateLayout(title, body, modifier, size, actions) {
        MarkyIcon(
            icon = glyph,
            contentDescription = null,
            size = if (size == MarkyEmptyStateSize.Small) 18.dp else 22.dp,
            tint = MarkyTheme.colors.text3,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EmptyStateLayout(
    title: String,
    body: String,
    modifier: Modifier,
    size: MarkyEmptyStateSize,
    actions: (@Composable RowScope.() -> Unit)?,
    glyph: @Composable () -> Unit,
) {
    val small = size == MarkyEmptyStateSize.Small
    val colors = MarkyTheme.colors
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .padding(horizontal = 8.dp, vertical = if (small) 24.dp else 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(if (small) 4.dp else 8.dp),
        ) {
            GlyphFrame(frame = if (small) 40.dp else 56.dp, radius = if (small) MarkyDimens.radiusMd else MarkyDimens.radiusLg) { glyph() }
            Spacer(Modifier.height(if (small) 4.dp else 0.dp))
            MarkyText(
                text = title,
                style = if (small) MarkyTheme.typography.titleS else MarkyTheme.typography.titleM,
                color = colors.text,
                textAlign = TextAlign.Center,
            )
            MarkyText(
                text = body,
                style = if (small) MarkyTheme.typography.bodyS else MarkyTheme.typography.bodyM,
                color = colors.text2,
                textAlign = TextAlign.Center,
            )
            if (actions != null && !small) {
                FlowRow(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    content = { actions() },
                )
            }
        }
    }
}

/** 1 dp dashed border-strong frame. */
@Composable
private fun GlyphFrame(frame: Dp, radius: Dp, content: @Composable () -> Unit) {
    val border = MarkyTheme.colors.borderStrong
    Box(
        modifier = Modifier
            .size(frame)
            .drawBehind {
                val stroke = 1.dp.toPx()
                val inset = stroke / 2f
                drawRoundRect(
                    color = border,
                    topLeft = Offset(inset, inset),
                    size = Size(size.width - stroke, size.height - stroke),
                    cornerRadius = CornerRadius(radius.toPx() - inset),
                    style = Stroke(width = stroke, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx()))),
                )
            },
        contentAlignment = Alignment.Center,
    ) { content() }
}

// ---------- Previews: the empty states from the design ----------

@Preview(name = "No notes (phone list)", widthDp = 760)
@Composable
private fun EmptyNotesPreview() {
    MarkyPreviewPanels {
        MarkyEmptyState(
            glyph = "#",
            title = "No notes yet",
            body = "Tap New note to write your first one, or import a .md file from another editor.",
        ) {
            MarkyButton(text = "Import .md file", onClick = {}, variant = MarkyButtonVariant.Secondary, icon = MarkyIcons.Import)
        }
    }
}

@Preview(name = "Search, before typing", widthDp = 760)
@Composable
private fun EmptySearchPreview() {
    MarkyPreviewPanels {
        MarkyEmptyState(
            glyph = MarkyIcons.Search,
            title = "Search your notes",
            body = "Type a word from a title or from the text. Notes with a matching title come first.",
        )
    }
}

@Preview(name = "Search, no results", widthDp = 760)
@Composable
private fun EmptyNoResultsPreview() {
    MarkyPreviewPanels {
        MarkyEmptyState(
            glyph = "0",
            title = "No notes match “bananas”",
            body = "Check the spelling or try fewer words. Search looks through every title and all note text.",
        )
    }
}

@Preview(name = "Note preview, empty note", widthDp = 760)
@Composable
private fun EmptyPreviewPreview() {
    MarkyPreviewPanels {
        MarkyEmptyState(glyph = "¶", title = "Nothing to preview", body = "This note is empty. Switch to Edit and start writing.") {
            MarkyButton(text = "Edit note", onClick = {}, variant = MarkyButtonVariant.Secondary, icon = MarkyIcons.Edit)
        }
    }
}

@Preview(name = "Desktop · no notes (main pane)", widthDp = 760)
@Composable
private fun EmptyNotesDesktopPreview() {
    MarkyPreviewPanels {
        MarkyEmptyState(
            glyph = "#",
            title = "No notes yet",
            body = "Write your first note in Markdown, or import a .md file from another editor.",
        ) {
            MarkyButton(text = "New note", onClick = {}, icon = MarkyIcons.Plus)
            MarkyButton(text = "Import .md file", onClick = {}, variant = MarkyButtonVariant.Secondary, icon = MarkyIcons.Import)
        }
    }
}

@Preview(name = "Desktop · sidebar, small", widthDp = 680)
@Composable
private fun EmptySmallPreview() {
    MarkyPreviewPanels(panelWidth = 320.dp) {
        MarkyEmptyState(
            glyph = "0",
            title = "No notes match “bananas”",
            body = "Check the spelling or try fewer words. Search looks through every title and all note text.",
            size = MarkyEmptyStateSize.Small,
        )
    }
}

@Preview(name = "Desktop · no note open", widthDp = 760)
@Composable
private fun EmptyNoNotePreview() {
    MarkyPreviewPanels {
        MarkyEmptyState(glyph = "↵", title = "No note open", body = "Choose a note from the list, or start a new one.")
    }
}

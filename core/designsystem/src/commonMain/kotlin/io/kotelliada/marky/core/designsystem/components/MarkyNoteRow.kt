package io.kotelliada.marky.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.kotelliada.marky.core.designsystem.icons.MarkyIcons
import io.kotelliada.marky.core.designsystem.interaction.FocusRingInset
import io.kotelliada.marky.core.designsystem.interaction.MarkyInteraction
import io.kotelliada.marky.core.designsystem.interaction.collectMarkyInteraction
import io.kotelliada.marky.core.designsystem.interaction.markyFocusRing
import io.kotelliada.marky.core.designsystem.preview.ForcedInteraction
import io.kotelliada.marky.core.designsystem.preview.MarkyPreviewPanels
import io.kotelliada.marky.core.designsystem.resources.Res
import io.kotelliada.marky.core.designsystem.resources.no_text_yet
import io.kotelliada.marky.core.designsystem.resources.pinned
import io.kotelliada.marky.core.designsystem.resources.untitled
import io.kotelliada.marky.core.designsystem.theme.MarkyColors
import io.kotelliada.marky.core.designsystem.theme.MarkyDimens
import io.kotelliada.marky.core.designsystem.theme.MarkyMotion
import io.kotelliada.marky.core.designsystem.theme.MarkyShapes
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

/**
 * Phone: full-bleed row with a hairline on top, two-line preview.
 * Desktop: rounded row in the 320 dp sidebar, one-line preview; selected uses accent-soft.
 * In the desktop sidebar, put 2 dp between rows.
 */
enum class MarkyNoteRowLayout { Phone, Desktop }

/**
 * One note in the list or in search results: title, time since the last edit, a plain-text
 * preview (or search snippet) and the pin indicator. An empty [title] shows "Untitled" and an
 * empty [preview] "No text yet". Use [markyHighlight] to mark search matches.
 *
 * [isJustImported] shows the accent-soft highlight that fades over 2.4 s.
 */
@Composable
fun MarkyNoteRow(
    title: AnnotatedString,
    preview: AnnotatedString,
    timeLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPinned: Boolean = false,
    layout: MarkyNoteRowLayout = MarkyNoteRowLayout.Phone,
    isSelected: Boolean = false,
    isJustImported: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interaction = source.collectMarkyInteraction()
    val colors = MarkyTheme.colors
    val desktop = layout == MarkyNoteRowLayout.Desktop
    val shape = if (desktop) MarkyShapes.md else RectangleShape
    val background = markyNoteRowBackground(interaction, isSelected, colors)
    val importHighlight = rememberImportHighlight(isJustImported)

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .semantics { selected = isSelected }
            .pointerHoverIcon(PointerIcon.Hand),
        shape = shape,
        color = Color.Transparent,
        contentColor = colors.text,
        interactionSource = source,
    ) {
        Column(
            modifier = Modifier
                .drawBehind {
                    drawRect(background)
                    val alpha = importHighlight.value
                    if (alpha > 0f) drawRect(colors.accentSoft, alpha = alpha)
                    if (!desktop) drawLine(colors.border, Offset(0f, 0.5.dp.toPx()), Offset(size.width, 0.5.dp.toPx()), 1.dp.toPx())
                }
                .markyFocusRing(interaction.focused, shape, colors.focus, offset = FocusRingInset)
                .then(if (desktop) Modifier else Modifier.heightIn(min = MarkyDimens.rowMinHeight))
                .padding(if (desktop) DesktopPadding else PhonePadding),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (isPinned) {
                    MarkyIcon(
                        icon = MarkyIcons.PinFilled,
                        contentDescription = stringResource(Res.string.pinned),
                        size = 14.dp,
                        tint = colors.accent,
                    )
                }
                val untitled = title.isEmpty()
                MarkyText(
                    text = if (untitled) AnnotatedString(stringResource(Res.string.untitled)) else title,
                    style = MarkyTheme.typography.titleS,
                    color = if (untitled) colors.text3 else colors.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                MarkyText(text = timeLabel, style = MarkyTheme.typography.caption, color = colors.text3, maxLines = 1)
            }
            Spacer(Modifier.height(3.dp))
            val empty = preview.isEmpty()
            MarkyText(
                text = if (empty) AnnotatedString(stringResource(Res.string.no_text_yet)) else preview,
                style = MarkyTheme.typography.bodyS,
                color = if (empty) colors.text3 else colors.text2,
                maxLines = if (desktop) 1 else 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** [MarkyNoteRow] for plain (unhighlighted) text. */
@Composable
fun MarkyNoteRow(
    title: String,
    preview: String,
    timeLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPinned: Boolean = false,
    layout: MarkyNoteRowLayout = MarkyNoteRowLayout.Phone,
    isSelected: Boolean = false,
    isJustImported: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    MarkyNoteRow(
        title = AnnotatedString(title),
        preview = AnnotatedString(preview),
        timeLabel = timeLabel,
        onClick = onClick,
        modifier = modifier,
        isPinned = isPinned,
        layout = layout,
        isSelected = isSelected,
        isJustImported = isJustImported,
        interactionSource = interactionSource,
    )
}

private val PhonePadding = PaddingValues(start = MarkyDimens.gutterPhone, top = 12.dp, end = MarkyDimens.gutterPhone, bottom = 13.dp)
private val DesktopPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)

/** Selected beats pressed, pressed beats hover. */
internal fun markyNoteRowBackground(interaction: MarkyInteraction, selected: Boolean, c: MarkyColors): Color = when {
    selected -> c.accentSoft
    interaction.pressed -> c.statePressed
    interaction.hovered -> c.stateHover
    else -> Color.Transparent
}

/** Alpha of the just-imported highlight: holds for 35 % of 2.4 s, then eases out. */
@Composable
private fun rememberImportHighlight(active: Boolean): Animatable<Float, *> {
    val alpha = remember { Animatable(if (active) 1f else 0f) }
    val inspecting = LocalInspectionMode.current
    LaunchedEffect(active) {
        if (!active) {
            alpha.snapTo(0f)
        } else if (!inspecting) {
            alpha.snapTo(1f)
            val hold = (MarkyMotion.ROW_HIGHLIGHT_MS * MarkyMotion.ROW_HIGHLIGHT_HOLD_FRACTION).toLong()
            delay(hold)
            alpha.animateTo(0f, tween((MarkyMotion.ROW_HIGHLIGHT_MS - hold).toInt(), easing = MarkyMotion.ease))
        }
    }
    return alpha
}

// ---------- Previews ----------

private const val LISBON_PREVIEW = "Flights booked for 12 Dec. Hotel is near Príncipe Real. To sort Book flights Tram 28 pass Sintra day trip Pastéis de Belém opens at 8am. Go early."

@Composable
private fun PhoneRow(
    title: String = "Lisbon trip",
    preview: String = LISBON_PREVIEW,
    time: String = "3h",
    pinned: Boolean = false,
    justImported: Boolean = false,
) = MarkyNoteRow(title = title, preview = preview, timeLabel = time, onClick = {}, isPinned = pinned, isJustImported = justImported)

@Preview(name = "Row · default", widthDp = 760)
@Composable
private fun RowDefaultPreview() {
    MarkyPreviewPanels(padding = 0.dp) { PhoneRow() }
}

@Preview(name = "Row · pinned", widthDp = 760)
@Composable
private fun RowPinnedPreview() {
    MarkyPreviewPanels(padding = 0.dp) {
        PhoneRow(title = "Groceries", preview = "Oat milk Lemons Sourdough Coffee beans Feta Cherry tomatoes", time = "Yesterday", pinned = true)
    }
}

@Preview(name = "Row · hover, pressed, focused", widthDp = 760)
@Composable
private fun RowInteractionPreview() {
    MarkyPreviewPanels(padding = 0.dp) {
        ForcedInteraction(MarkyInteraction.Hovered) { PhoneRow() }
        ForcedInteraction(MarkyInteraction.Pressed) { PhoneRow() }
        ForcedInteraction(MarkyInteraction.Focused) { PhoneRow() }
    }
}

@Preview(name = "Row · untitled, no text", widthDp = 760)
@Composable
private fun RowUntitledPreview() {
    MarkyPreviewPanels(padding = 0.dp) { PhoneRow(title = "", preview = "", time = "Now") }
}

@Preview(name = "Row · long title", widthDp = 760)
@Composable
private fun RowLongTitlePreview() {
    MarkyPreviewPanels(padding = 0.dp) {
        PhoneRow(
            title = "Notes from the product offsite in Porto, day two afternoon",
            preview = "Done: import flow, snackbar copy. Next: export as .md, delete confirmation dialog. Release notes draft for v1.2 is ready for review.",
            time = "2w",
        )
    }
}

@Preview(name = "Row · just imported (start of fade)", widthDp = 760)
@Composable
private fun RowJustImportedPreview() {
    MarkyPreviewPanels(padding = 0.dp) {
        PhoneRow(title = "Meeting notes", preview = "Agenda Roadmap for December Hiring update Send the summary to the team", time = "Now", justImported = true)
    }
}

@Composable
private fun DesktopRow(title: String = "Lisbon trip", selected: Boolean = false, pinned: Boolean = false) = MarkyNoteRow(
    title = title,
    preview = LISBON_PREVIEW,
    timeLabel = "3h",
    onClick = {},
    layout = MarkyNoteRowLayout.Desktop,
    isSelected = selected,
    isPinned = pinned,
)

@Preview(name = "Desktop row · default, hover, selected, focused", widthDp = 680)
@Composable
private fun RowDesktopPreview() {
    MarkyPreviewPanels(panelWidth = 320.dp, padding = 8.dp) {
        DesktopRow()
        ForcedInteraction(MarkyInteraction.Hovered) { DesktopRow() }
        DesktopRow(title = "Welcome to Marky", selected = true, pinned = true)
        ForcedInteraction(MarkyInteraction.Focused) { DesktopRow() }
    }
}

@Composable
private fun ResultRow(title: String, preview: String, time: String, query: List<String>, pinned: Boolean = false) {
    fun ranges(text: String) = query.flatMap { word ->
        Regex(Regex.escape(word), RegexOption.IGNORE_CASE).findAll(text).map { it.range }.toList()
    }
    MarkyNoteRow(
        title = markyHighlight(title, ranges(title)),
        preview = markyHighlight(preview, ranges(preview)),
        timeLabel = time,
        onClick = {},
        isPinned = pinned,
    )
}

@Preview(name = "Search result · title match", widthDp = 760)
@Composable
private fun ResultTitleMatchPreview() {
    MarkyPreviewPanels(padding = 0.dp) {
        ResultRow(
            title = "Book notes: Deep Work",
            preview = "Key ideas Deep work is focused, cognitively demanding work. Shallow work is logistical and often done while distracted.",
            time = "2d",
            query = listOf("notes"),
        )
    }
}

@Preview(name = "Search result · text match, snippet around the hit", widthDp = 760)
@Composable
private fun ResultTextMatchPreview() {
    MarkyPreviewPanels(padding = 0.dp) {
        ResultRow(
            title = "Welcome to Marky",
            preview = "Marky keeps your notes as plain Markdown. Type the syntax yourself, or use the toolbar above the keyboard.",
            time = "2m",
            query = listOf("notes"),
            pinned = true,
        )
    }
}

@Preview(name = "Search result · several words", widthDp = 760)
@Composable
private fun ResultSeveralWordsPreview() {
    MarkyPreviewPanels(padding = 0.dp) {
        ResultRow(title = "Lisbon trip", preview = LISBON_PREVIEW, time = "3h", query = listOf("tram", "pass"))
    }
}


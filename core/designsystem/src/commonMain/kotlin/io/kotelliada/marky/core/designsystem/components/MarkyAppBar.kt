package io.kotelliada.marky.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.kotelliada.marky.core.designsystem.icons.MarkyIcons
import io.kotelliada.marky.core.designsystem.interaction.MarkyInteraction
import io.kotelliada.marky.core.designsystem.preview.ForcedInteraction
import io.kotelliada.marky.core.designsystem.preview.MarkyPreviewPanels
import io.kotelliada.marky.core.designsystem.theme.MarkyDimens
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme

/**
 * A 56 dp bar: the list, note and search app bars on phones, and the desktop sidebar header
 * and note pane bar ([MarkyAppBarDefaults] has the padding for each). Content is laid out in
 * a row with 4 dp gaps; use [MarkyAppBarTitle] / [MarkySidebarTitle] for titles and
 * `Modifier.weight(1f)` to push actions to the end.
 *
 * [showDivider]: the hairline under the bar appears only once content scrolls beneath it
 * (the desktop pane bar always shows it).
 *
 * Built on Row rather than Material's TopAppBar, which fixes 4 dp edge padding and a 16 dp
 * title inset; Margin uses 8 dp padding, 4 dp gaps and a title 20 dp from the edge.
 */
@Composable
fun MarkyAppBar(
    modifier: Modifier = Modifier,
    showDivider: Boolean = false,
    contentPadding: PaddingValues = MarkyAppBarDefaults.PhonePadding,
    windowInsets: WindowInsets = WindowInsets.statusBars,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = MarkyTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.bg)
            .windowInsetsPadding(windowInsets)
            .height(MarkyDimens.appBarHeight)
            .drawBehind {
                if (showDivider) {
                    val y = size.height - 0.5.dp.toPx()
                    drawLine(colors.border, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                }
            }
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        content = content,
    )
}

object MarkyAppBarDefaults {
    /** Notes list and note app bars. */
    val PhonePadding = PaddingValues(horizontal = 8.dp)

    /** Search app bar: the field ends 16 dp from the edge. */
    val SearchPadding = PaddingValues(start = 8.dp, end = 16.dp)

    /** Desktop sidebar header ("Notes" plus actions). */
    val SidebarHeaderPadding = PaddingValues(start = 20.dp, end = 10.dp)

    /** Desktop sidebar header while searching (starts with Back). */
    val SidebarSearchPadding = PaddingValues(start = 8.dp, end = 10.dp)

    /** Desktop note pane bar. */
    val PaneBarPadding = PaddingValues(start = 20.dp, end = 12.dp)
}

/** The screen title in a phone app bar ("Notes"): display style, 12 dp in from the bar padding. */
@Composable
fun RowScope.MarkyAppBarTitle(text: String, modifier: Modifier = Modifier) {
    MarkyText(
        text = text,
        style = MarkyTheme.typography.display,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.weight(1f).padding(start = 12.dp).semantics { heading() },
    )
}

/** The title in the desktop sidebar header: title-m. */
@Composable
fun RowScope.MarkySidebarTitle(text: String, modifier: Modifier = Modifier) {
    MarkyText(
        text = text,
        style = MarkyTheme.typography.titleM,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.weight(1f).semantics { heading() },
    )
}

/** The phone bottom bar: hairline on top, holds the L block New note button. */
@Composable
fun MarkyBottomBar(
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.navigationBars,
    content: @Composable () -> Unit,
) {
    val colors = MarkyTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.bg)
            .drawBehind { drawLine(colors.border, Offset(0f, 0.5.dp.toPx()), Offset(size.width, 0.5.dp.toPx()), 1.dp.toPx()) }
            .windowInsetsPadding(windowInsets)
            .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 16.dp),
    ) { content() }
}

// ---------- Previews ----------

@Preview(name = "Notes list", widthDp = 760)
@Composable
private fun AppBarListPreview() {
    MarkyPreviewPanels(padding = 0.dp) {
        MarkyAppBar(windowInsets = WindowInsets(0)) {
            MarkyAppBarTitle("Notes")
            MarkyIconButton(MarkyIcons.Import, "Import .md file", onClick = {})
        }
    }
}

@Composable
private fun NoteBar(editing: Boolean, pinned: Boolean, scrolled: Boolean, menuOpen: Boolean = false) {
    MarkyAppBar(showDivider = scrolled, windowInsets = WindowInsets(0)) {
        MarkyIconButton(MarkyIcons.Back, "Back to notes", onClick = {})
        MarkySegmentedControl(listOf("Edit", "Preview"), selectedIndex = if (editing) 0 else 1, onSelect = {}, modifier = Modifier.padding(start = 4.dp))
        androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
        MarkyIconToggleButton(MarkyIcons.Pin, MarkyIcons.PinFilled, if (pinned) "Unpin" else "Pin to top", checked = pinned, onCheckedChange = {})
        if (menuOpen) {
            ForcedInteraction(MarkyInteraction.Pressed) { MarkyIconButton(MarkyIcons.More, "More actions", onClick = {}) }
        } else {
            MarkyIconButton(MarkyIcons.More, "More actions", onClick = {})
        }
    }
}

@Preview(name = "Note · preview (pinned)", widthDp = 760)
@Composable
private fun AppBarNotePreviewPreview() {
    MarkyPreviewPanels(padding = 0.dp) { NoteBar(editing = false, pinned = true, scrolled = false) }
}

@Preview(name = "Note · edit, scrolled", widthDp = 760)
@Composable
private fun AppBarNoteEditScrolledPreview() {
    MarkyPreviewPanels(padding = 0.dp) { NoteBar(editing = true, pinned = false, scrolled = true) }
}

@Preview(name = "Note · menu open", widthDp = 760)
@Composable
private fun AppBarNoteMenuOpenPreview() {
    MarkyPreviewPanels(padding = 0.dp) { NoteBar(editing = false, pinned = false, scrolled = false, menuOpen = true) }
}

@Preview(name = "Search", widthDp = 760)
@Composable
private fun AppBarSearchPreview() {
    MarkyPreviewPanels(padding = 0.dp) {
        MarkyAppBar(contentPadding = MarkyAppBarDefaults.SearchPadding, windowInsets = WindowInsets(0)) {
            MarkyIconButton(MarkyIcons.Back, "Back to notes", onClick = {})
            ForcedInteraction(MarkyInteraction.Focused) {
                MarkySearchField(query = "lisbon", onQueryChange = {}, onClearClick = {}, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview(name = "Bottom bar", widthDp = 760)
@Composable
private fun BottomBarPreview() {
    MarkyPreviewPanels(padding = 0.dp) {
        MarkyBottomBar(windowInsets = WindowInsets(0)) {
            MarkyButton(text = "New note", onClick = {}, size = MarkyButtonSize.Large, icon = MarkyIcons.Plus, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview(name = "List search entry", widthDp = 760)
@Composable
private fun ListSearchEntryPreview() {
    MarkyPreviewPanels(padding = 0.dp) {
        MarkySearchEntry(onClick = {}, modifier = Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 8.dp))
    }
}

@Preview(name = "Desktop · sidebar header and pane bar", widthDp = 1100)
@Composable
private fun DesktopBarsPreview() {
    MarkyPreviewPanels(panelWidth = 540.dp, padding = 0.dp) {
        MarkyAppBar(contentPadding = MarkyAppBarDefaults.SidebarHeaderPadding, windowInsets = WindowInsets(0)) {
            MarkySidebarTitle("Notes")
            MarkyIconButton(MarkyIcons.Import, "Import .md file", onClick = {}, shortcut = MarkyShortcut.ctrl('o'))
            MarkyIconButton(MarkyIcons.Plus, "New note", onClick = {}, shortcut = MarkyShortcut.ctrl('n'))
        }
        MarkyAppBar(showDivider = true, contentPadding = MarkyAppBarDefaults.PaneBarPadding, windowInsets = WindowInsets(0)) {
            MarkySegmentedControl(listOf("Edit", "Preview"), selectedIndex = 1, onSelect = {})
            androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
            MarkyIconToggleButton(MarkyIcons.Pin, MarkyIcons.PinFilled, "Pin to top", checked = false, onCheckedChange = {})
            MarkyIconButton(MarkyIcons.More, "More actions", onClick = {})
        }
    }
}

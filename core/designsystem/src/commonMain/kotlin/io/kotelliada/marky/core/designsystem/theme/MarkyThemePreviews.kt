package io.kotelliada.marky.core.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.kotelliada.marky.core.designsystem.components.MarkyText
import io.kotelliada.marky.core.designsystem.preview.MarkyPreviewPanels

@Preview(name = "Type scale", widthDp = 760)
@Composable
private fun TypeScalePreview() {
    MarkyPreviewPanels {
        val t = MarkyTheme.typography
        listOf<Pair<TextStyle, String>>(
            t.display to "Notes",
            t.titleL to "Lisbon trip",
            t.titleM to "To sort",
            t.titleS to "Book notes: Deep Work",
            t.bodyL to "Flights booked for 12 Dec.",
            t.bodyM to "This can’t be undone.",
            t.bodyS to "Oat milk, lemons, sourdough",
            t.labelL to "New note",
            t.labelM to "Export as .md",
            t.control to "Edit  Preview",
            t.caption to "3h · Edited 3 h ago",
            t.overline to "PINNED",
            t.code to "val note = Note()",
            t.editor to "## To sort",
        ).forEach { (style, sample) -> MarkyText(text = sample, style = style) }
    }
}

@Preview(name = "Colors", widthDp = 760)
@Composable
private fun ColorsPreview() {
    MarkyPreviewPanels {
        val c = MarkyTheme.colors
        val groups = listOf(
            listOf(c.bg, c.surface, c.surface2, c.float, c.border, c.borderStrong),
            listOf(c.text, c.text2, c.text3, c.textDisabled, c.marker),
            listOf(c.accent, c.accentHover, c.accentPressed, c.onAccent, c.accentSoft, c.onAccentSoft, c.focus, c.selection),
            listOf(c.danger, c.dangerHover, c.dangerPressed, c.onDanger, c.dangerSoft),
            listOf(c.highlight, c.onHighlight, c.inverse, c.onInverse, c.inverseAccent, c.scrim),
            listOf(c.stateHover, c.statePressed, c.shadowColor),
            listOf(c.codeBg, c.codeText, c.codeKeyword, c.codeString, c.codeComment, c.codeNumber),
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            groups.forEach { group -> Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { group.forEach { Swatch(it) } } }
        }
    }
}

@Preview(name = "Shapes and elevation", widthDp = 760)
@Composable
private fun ShapesPreview() {
    MarkyPreviewPanels {
        val c = MarkyTheme.colors
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            listOf(MarkyShapes.xs, MarkyShapes.sm, MarkyShapes.md, MarkyShapes.lg).forEach { shape ->
                Box(Modifier.size(40.dp).border(1.dp, c.borderStrong, shape))
            }
            Box(
                Modifier.size(64.dp)
                    .markyFloatShadow(MarkyShapes.lg, c.shadowColor)
                    .background(c.float, MarkyShapes.lg)
                    .border(1.dp, c.border, MarkyShapes.lg),
            )
        }
    }
}

@Composable
private fun Swatch(color: Color) {
    Box(
        Modifier.size(32.dp)
            .background(color, MarkyShapes.xs)
            .border(1.dp, MarkyTheme.colors.border, MarkyShapes.xs),
    )
}

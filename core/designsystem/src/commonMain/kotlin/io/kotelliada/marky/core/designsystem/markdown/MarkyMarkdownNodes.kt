package io.kotelliada.marky.core.designsystem.markdown

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.kotelliada.marky.core.designsystem.components.MarkyText
import io.kotelliada.marky.core.designsystem.theme.MarkyColors
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme
import io.kotelliada.marky.core.designsystem.theme.MarkyTypography

/**
 * The rendered note body (`.mk-md`): blocks 12 dp apart. Fill it with the node composables
 * below, built from the JetBrains Markdown AST.
 */
@Composable
fun MarkyMarkdownColumn(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
}

/**
 * Heading level 1–3. The `#` marker stays faintly in view (Plex Mono, marker color), so preview
 * and edit mode read as one document. Headings sit 8 dp further from the block above unless
 * [isFirst], and 2 dp closer to the block below.
 */
@Composable
fun MarkyMarkdownHeading(
    level: Int,
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    isFirst: Boolean = false,
) {
    val typography = MarkyTheme.typography
    val colors = MarkyTheme.colors
    val style = when (level) {
        1 -> typography.titleL
        2 -> typography.titleM
        else -> typography.titleS
    }
    MarkyText(
        text = headingWithMarker(level, text, typography, colors),
        style = style,
        color = colors.text,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = if (isFirst) 0.dp else 8.dp)
            .pullUp(2.dp)
            .semantics { heading() },
    )
}

/** `.mk-md-mark`: 0.6 em Plex Mono 500, raised 0.18 em, 0.55 em gap after it. */
internal fun headingWithMarker(level: Int, text: AnnotatedString, t: MarkyTypography, c: MarkyColors): AnnotatedString {
    require(level in 1..3) { "Margin styles heading levels 1 to 3, got $level" }
    return buildAnnotatedString {
        withStyle(
            SpanStyle(
                fontFamily = t.mono,
                fontWeight = FontWeight.Medium,
                fontSize = 0.6.em,
                color = c.marker,
                letterSpacing = 0.04.em,
                baselineShift = BaselineShift(0.18f),
            ),
        ) { append("#".repeat(level)) }
        // The gap is 0.55 em of the marker = 0.33 em of the heading. A Plex Mono space is 0.6 of
        // its font size wide, so a space at 0.55 em of the heading is exactly that gap.
        withStyle(SpanStyle(fontFamily = t.mono, fontSize = 0.55.em)) { append(" ") }
        append(text)
    }
}

/** Paragraph: body-l. */
@Composable
fun MarkyMarkdownParagraph(text: AnnotatedString, modifier: Modifier = Modifier) {
    MarkyText(text = text, style = MarkyTheme.typography.bodyL, color = MarkyTheme.colors.text, modifier = modifier.fillMaxWidth())
}

/** Items of one list, 4 dp apart. Nested items go in the same column with a higher depth. */
@Composable
fun MarkyMarkdownList(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp), content = content)
}

/** Bulleted list item: an en dash in text-3 Plex Mono. [depth] 0 is top level. */
@Composable
fun MarkyMarkdownBulletItem(text: AnnotatedString, modifier: Modifier = Modifier, depth: Int = 0) {
    ListItem(marker = "–", markerWidth = 18, markerStyleSize = null, depth = depth, text = text, modifier = modifier, alignEnd = false)
}

/** Numbered list item: the number in Plex Mono 500 12, text-3. [depth] 0 is top level. */
@Composable
fun MarkyMarkdownNumberedItem(number: Int, text: AnnotatedString, modifier: Modifier = Modifier, depth: Int = 0) {
    ListItem(marker = "$number.", markerWidth = 28, markerStyleSize = 12, depth = depth, text = text, modifier = modifier, alignEnd = true)
}

@Composable
private fun ListItem(
    marker: String,
    markerWidth: Int,
    markerStyleSize: Int?,
    depth: Int,
    text: AnnotatedString,
    modifier: Modifier,
    alignEnd: Boolean,
) {
    val typography = MarkyTheme.typography
    val body = typography.bodyL
    val markerStyle = if (markerStyleSize == null) {
        body.copy(fontFamily = typography.mono)
    } else {
        body.copy(fontFamily = typography.mono, fontWeight = FontWeight.Medium, fontSize = markerStyleSize.sp)
    }
    Row(modifier = modifier.fillMaxWidth().padding(start = (depth * markerWidth).dp)) {
        MarkyText(
            text = marker,
            style = markerStyle,
            color = MarkyTheme.colors.text3,
            textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
            maxLines = 1,
            softWrap = false,
            // The numbered marker is "1. " in 12 sp Plex Mono; its trailing space (0.6 em) becomes end padding.
            modifier = Modifier.width(markerWidth.dp).alignByBaseline().padding(start = if (alignEnd) 0.dp else 1.dp, end = if (alignEnd) 7.2.dp else 0.dp),
        )
        MarkyText(text = text, style = body, color = MarkyTheme.colors.text, modifier = Modifier.weight(1f).alignByBaseline())
    }
}

/** Quote: 2 dp accent rule, 14 dp inset, text-2. */
@Composable
fun MarkyMarkdownQuote(text: AnnotatedString, modifier: Modifier = Modifier) {
    val colors = MarkyTheme.colors
    Row(modifier = modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Box(Modifier.width(2.dp).fillMaxHeight().background(colors.accent))
        MarkyText(
            text = text,
            style = MarkyTheme.typography.bodyL,
            color = colors.text2,
            modifier = Modifier.weight(1f).padding(start = 14.dp, top = 2.dp, bottom = 2.dp),
        )
    }
}

/** Reports [amount] less height so the next block sits that much closer (CSS negative margin). */
private fun Modifier.pullUp(amount: androidx.compose.ui.unit.Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val height = (placeable.height - amount.roundToPx()).coerceAtLeast(0)
    layout(placeable.width, height) { placeable.place(0, 0) }
}

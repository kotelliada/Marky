package io.kotelliada.marky.core.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme

/**
 * Marks search matches (`.mk-hl`) in [text]: highlight background, on-highlight text.
 * [ranges] are character ranges (inclusive, as produced by `indexOf` + length − 1); they may
 * overlap, touch, be unsorted or run past the text, and are never split inside a surrogate pair.
 */
@Composable
@ReadOnlyComposable
fun markyHighlight(text: String, ranges: List<IntRange>): AnnotatedString {
    val colors = MarkyTheme.colors
    return highlight(text, ranges, SpanStyle(color = colors.onHighlight, background = colors.highlight))
}

internal fun highlight(text: String, ranges: List<IntRange>, style: SpanStyle): AnnotatedString {
    val merged = normalizeRanges(text, ranges)
    return buildAnnotatedString {
        append(text)
        merged.forEach { addStyle(style, it.first, it.last + 1) }
    }
}

/** Clamps, widens to whole surrogate pairs, sorts and merges overlapping or touching ranges. */
internal fun normalizeRanges(text: String, ranges: List<IntRange>): List<IntRange> {
    if (text.isEmpty()) return emptyList()
    val clamped = ranges.mapNotNull { range ->
        if (range.isEmpty() || range.last < 0 || range.first >= text.length) return@mapNotNull null
        var start = range.first.coerceAtLeast(0)
        var end = range.last.coerceAtMost(text.lastIndex)
        if (start > 0 && text[start].isLowSurrogate() && text[start - 1].isHighSurrogate()) start--
        if (end < text.lastIndex && text[end].isHighSurrogate() && text[end + 1].isLowSurrogate()) end++
        start..end
    }.sortedBy { it.first }
    val merged = mutableListOf<IntRange>()
    clamped.forEach { range ->
        val last = merged.lastOrNull()
        if (last != null && range.first <= last.last + 1) {
            merged[merged.lastIndex] = last.first..maxOf(last.last, range.last)
        } else {
            merged += range
        }
    }
    return merged
}

package io.kotelliada.marky.core.designsystem.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import kotlin.test.Test

class HighlightTest {

    // Break vectors for normalizeRanges / highlight:
    // - empty text, empty range list, empty IntRange (5..4)
    // - ranges entirely before 0 or past the end, partly out of bounds
    // - unsorted, duplicate, nested, overlapping and touching ranges (adjacent words)
    // - a boundary that splits an emoji's surrogate pair (would render a broken glyph)
    // - multi-codepoint text (combining marks, Príncipe) must keep its exact characters
    // - the text itself must never change, only gain styles

    @Test
    fun `empty inputs give no ranges`() {
        assertThat(normalizeRanges("", listOf(0..3))).isEmpty()
        assertThat(normalizeRanges("tram", emptyList())).isEmpty()
        assertThat(normalizeRanges("tram", listOf(5..4))).isEmpty()
    }

    @Test
    fun `ranges outside the text are dropped and partial ones clamped`() {
        assertThat(normalizeRanges("tram pass", listOf(-5..-1, 20..30, -2..3, 6..99))).containsExactly(0..3, 6..8)
    }

    @Test
    fun `unsorted overlapping and touching ranges merge`() {
        assertThat(normalizeRanges("abcdefghij", listOf(6..7, 0..2, 1..3, 4..5, 6..6))).containsExactly(0..7)
    }

    @Test
    fun `separate ranges stay separate and sorted`() {
        assertThat(normalizeRanges("tram 28 pass", listOf(8..11, 0..3))).containsExactly(0..3, 8..11)
    }

    @Test
    fun `a boundary inside a surrogate pair widens to the whole pair`() {
        val text = "a👍b" // 👍 is two chars: indices 1 and 2
        assertThat(normalizeRanges(text, listOf(2..3))).containsExactly(1..3)
        assertThat(normalizeRanges(text, listOf(0..1))).containsExactly(0..2)
    }

    @Test
    fun `highlight keeps the text and styles each merged range once`() {
        val style = SpanStyle(background = Color.Yellow)
        val text = "Hotel near Príncipe Real" // é as e + U+0301
        val result = highlight(text, listOf(11..21, 13..15), style)
        assertThat(result.text).isEqualTo(text)
        assertThat(result.spanStyles.map { it.start until it.end }).containsExactly(11 until 22)
        assertThat(result.spanStyles.single().item).isEqualTo(style)
    }
}

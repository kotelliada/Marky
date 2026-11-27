package io.kotelliada.marky.core.data.search

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.kotelliada.marky.core.database.FtsHighlight
import io.kotelliada.marky.core.domain.note.HighlightedText
import kotlin.test.Test

class HighlightParserTest {

    private val s = FtsHighlight.START
    private val e = FtsHighlight.END

    @Test
    fun `text without markers has no highlights`() {
        assertThat(HighlightParser.parse("plain text…")).isEqualTo(HighlightedText("plain text…"))
    }

    @Test
    fun `empty snippet stays empty`() {
        assertThat(HighlightParser.parse("")).isEqualTo(HighlightedText(""))
    }

    @Test
    fun `markers become ranges over the text without markers`() {
        assertThat(HighlightParser.parse("${s}Book$e the ${s}hotel$e…")).isEqualTo(
            HighlightedText("Book the hotel…", listOf(0..3, 9..13)),
        )
    }

    @Test
    fun `adjacent highlights stay separate`() {
        assertThat(HighlightParser.parse("${s}ab$e${s}cd$e")).isEqualTo(HighlightedText("abcd", listOf(0..1, 2..3)))
    }

    @Test
    fun `empty highlight is dropped`() {
        assertThat(HighlightParser.parse("a$s${e}b")).isEqualTo(HighlightedText("ab"))
    }

    @Test
    fun `end marker without start is removed and highlights nothing`() {
        assertThat(HighlightParser.parse("a${e}b")).isEqualTo(HighlightedText("ab"))
    }

    @Test
    fun `start marker without end is removed and highlights nothing`() {
        assertThat(HighlightParser.parse("a${s}bc")).isEqualTo(HighlightedText("abc"))
    }

    @Test
    fun `repeated start marker keeps the first start`() {
        assertThat(HighlightParser.parse("${s}a${s}b${e}c")).isEqualTo(HighlightedText("abc", listOf(0..1)))
    }

    @Test
    fun `ranges count utf16 units so emoji are never split`() {
        val result = HighlightParser.parse("x ${s}👍🏽👨‍👩‍👧‍👦$e y")
        assertThat(result.highlights.single().let { result.text.substring(it) }).isEqualTo("👍🏽👨‍👩‍👧‍👦")
    }
}

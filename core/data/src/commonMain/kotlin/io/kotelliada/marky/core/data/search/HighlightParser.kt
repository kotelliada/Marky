package io.kotelliada.marky.core.data.search

import io.kotelliada.marky.core.database.FtsHighlight
import io.kotelliada.marky.core.domain.note.HighlightedText

/** Turns an FTS `snippet()` with [FtsHighlight] markers into text plus highlight ranges. */
internal object HighlightParser {

    private val START = FtsHighlight.START.single()
    private val END = FtsHighlight.END.single()

    fun parse(snippet: String): HighlightedText {
        val text = StringBuilder(snippet.length)
        val highlights = mutableListOf<IntRange>()
        var highlightStart = -1
        for (char in snippet) {
            when (char) {
                START -> if (highlightStart < 0) highlightStart = text.length
                END -> {
                    if (highlightStart in 0 until text.length) highlights += highlightStart until text.length
                    highlightStart = -1
                }
                else -> text.append(char)
            }
        }
        // A start marker without an end marker highlights nothing.
        return HighlightedText(text.toString(), highlights)
    }
}

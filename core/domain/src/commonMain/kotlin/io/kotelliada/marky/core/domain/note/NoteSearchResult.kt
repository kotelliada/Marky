package io.kotelliada.marky.core.domain.note

import kotlin.time.Instant

/**
 * One note found by search.
 *
 * @property snippet a short excerpt of the note's plain text around the matches.
 * @property titleMatch true when a search word matches the title; these results come first.
 */
data class NoteSearchResult(
    val id: Long,
    val title: HighlightedText,
    val snippet: HighlightedText,
    val pinned: Boolean,
    val editedAt: Instant,
    val titleMatch: Boolean,
)

package io.kotelliada.marky.core.database.dao

/**
 * One search hit. Both snippets carry [io.kotelliada.marky.core.database.FtsHighlight] markers.
 *
 * @property titleMatch true when at least one search word matches the title.
 */
data class NoteSearchRow(
    val id: Long,
    val pinned: Boolean,
    val editedAt: Long,
    val titleSnippet: String,
    val bodySnippet: String,
    val titleMatch: Boolean,
)

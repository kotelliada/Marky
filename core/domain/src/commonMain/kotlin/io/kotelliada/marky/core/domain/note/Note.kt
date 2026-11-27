package io.kotelliada.marky.core.domain.note

import kotlin.time.Instant

/**
 * @property body raw Markdown, as typed.
 * @property plainText [body] without Markdown syntax, on one line; used for list previews.
 * @property editedAt last change to the title or body. Pinning is not an edit.
 */
data class Note(
    val id: Long,
    val title: String,
    val body: String,
    val plainText: String,
    val pinned: Boolean,
    val createdAt: Instant,
    val editedAt: Instant,
)

package io.kotelliada.marky.core.data.note

import io.kotelliada.marky.core.data.search.HighlightParser
import io.kotelliada.marky.core.database.dao.NoteSearchRow
import io.kotelliada.marky.core.database.entity.NoteEntity
import io.kotelliada.marky.core.domain.note.Note
import io.kotelliada.marky.core.domain.note.NoteSearchResult
import kotlin.time.Instant

internal fun NoteEntity.toNote(): Note = Note(
    id = id,
    title = title,
    body = body,
    plainText = bodyText,
    pinned = pinned,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    editedAt = Instant.fromEpochMilliseconds(editedAt),
)

internal fun NoteSearchRow.toNoteSearchResult(): NoteSearchResult = NoteSearchResult(
    id = id,
    title = HighlightParser.parse(titleSnippet),
    snippet = HighlightParser.parse(bodySnippet),
    pinned = pinned,
    editedAt = Instant.fromEpochMilliseconds(editedAt),
    titleMatch = titleMatch,
)

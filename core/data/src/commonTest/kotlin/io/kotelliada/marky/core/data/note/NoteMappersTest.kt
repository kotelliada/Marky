package io.kotelliada.marky.core.data.note

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.kotelliada.marky.core.database.FtsHighlight
import io.kotelliada.marky.core.database.dao.NoteSearchRow
import io.kotelliada.marky.core.database.entity.NoteEntity
import io.kotelliada.marky.core.domain.note.HighlightedText
import io.kotelliada.marky.core.domain.note.Note
import io.kotelliada.marky.core.domain.note.NoteSearchResult
import kotlin.test.Test
import kotlin.time.Instant

class NoteMappersTest {

    @Test
    fun `entity maps every field and epoch millis to instants`() {
        val entity = NoteEntity(
            id = 7,
            title = "Lisbon 🇵🇹",
            body = "# Plan\n- [ ] **hotel**",
            bodyText = "Plan hotel",
            pinned = true,
            createdAt = 0,
            editedAt = 1_795_000_000_123,
        )

        assertThat(entity.toNote()).isEqualTo(
            Note(
                id = 7,
                title = "Lisbon 🇵🇹",
                body = "# Plan\n- [ ] **hotel**",
                plainText = "Plan hotel",
                pinned = true,
                createdAt = Instant.fromEpochMilliseconds(0),
                editedAt = Instant.fromEpochMilliseconds(1_795_000_000_123),
            ),
        )
    }

    @Test
    fun `search row parses both snippets and keeps ranking fields`() {
        val s = FtsHighlight.START
        val e = FtsHighlight.END
        val row = NoteSearchRow(
            id = 3,
            pinned = false,
            editedAt = 42,
            titleSnippet = "${s}Lisbon$e trip",
            bodySnippet = "…near the ${s}lisbon$e river…",
            titleMatch = true,
        )

        assertThat(row.toNoteSearchResult()).isEqualTo(
            NoteSearchResult(
                id = 3,
                title = HighlightedText("Lisbon trip", listOf(0..5)),
                snippet = HighlightedText("…near the lisbon river…", listOf(10..15)),
                pinned = false,
                editedAt = Instant.fromEpochMilliseconds(42),
                titleMatch = true,
            ),
        )
    }
}

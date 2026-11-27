package io.kotelliada.marky.core.data.note

import androidx.sqlite.SQLiteException
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import assertk.assertions.single
import io.kotelliada.marky.core.data.testing.FakeClock
import io.kotelliada.marky.core.data.testing.FakeNoteDao
import io.kotelliada.marky.core.database.FtsHighlight
import io.kotelliada.marky.core.database.dao.NoteSearchRow
import io.kotelliada.marky.core.database.entity.NoteEntity
import io.kotelliada.marky.core.domain.note.HighlightedText
import io.kotelliada.marky.core.domain.util.DataError
import io.kotelliada.marky.core.domain.util.Result
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class RoomNoteRepositoryTest {

    private val created = Instant.fromEpochMilliseconds(1_795_000_000_000)
    private val dao = FakeNoteDao()
    private val clock = FakeClock(created)
    private val repository = RoomNoteRepository(dao, clock)

    // The exact message-to-error mapping is covered by SQLiteErrorsTest. On the Android host JVM
    // SQLiteException is a stubbed android.database.SQLException without a message, so these
    // tests use a failure that maps to UNKNOWN on every target.
    private val databaseFailure = SQLiteException("Error code: 5, message: database is locked")

    @Test
    fun `create stores title body and plain text as an unpinned note created and edited now`() = runTest {
        val result = repository.createNote(title = "Lisbon", body = "# Plan\n- [ ] **hotel**")

        val stored = dao.notes.value.single()
        assertThat(result).isEqualTo(Result.Success(stored.id))
        assertThat(stored).isEqualTo(
            NoteEntity(
                id = stored.id,
                title = "Lisbon",
                body = "# Plan\n- [ ] **hotel**",
                bodyText = "Plan hotel",
                pinned = false,
                createdAt = created.toEpochMilliseconds(),
                editedAt = created.toEpochMilliseconds(),
            ),
        )
    }

    @Test
    fun `create without arguments makes an empty note`() = runTest {
        repository.createNote()

        val stored = dao.notes.value.single()
        assertThat(listOf(stored.title, stored.body, stored.bodyText)).containsExactly("", "", "")
    }

    @Test
    fun `create returns the database error and stores nothing when the write fails`() = runTest {
        dao.writeFailure = databaseFailure

        assertThat(repository.createNote(title = "x")).isEqualTo(Result.Error(DataError.Local.UNKNOWN))
        assertThat(dao.notes.value).isEmpty()
    }

    @Test
    fun `update replaces content recomputes plain text and marks edited now keeping created and pinned`() = runTest {
        val id = repository.createNote(title = "Old", body = "old").successId()
        repository.setPinned(id, true)
        clock.now = created + 5.minutes

        val result = repository.updateNote(id, title = "New 👍🏽", body = "**new** text")

        assertThat(result).isEqualTo(Result.Success(Unit))
        assertThat(dao.notes.value.single()).isEqualTo(
            NoteEntity(
                id = id,
                title = "New 👍🏽",
                body = "**new** text",
                bodyText = "new text",
                pinned = true,
                createdAt = created.toEpochMilliseconds(),
                editedAt = (created + 5.minutes).toEpochMilliseconds(),
            ),
        )
    }

    @Test
    fun `update of a missing note is not found`() = runTest {
        assertThat(repository.updateNote(id = 99, title = "a", body = "b"))
            .isEqualTo(Result.Error(DataError.Local.NOT_FOUND))
    }

    @Test
    fun `update returns the database error`() = runTest {
        val id = repository.createNote().successId()
        dao.writeFailure = databaseFailure

        assertThat(repository.updateNote(id, "a", "b")).isEqualTo(Result.Error(DataError.Local.UNKNOWN))
    }

    @Test
    fun `pinning does not count as an edit`() = runTest {
        val id = repository.createNote().successId()
        clock.now = created + 60.minutes

        repository.setPinned(id, true)

        val stored = dao.notes.value.single()
        assertThat(stored.pinned to stored.editedAt).isEqualTo(true to created.toEpochMilliseconds())
    }

    @Test
    fun `pinning a missing note is not found`() = runTest {
        assertThat(repository.setPinned(id = 1, pinned = true)).isEqualTo(Result.Error(DataError.Local.NOT_FOUND))
    }

    @Test
    fun `delete removes only that note`() = runTest {
        val keep = repository.createNote(title = "keep").successId()
        val remove = repository.createNote(title = "remove").successId()

        assertThat(repository.deleteNote(remove)).isEqualTo(Result.Success(Unit))
        assertThat(dao.notes.value.map { it.id }).containsExactly(keep)
    }

    @Test
    fun `deleting a missing note is not found`() = runTest {
        assertThat(repository.deleteNote(id = 1)).isEqualTo(Result.Error(DataError.Local.NOT_FOUND))
    }

    @Test
    fun `delete returns the database error`() = runTest {
        val id = repository.createNote().successId()
        dao.writeFailure = databaseFailure

        assertThat(repository.deleteNote(id)).isEqualTo(Result.Error(DataError.Local.UNKNOWN))
    }

    @Test
    fun `observed notes keep the dao order and update after an edit`() = runTest {
        val first = repository.createNote(title = "first").successId()
        val second = repository.createNote(title = "second").successId()

        repository.observeNotes().test {
            assertThat(awaitItem().map { it.id }).containsExactly(first, second)
            repository.updateNote(second, title = "second edited", body = "")
            assertThat(awaitItem().map { it.title }).containsExactly("first", "second edited")
        }
    }

    @Test
    fun `observing a note emits null once it is deleted`() = runTest {
        val id = repository.createNote(title = "gone soon").successId()

        repository.observeNote(id).test {
            assertThat(awaitItem()?.title).isEqualTo("gone soon")
            repository.deleteNote(id)
            assertThat(awaitItem()).isNull()
        }
    }

    @Test
    fun `search without letters or digits finds nothing and never queries the database`() = runTest {
        listOf("", "   ", "***", "👍🏽").forEach { query ->
            repository.search(query).test {
                assertThat(awaitItem(), name = "query=\"$query\"").isEmpty()
                awaitComplete()
            }
        }
        assertThat(dao.searchQueries).isEmpty()
    }

    @Test
    fun `search passes the escaped fts queries and maps highlighted results`() = runTest {
        val s = FtsHighlight.START
        val e = FtsHighlight.END
        dao.searchRows.value = listOf(
            NoteSearchRow(
                id = 4,
                pinned = true,
                editedAt = 10,
                titleSnippet = "${s}Hotel$e ideas",
                bodySnippet = "",
                titleMatch = true,
            ),
        )

        repository.search("  Hotel \"OR\" ").test {
            val result = awaitItem().single()
            assertThat(result.title).isEqualTo(HighlightedText("Hotel ideas", listOf(0..4)))
            assertThat(result.snippet).isEqualTo(HighlightedText(""))
        }
        assertThat(dao.searchQueries).single().isEqualTo("hotel* or*" to "title:hotel* OR title:or*")
    }

    @Test
    fun `search results update when the database emits again`() = runTest {
        repository.search("hotel").test {
            assertThat(awaitItem()).isEmpty()
            dao.searchRows.value = listOf(
                NoteSearchRow(id = 1, pinned = false, editedAt = 1, titleSnippet = "a", bodySnippet = "b", titleMatch = false),
            )
            assertThat(awaitItem().map { it.id }).containsExactly(1L)
        }
    }

    private fun Result<Long, DataError.Local>.successId(): Long = (this as Result.Success).data
}

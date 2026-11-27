package io.kotelliada.marky.core.data.note

import androidx.sqlite.SQLiteException
import io.kotelliada.marky.core.data.search.FtsQuery
import io.kotelliada.marky.core.data.util.toDataError
import io.kotelliada.marky.core.database.dao.NoteDao
import io.kotelliada.marky.core.database.entity.NoteEntity
import io.kotelliada.marky.core.domain.note.Note
import io.kotelliada.marky.core.domain.note.NoteRepository
import io.kotelliada.marky.core.domain.note.NoteSearchResult
import io.kotelliada.marky.core.domain.util.DataError
import io.kotelliada.marky.core.domain.util.EmptyResult
import io.kotelliada.marky.core.domain.util.Result
import io.kotelliada.marky.core.markdown.MarkdownPlainText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class RoomNoteRepository(
    private val noteDao: NoteDao,
    private val clock: Clock,
) : NoteRepository {

    override fun observeNotes(): Flow<List<Note>> =
        noteDao.observeAll().map { notes -> notes.map { it.toNote() } }

    override fun observeNote(id: Long): Flow<Note?> =
        noteDao.observeById(id).map { it?.toNote() }

    override suspend fun createNote(title: String, body: String): Result<Long, DataError.Local> {
        val now = clock.now().toEpochMilliseconds()
        val note = NoteEntity(
            title = title,
            body = body,
            bodyText = MarkdownPlainText.from(body),
            pinned = false,
            createdAt = now,
            editedAt = now,
        )
        return databaseCall { noteDao.insert(note) }
    }

    override suspend fun updateNote(id: Long, title: String, body: String): EmptyResult<DataError.Local> =
        databaseCall {
            noteDao.updateContent(
                id = id,
                title = title,
                body = body,
                bodyText = MarkdownPlainText.from(body),
                editedAt = clock.now().toEpochMilliseconds(),
            )
        }.requireChangedRow()

    override suspend fun setPinned(id: Long, pinned: Boolean): EmptyResult<DataError.Local> =
        databaseCall { noteDao.setPinned(id, pinned) }.requireChangedRow()

    override suspend fun deleteNote(id: Long): EmptyResult<DataError.Local> =
        databaseCall { noteDao.delete(id) }.requireChangedRow()

    override fun search(query: String): Flow<List<NoteSearchResult>> {
        val ftsQuery = FtsQuery.from(query) ?: return flowOf(emptyList())
        return noteDao.search(ftsQuery.matchQuery, ftsQuery.titleMatchQuery)
            .map { rows -> rows.map { it.toNoteSearchResult() } }
    }

    private inline fun <T> databaseCall(block: () -> T): Result<T, DataError.Local> =
        try {
            Result.Success(block())
        } catch (e: SQLiteException) {
            Result.Error(e.toDataError())
        }

    /** A write that changed no row targeted a note that doesn't exist. */
    private fun Result<Int, DataError.Local>.requireChangedRow(): EmptyResult<DataError.Local> =
        when (this) {
            is Result.Error -> this
            is Result.Success -> if (data > 0) Result.Success(Unit) else Result.Error(DataError.Local.NOT_FOUND)
        }
}

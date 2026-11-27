package io.kotelliada.marky.core.data.testing

import androidx.sqlite.SQLiteException
import io.kotelliada.marky.core.database.dao.NoteDao
import io.kotelliada.marky.core.database.dao.NoteSearchRow
import io.kotelliada.marky.core.database.entity.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory [NoteDao]. Keeps notes in insertion order (ordering is the real DAO's SQL) and
 * returns [searchRows] for any search, recording the queries it was given.
 */
class FakeNoteDao : NoteDao {

    val notes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val searchRows = MutableStateFlow<List<NoteSearchRow>>(emptyList())
    val searchQueries = mutableListOf<Pair<String, String>>()

    /** When set, every write throws it, like a failing SQLite driver. */
    var writeFailure: SQLiteException? = null

    private var nextId = 1L

    override fun observeAll(): Flow<List<NoteEntity>> = notes

    override fun observeById(id: Long): Flow<NoteEntity?> = notes.map { all -> all.find { it.id == id } }

    override suspend fun insert(note: NoteEntity): Long {
        failIfRequested()
        val id = nextId++
        notes.value += note.copy(id = id)
        return id
    }

    override suspend fun updateContent(id: Long, title: String, body: String, bodyText: String, editedAt: Long): Int =
        update(id) { it.copy(title = title, body = body, bodyText = bodyText, editedAt = editedAt) }

    override suspend fun setPinned(id: Long, pinned: Boolean): Int = update(id) { it.copy(pinned = pinned) }

    override suspend fun delete(id: Long): Int {
        failIfRequested()
        val before = notes.value
        notes.value = before.filterNot { it.id == id }
        return before.size - notes.value.size
    }

    override fun search(matchQuery: String, titleMatchQuery: String): Flow<List<NoteSearchRow>> {
        searchQueries += matchQuery to titleMatchQuery
        return searchRows
    }

    private fun update(id: Long, change: (NoteEntity) -> NoteEntity): Int {
        failIfRequested()
        if (notes.value.none { it.id == id }) return 0
        notes.value = notes.value.map { if (it.id == id) change(it) else it }
        return 1
    }

    private fun failIfRequested() {
        writeFailure?.let { throw it }
    }
}

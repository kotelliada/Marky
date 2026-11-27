package io.kotelliada.marky.core.domain.note

import io.kotelliada.marky.core.domain.util.DataError
import io.kotelliada.marky.core.domain.util.EmptyResult
import io.kotelliada.marky.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

/** All notes on the device. Every feature reads and writes notes through this. */
interface NoteRepository {

    /** Pinned notes first, then the rest; most recently edited first in each group. */
    fun observeNotes(): Flow<List<Note>>

    /** Emits null when the note doesn't exist (or was deleted). */
    fun observeNote(id: Long): Flow<Note?>

    /** Creates an unpinned note, created and edited now. @return the new note's id. */
    suspend fun createNote(title: String = "", body: String = ""): Result<Long, DataError.Local>

    /** Replaces the title and body and marks the note as edited now. */
    suspend fun updateNote(id: Long, title: String, body: String): EmptyResult<DataError.Local>

    /** Pins or unpins without changing when the note was last edited. */
    suspend fun setPinned(id: Long, pinned: Boolean): EmptyResult<DataError.Local>

    suspend fun deleteNote(id: Long): EmptyResult<DataError.Local>

    /**
     * Notes containing every word of [query] (as a word or the start of one) in the title or text.
     * Title matches come first, then most recently edited. A query without any letters or digits
     * finds nothing.
     */
    fun search(query: String): Flow<List<NoteSearchResult>>
}

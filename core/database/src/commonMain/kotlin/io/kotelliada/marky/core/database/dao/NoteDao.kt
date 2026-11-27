package io.kotelliada.marky.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.kotelliada.marky.core.database.FtsHighlight
import io.kotelliada.marky.core.database.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    /** Pinned notes first, then the rest; most recently edited first in each group. */
    @Query("SELECT * FROM notes ORDER BY pinned DESC, editedAt DESC, id DESC")
    fun observeAll(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun observeById(id: Long): Flow<NoteEntity?>

    /** @return the new note's id. */
    @Insert
    suspend fun insert(note: NoteEntity): Long

    /** @return the number of updated rows: 0 when the note doesn't exist. */
    @Query(
        "UPDATE notes SET title = :title, body = :body, bodyText = :bodyText, editedAt = :editedAt " +
            "WHERE id = :id",
    )
    suspend fun updateContent(id: Long, title: String, body: String, bodyText: String, editedAt: Long): Int

    /** Pinning doesn't count as an edit, so editedAt stays as it is. */
    @Query("UPDATE notes SET pinned = :pinned WHERE id = :id")
    suspend fun setPinned(id: Long, pinned: Boolean): Int

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun delete(id: Long): Int

    /**
     * Notes matching [matchQuery], title matches first, then most recently edited.
     * Both queries are FTS4 MATCH expressions built by the data layer:
     * [titleMatchQuery] is the same words restricted to the title column.
     */
    @Query(
        "SELECT notes.id, notes.pinned, notes.editedAt, " +
            "snippet(notes_fts, '${FtsHighlight.START}', '${FtsHighlight.END}', '${FtsHighlight.ELLIPSIS}', 0, 64) " +
            "AS titleSnippet, " +
            "snippet(notes_fts, '${FtsHighlight.START}', '${FtsHighlight.END}', '${FtsHighlight.ELLIPSIS}', 1, 16) " +
            "AS bodySnippet, " +
            "notes.id IN (SELECT rowid FROM notes_fts WHERE notes_fts MATCH :titleMatchQuery) AS titleMatch " +
            "FROM notes JOIN notes_fts ON notes.id = notes_fts.rowid " +
            "WHERE notes_fts MATCH :matchQuery " +
            "ORDER BY titleMatch DESC, notes.editedAt DESC, notes.id DESC",
    )
    fun search(matchQuery: String, titleMatchQuery: String): Flow<List<NoteSearchRow>>
}

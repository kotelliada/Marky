package io.kotelliada.marky.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One note. [id] is an INTEGER PRIMARY KEY, so it is the table's rowid: the FTS index
 * ([NoteFts]) refers to notes by rowid and needs it to stay stable.
 *
 * @property body raw Markdown, exactly as typed.
 * @property bodyText [body] as plain text without Markdown syntax; indexed for search.
 * @property createdAt epoch milliseconds.
 * @property editedAt epoch milliseconds of the last change to the title or body.
 */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val body: String,
    val bodyText: String,
    val pinned: Boolean,
    val createdAt: Long,
    val editedAt: Long,
)

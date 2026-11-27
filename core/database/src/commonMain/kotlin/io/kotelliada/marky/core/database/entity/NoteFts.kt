package io.kotelliada.marky.core.database.entity

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

/**
 * Full-text index over [NoteEntity.title] and [NoteEntity.bodyText]. It stores no text of its
 * own (external content); Room keeps it in sync with `notes` through generated triggers.
 * Column order matters: `snippet()` addresses title as column 0 and bodyText as column 1.
 */
@Fts4(contentEntity = NoteEntity::class, tokenizer = FtsOptions.TOKENIZER_UNICODE61)
@Entity(tableName = "notes_fts")
data class NoteFts(
    val title: String,
    val bodyText: String,
)

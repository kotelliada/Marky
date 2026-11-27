package io.kotelliada.marky.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.kotelliada.marky.core.database.dao.NoteDao
import io.kotelliada.marky.core.database.entity.NoteEntity
import io.kotelliada.marky.core.database.entity.NoteFts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal const val DATABASE_FILE_NAME = "marky.db"

@Database(
    entities = [NoteEntity::class, NoteFts::class],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(MarkyDatabaseConstructor::class)
abstract class MarkyDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}

// Room's KSP processor generates the actual object for every target.
@Suppress("KotlinNoActualForExpect")
expect object MarkyDatabaseConstructor : RoomDatabaseConstructor<MarkyDatabase> {
    override fun initialize(): MarkyDatabase
}

/** Builds the database from a platform builder (see `markyDatabaseBuilder` in each platform source set). */
fun createMarkyDatabase(builder: RoomDatabase.Builder<MarkyDatabase>): MarkyDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()

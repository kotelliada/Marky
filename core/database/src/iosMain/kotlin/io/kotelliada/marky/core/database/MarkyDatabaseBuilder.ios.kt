package io.kotelliada.marky.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun markyDatabaseBuilder(): RoomDatabase.Builder<MarkyDatabase> {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    val directoryPath = requireNotNull(documentDirectory?.path) { "No documents directory" }
    return Room.databaseBuilder<MarkyDatabase>(name = "$directoryPath/$DATABASE_FILE_NAME")
}

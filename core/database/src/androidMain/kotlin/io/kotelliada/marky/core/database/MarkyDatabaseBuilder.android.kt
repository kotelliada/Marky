package io.kotelliada.marky.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun markyDatabaseBuilder(context: Context): RoomDatabase.Builder<MarkyDatabase> {
    val appContext = context.applicationContext
    val databaseFile = appContext.getDatabasePath(DATABASE_FILE_NAME)
    return Room.databaseBuilder<MarkyDatabase>(
        context = appContext,
        name = databaseFile.absolutePath,
    )
}

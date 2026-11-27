package io.kotelliada.marky.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

/** The database lives in a hidden folder in the user's home: `~/.Marky/marky.db`. */
fun markyDatabaseBuilder(): RoomDatabase.Builder<MarkyDatabase> {
    val directory = File(System.getProperty("user.home"), ".Marky").apply { mkdirs() }
    return Room.databaseBuilder<MarkyDatabase>(name = File(directory, DATABASE_FILE_NAME).absolutePath)
}

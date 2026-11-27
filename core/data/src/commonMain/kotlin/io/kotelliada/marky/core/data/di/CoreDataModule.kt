package io.kotelliada.marky.core.data.di

import io.kotelliada.marky.core.data.note.RoomNoteRepository
import io.kotelliada.marky.core.database.MarkyDatabase
import io.kotelliada.marky.core.database.createMarkyDatabase
import io.kotelliada.marky.core.domain.note.NoteRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.time.Clock

/** Provides the platform's `RoomDatabase.Builder<MarkyDatabase>`. */
internal expect val coreDataPlatformModule: Module

val coreDataModule = module {
    includes(coreDataPlatformModule)

    single { createMarkyDatabase(get()) }
    single { get<MarkyDatabase>().noteDao() }
    single<Clock> { Clock.System }
    singleOf(::RoomNoteRepository) { bind<NoteRepository>() }
}

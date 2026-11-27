package io.kotelliada.marky.core.data.di

import io.kotelliada.marky.core.database.markyDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val coreDataPlatformModule: Module = module {
    single { markyDatabaseBuilder() }
}

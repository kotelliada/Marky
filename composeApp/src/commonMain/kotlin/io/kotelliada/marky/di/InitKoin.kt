package io.kotelliada.marky.di

import io.kotelliada.marky.core.data.di.coreDataModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Starts Koin with every app module. Call exactly once per process, at app start:
 * `MarkyApplication` on Android, `main()` on desktop, `iOSApp.init` on iOS.
 */
fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            coreDataModule,
        )
    }
}

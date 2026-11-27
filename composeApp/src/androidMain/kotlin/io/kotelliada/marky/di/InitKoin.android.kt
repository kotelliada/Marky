package io.kotelliada.marky.di

import android.content.Context
import org.koin.android.ext.koin.androidContext

/** Android entry point: keeps Koin out of :androidApp. */
fun initKoin(context: Context) {
    initKoin {
        androidContext(context.applicationContext)
    }
}

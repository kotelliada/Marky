package io.kotelliada.marky

import android.app.Application
import io.kotelliada.marky.di.initKoin

class MarkyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this)
    }
}

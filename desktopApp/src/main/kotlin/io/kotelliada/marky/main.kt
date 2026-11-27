package io.kotelliada.marky

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.kotelliada.marky.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Marky",
        ) {
            App()
        }
    }
}
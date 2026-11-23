package io.kotelliada.marky

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
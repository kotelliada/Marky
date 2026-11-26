package io.kotelliada.marky.core.domain.util

/**
 * Errors from the data layer. Marky is local-first with no backend, so only local
 * (file system) failures exist.
 */
sealed interface DataError : Error {
    enum class Local : DataError {
        DISK_FULL,
        NOT_FOUND,
        UNKNOWN,
    }
}

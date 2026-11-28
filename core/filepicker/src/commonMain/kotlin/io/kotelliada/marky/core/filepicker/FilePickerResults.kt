package io.kotelliada.marky.core.filepicker

/** Outcome of [FileOpenLauncher.launch]. */
sealed interface OpenFileResult {

    /** [name] is the file's name including its extension, as the platform reports it. */
    class Picked(val name: String, val bytes: ByteArray) : OpenFileResult {
        override fun equals(other: Any?): Boolean =
            other is Picked && name == other.name && bytes.contentEquals(other.bytes)

        override fun hashCode(): Int = 31 * name.hashCode() + bytes.contentHashCode()

        override fun toString(): String = "Picked(name=$name, bytes=${bytes.size})"
    }

    /** A file was picked but its contents couldn't be read. */
    data class Unreadable(val name: String) : OpenFileResult

    data object Cancelled : OpenFileResult
}

/** Outcome of [FileSaveLauncher.launch]. */
sealed interface SaveFileResult {

    /** [name] is the name the file was saved under, which the user may have changed. */
    data class Saved(val name: String) : SaveFileResult

    /** A location was chosen but the file couldn't be written. */
    data object Failed : SaveFileResult

    data object Cancelled : SaveFileResult
}

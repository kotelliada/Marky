package io.kotelliada.marky.core.domain.file

/** A note as a file to save: [fileName] includes `.md`, [bytes] is the body as UTF-8. */
class ExportedNote(
    val fileName: String,
    val bytes: ByteArray,
) {
    override fun equals(other: Any?): Boolean =
        other is ExportedNote && fileName == other.fileName && bytes.contentEquals(other.bytes)

    override fun hashCode(): Int = 31 * fileName.hashCode() + bytes.contentHashCode()

    override fun toString(): String = "ExportedNote(fileName=$fileName, bytes=${bytes.size})"
}

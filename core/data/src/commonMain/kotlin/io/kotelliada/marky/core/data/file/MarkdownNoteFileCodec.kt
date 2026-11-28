package io.kotelliada.marky.core.data.file

import io.kotelliada.marky.core.domain.file.ExportedNote
import io.kotelliada.marky.core.domain.file.ImportedNote
import io.kotelliada.marky.core.domain.file.NoteFileCodec
import io.kotelliada.marky.core.domain.file.NoteImportError
import io.kotelliada.marky.core.domain.util.Result

/**
 * Import: any `.md` file (any case) whose bytes are UTF-8 becomes a note titled with the file name
 * minus `.md`, unchanged otherwise (spec: duplicate titles are allowed and kept as they are).
 *
 * Export: `<title>.md`, with characters file systems reject replaced by `-` so the suggested name
 * can always be saved.
 */
class MarkdownNoteFileCodec : NoteFileCodec {

    override fun decode(fileName: String, bytes: ByteArray): Result<ImportedNote, NoteImportError> {
        if (!fileName.endsWith(EXTENSION, ignoreCase = true)) {
            return Result.Error(NoteImportError.NOT_MARKDOWN)
        }
        val text = try {
            bytes.decodeToString(throwOnInvalidSequence = true)
        } catch (_: CharacterCodingException) {
            return Result.Error(NoteImportError.UNREADABLE)
        }
        return Result.Success(
            ImportedNote(
                title = fileName.dropLast(EXTENSION.length),
                body = text.removePrefix(BYTE_ORDER_MARK),
            ),
        )
    }

    override fun encode(title: String, body: String, untitled: String): ExportedNote {
        val stem = title.toFileNameStem().ifEmpty { untitled.toFileNameStem() }
        return ExportedNote(
            fileName = stem + EXTENSION,
            bytes = body.encodeToByteArray(),
        )
    }

    private fun String.toFileNameStem(): String =
        map { if (it in FORBIDDEN || it.isControl()) REPLACEMENT else it }
            .joinToString("")
            .trimForFileName()
            .takeUtf8Bytes(MAX_STEM_BYTES)
            .trimForFileName()

    /** Surrounding whitespace is invisible, leading dots hide the file, trailing dots are dropped by Windows. */
    private fun String.trimForFileName(): String =
        trim { it.isWhitespace() || it == '.' }

    private fun Char.isControl(): Boolean = code < 0x20 || code == 0x7F

    /** The longest prefix that fits in [maxBytes] of UTF-8, never splitting a surrogate pair. */
    private fun String.takeUtf8Bytes(maxBytes: Int): String {
        var bytes = 0
        var index = 0
        while (index < length) {
            val pair = this[index].isHighSurrogate() && index + 1 < length && this[index + 1].isLowSurrogate()
            val charCount = if (pair) 2 else 1
            val size = when {
                pair -> 4
                this[index].code < 0x80 -> 1
                this[index].code < 0x800 -> 2
                else -> 3
            }
            if (bytes + size > maxBytes) break
            bytes += size
            index += charCount
        }
        return substring(0, index)
    }

    private companion object {
        const val EXTENSION = ".md"
        const val BYTE_ORDER_MARK = "\uFEFF"
        const val REPLACEMENT = '-'
        val FORBIDDEN = setOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')

        /** Most file systems allow 255 bytes per name; `.md` takes 3 of them. */
        const val MAX_STEM_BYTES = 255 - 3
    }
}

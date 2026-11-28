package io.kotelliada.marky.core.domain.file

import io.kotelliada.marky.core.domain.util.Result

/**
 * Turns picked files into notes and notes into files. Marky imports and exports one note as one
 * plain Markdown (`.md`) file.
 */
interface NoteFileCodec {

    /**
     * @param fileName the picked file's name, including its extension.
     * @return the note to create: title = [fileName] without `.md`, body = the file's text.
     */
    fun decode(fileName: String, bytes: ByteArray): Result<ImportedNote, NoteImportError>

    /**
     * @param untitled the name to use when [title] gives no usable file name (the localized "Untitled").
     * @return the file to save: `<title>.md` holding [body] as UTF-8.
     */
    fun encode(title: String, body: String, untitled: String): ExportedNote
}

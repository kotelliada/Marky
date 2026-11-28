package io.kotelliada.marky.core.presentation.file

import io.kotelliada.marky.core.filepicker.FileType

/**
 * Marky imports and exports one note as one `.md` file.
 *
 * Android document providers label `.md` files as Markdown, plain text or unknown binary, so all
 * of those are offered; the picked name is then checked by `NoteFileCodec`, which rejects anything
 * that isn't `.md`.
 */
val MarkdownFileType = FileType(
    extensions = listOf("md"),
    mimeTypes = listOf("text/markdown", "text/x-markdown", "text/plain", "application/octet-stream"),
)

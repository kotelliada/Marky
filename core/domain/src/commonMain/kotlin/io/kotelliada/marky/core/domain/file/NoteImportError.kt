package io.kotelliada.marky.core.domain.file

import io.kotelliada.marky.core.domain.util.Error

/** Why a picked file can't become a note. */
enum class NoteImportError : Error {
    /** The file name doesn't end in `.md`. */
    NOT_MARKDOWN,

    /** The file couldn't be read as UTF-8 text. */
    UNREADABLE,
}

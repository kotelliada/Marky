package io.kotelliada.marky.core.domain.file

/** A note read from a `.md` file, ready to be created. [body] is raw Markdown. */
data class ImportedNote(
    val title: String,
    val body: String,
)

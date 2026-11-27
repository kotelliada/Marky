package io.kotelliada.marky.core.database

/**
 * Markers that FTS `snippet()` puts around matched words. Private-use characters, so they
 * never collide with anything a person types as Markdown.
 */
object FtsHighlight {
    const val START = ""
    const val END = ""
    const val ELLIPSIS = "…"
}

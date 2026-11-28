package io.kotelliada.marky.core.filepicker

/**
 * The kind of file a picker offers.
 *
 * @property extensions without the dot, lowercase. The first one is added on save when the user
 * removes it from the name.
 * @property mimeTypes what Android's document picker filters by, and what iOS turns into content
 * types. The first one is the type of files created on Android. Filters are only a hint (file
 * providers label files differently), so callers still check the picked file's name.
 */
data class FileType(
    val extensions: List<String>,
    val mimeTypes: List<String>,
) {
    init {
        require(extensions.isNotEmpty()) { "A file type needs at least one extension" }
        require(extensions.all { it.isNotEmpty() && '.' !in it && it == it.lowercase() }) {
            "Extensions are lowercase and without the dot: $extensions"
        }
    }

    /** True when [fileName] ends in one of [extensions], in any case. */
    fun matches(fileName: String): Boolean =
        extensions.any { fileName.endsWith(".$it", ignoreCase = true) }
}

/** [fileName] with `.<first extension>` appended unless it already ends in one of the type's extensions. */
internal fun FileType.withExtension(fileName: String): String =
    if (matches(fileName)) fileName else "$fileName.${extensions.first()}"

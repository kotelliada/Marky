package io.kotelliada.marky.core.data.search

/**
 * A person's search text turned into safe FTS4 MATCH expressions.
 *
 * Only runs of letters and digits are kept as words, which is how the `unicode61` tokenizer
 * splits indexed text, so quotes, `*`, `:`, `-` and parentheses can never act as FTS syntax.
 * Words are lowercased so `AND`, `OR`, `NOT` and `NEAR` are searched for, not used as operators.
 * Each word matches as a prefix: `hot` finds "hotel".
 *
 * @property matchQuery every word, anywhere in the note.
 * @property titleMatchQuery any of the words, in the title only.
 */
internal data class FtsQuery(
    val matchQuery: String,
    val titleMatchQuery: String,
) {
    companion object {
        /** @return null when [userQuery] contains no letters or digits. */
        fun from(userQuery: String): FtsQuery? {
            val words = words(userQuery)
            if (words.isEmpty()) return null
            return FtsQuery(
                matchQuery = words.joinToString(" ") { "$it*" },
                titleMatchQuery = words.joinToString(" OR ") { "title:$it*" },
            )
        }

        private fun words(text: String): List<String> {
            val words = LinkedHashSet<String>()
            val current = StringBuilder()
            fun endWord() {
                if (current.isNotEmpty()) words += current.toString().lowercase()
                current.clear()
            }
            for (char in text) {
                if (char.isLetterOrDigit()) current.append(char) else endWord()
            }
            endWord()
            return words.toList()
        }
    }
}

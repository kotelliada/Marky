package io.kotelliada.marky.core.domain.note

/**
 * Text with the parts that match a search highlighted.
 *
 * @property highlights non-empty, non-overlapping index ranges into [text], in order.
 */
data class HighlightedText(
    val text: String,
    val highlights: List<IntRange> = emptyList(),
)

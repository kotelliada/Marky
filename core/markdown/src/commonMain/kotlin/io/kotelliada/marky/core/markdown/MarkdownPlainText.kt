package io.kotelliada.marky.core.markdown

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMTokenTypes
import org.intellij.markdown.parser.CancellationToken
import org.intellij.markdown.parser.MarkdownParser

/**
 * Turns a note's raw Markdown into one line of plain text: what a reader sees, without the
 * syntax. Used for list previews and as the text that search indexes and builds snippets from.
 *
 * Text and code content are kept. Heading, quote, list and checklist markers, emphasis and
 * strikethrough delimiters, code-span backticks, code-fence lines and link destinations are
 * dropped (a link keeps its text). Lines are joined with single spaces.
 */
object MarkdownPlainText {

    private val parser = MarkdownParser(
        flavour = GFMFlavourDescriptor(),
        assertionsEnabled = false,
        cancellationToken = CancellationToken.NonCancellable,
    )

    fun from(markdown: String): String {
        if (markdown.isBlank()) return ""
        val source: CharSequence = markdown.replace("\r\n", "\n").replace('\r', '\n')
        val raw = StringBuilder()
        appendPlainText(parser.buildMarkdownTreeFromString(source), parent = null, source, raw)
        return raw.collapseWhitespace()
    }

    private fun appendPlainText(node: ASTNode, parent: ASTNode?, source: CharSequence, out: StringBuilder) {
        val type = node.type
        when {
            type in BREAKS -> out.append(' ')
            type in DROPPED -> Unit
            parent != null && isDelimiterOf(type, parent.type) -> Unit
            node.children.isEmpty() -> out.append(source, node.startOffset, node.endOffset)
            else -> contentChildren(node).forEach { appendPlainText(it, node, source, out) }
        }
    }

    /** Children that carry visible text; for links that is only the link text, without brackets. */
    private fun contentChildren(node: ASTNode): List<ASTNode> = when (node.type) {
        MarkdownElementTypes.INLINE_LINK,
        MarkdownElementTypes.FULL_REFERENCE_LINK,
        -> node.children.filter { it.type == MarkdownElementTypes.LINK_TEXT }
        MarkdownElementTypes.LINK_TEXT,
        MarkdownElementTypes.LINK_LABEL,
        -> node.children.filterNot {
            it.type == MarkdownTokenTypes.LBRACKET || it.type == MarkdownTokenTypes.RBRACKET
        }
        MarkdownElementTypes.IMAGE -> node.children.filterNot { it.type == MarkdownTokenTypes.EXCLAMATION_MARK }
        MarkdownElementTypes.AUTOLINK -> node.children.filterNot {
            it.type == MarkdownTokenTypes.LT || it.type == MarkdownTokenTypes.GT
        }
        else -> node.children
    }

    /** Delimiter tokens that are syntax only inside their own construct; elsewhere they are text. */
    private fun isDelimiterOf(token: IElementType, parent: IElementType): Boolean = when (token) {
        MarkdownTokenTypes.EMPH -> parent == MarkdownElementTypes.EMPH || parent == MarkdownElementTypes.STRONG
        MarkdownTokenTypes.BACKTICK -> parent == MarkdownElementTypes.CODE_SPAN
        GFMTokenTypes.TILDE -> parent == GFMElementTypes.STRIKETHROUGH
        else -> false
    }

    private fun StringBuilder.collapseWhitespace(): String {
        val result = StringBuilder(length)
        var pendingSpace = false
        for (char in this) {
            if (char.isWhitespace()) {
                pendingSpace = result.isNotEmpty()
            } else {
                if (pendingSpace) result.append(' ')
                pendingSpace = false
                result.append(char)
            }
        }
        return result.toString()
    }

    /** Tokens that end a line or separate cells: replaced by a space so words never merge. */
    private val BREAKS: Set<IElementType> = setOf(
        MarkdownTokenTypes.EOL,
        MarkdownTokenTypes.HARD_LINE_BREAK,
        MarkdownTokenTypes.HORIZONTAL_RULE,
        GFMTokenTypes.TABLE_SEPARATOR,
    )

    /** Pure syntax: dropped wherever it appears. */
    private val DROPPED: Set<IElementType> = setOf(
        MarkdownTokenTypes.ATX_HEADER,
        MarkdownTokenTypes.SETEXT_1,
        MarkdownTokenTypes.SETEXT_2,
        MarkdownTokenTypes.BLOCK_QUOTE,
        MarkdownTokenTypes.LIST_BULLET,
        MarkdownTokenTypes.LIST_NUMBER,
        GFMTokenTypes.CHECK_BOX,
        MarkdownTokenTypes.CODE_FENCE_START,
        MarkdownTokenTypes.CODE_FENCE_END,
        MarkdownTokenTypes.FENCE_LANG,
        MarkdownElementTypes.LINK_DEFINITION,
    )
}

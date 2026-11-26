package io.kotelliada.marky.core.designsystem.markdown

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.em
import io.kotelliada.marky.core.designsystem.theme.MarkyColors
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme
import io.kotelliada.marky.core.designsystem.theme.MarkyTypography

/**
 * Span styles for Markdown text, for whoever builds AnnotatedStrings from the Markdown AST
 * (preview) or tints the raw source (editor). Matches components.css `.mk-md*`, `.mk-tk-*`
 * and `.mk-editor .mk-syn*`.
 */
@Immutable
data class MarkyMarkdownStyles(
    // Preview, inline
    val strong: SpanStyle,
    val emphasis: SpanStyle,
    val strongEmphasis: SpanStyle,
    val inlineCode: SpanStyle,
    val link: TextLinkStyles,
    val searchHighlight: SpanStyle,
    // Code block tokens
    val codeKeyword: SpanStyle,
    val codeString: SpanStyle,
    val codeComment: SpanStyle,
    val codeNumber: SpanStyle,
    // Editor (raw Markdown), on top of the editor text style
    val editorSyntax: SpanStyle,
    val editorHeading: SpanStyle,
    val editorStrong: SpanStyle,
    val editorEmphasis: SpanStyle,
    val editorLinkText: SpanStyle,
    val editorUrl: SpanStyle,
    val editorFence: SpanStyle,
)

/** The Markdown span styles for the current theme. */
@Composable
@ReadOnlyComposable
fun markyMarkdownStyles(): MarkyMarkdownStyles = markdownStyles(MarkyTheme.colors, MarkyTheme.typography)

internal fun markdownStyles(c: MarkyColors, t: MarkyTypography): MarkyMarkdownStyles {
    val linkStyle = SpanStyle(color = c.accent, textDecoration = TextDecoration.Underline)
    return MarkyMarkdownStyles(
        strong = SpanStyle(fontWeight = FontWeight.SemiBold),
        emphasis = SpanStyle(fontStyle = FontStyle.Italic),
        strongEmphasis = SpanStyle(fontWeight = FontWeight.SemiBold, fontStyle = FontStyle.Italic),
        inlineCode = SpanStyle(fontFamily = t.mono, fontWeight = FontWeight.Normal, fontSize = 0.86.em, background = c.surface),
        link = TextLinkStyles(
            style = linkStyle,
            hoveredStyle = linkStyle,
            pressedStyle = SpanStyle(color = c.accentPressed, background = c.accentSoft, textDecoration = TextDecoration.Underline),
        ),
        searchHighlight = SpanStyle(color = c.onHighlight, background = c.highlight),
        codeKeyword = SpanStyle(color = c.codeKeyword),
        codeString = SpanStyle(color = c.codeString),
        codeComment = SpanStyle(color = c.codeComment, fontStyle = FontStyle.Italic),
        codeNumber = SpanStyle(color = c.codeNumber),
        editorSyntax = SpanStyle(color = c.accent),
        editorHeading = SpanStyle(fontWeight = FontWeight.SemiBold),
        editorStrong = SpanStyle(fontWeight = FontWeight.SemiBold),
        editorEmphasis = SpanStyle(fontStyle = FontStyle.Italic),
        editorLinkText = SpanStyle(textDecoration = TextDecoration.Underline),
        editorUrl = SpanStyle(color = c.text3),
        editorFence = SpanStyle(color = c.text2),
    )
}

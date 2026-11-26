package io.kotelliada.marky.core.designsystem.markdown

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.kotelliada.marky.core.designsystem.components.MarkyChecklistItem
import io.kotelliada.marky.core.designsystem.components.MarkyHairline
import io.kotelliada.marky.core.designsystem.interaction.MarkyInteraction
import io.kotelliada.marky.core.designsystem.preview.ForcedInteraction
import io.kotelliada.marky.core.designsystem.preview.MarkyPreviewPanels

// Preview-only helpers. They imitate the design's sample rendering with a few regexes so each
// node can be shown with realistic content; real parsing comes from JetBrains Markdown.

private val InlinePattern = Regex("""\*\*([^*]+)\*\*|\*([^*]+)\*|`([^`]+)`|\[([^\]]+)]\(([^)\s]+)\)""")

private fun inline(text: String, s: MarkyMarkdownStyles, linkStyles: TextLinkStyles = s.link): AnnotatedString = buildAnnotatedString {
    var last = 0
    InlinePattern.findAll(text).forEach { m ->
        append(text.substring(last, m.range.first))
        val (bold, em, code, linkText, url) = m.destructured
        when {
            bold.isNotEmpty() -> withStyle(s.strong) { append(bold) }
            em.isNotEmpty() -> withStyle(s.emphasis) { append(em) }
            code.isNotEmpty() -> withStyle(s.inlineCode) { append(code) }
            else -> withLink(LinkAnnotation.Url(url, linkStyles)) { append(linkText) }
        }
        last = m.range.last + 1
    }
    append(text.substring(last))
}

private val CodeTokens = Regex("""("[^"]*")|\b(fun|val|var|return|class|data|if|else|null|true|false)\b|(//.*$)|\b(\d+)\b""", RegexOption.MULTILINE)

private fun codeTokens(code: String, s: MarkyMarkdownStyles): AnnotatedString = buildAnnotatedString {
    append(code)
    CodeTokens.findAll(code).forEach { m ->
        val style = when {
            m.groups[1] != null -> s.codeString
            m.groups[2] != null -> s.codeKeyword
            m.groups[3] != null -> s.codeComment
            else -> s.codeNumber
        }
        addStyle(style, m.range.first, m.range.last + 1)
    }
}

/** Tints raw Markdown like `.mk-editor .mk-syn*`, keeping offsets 1:1. */
private class PreviewSyntaxTint(private val s: MarkyMarkdownStyles) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val src = text.text
        val out = buildAnnotatedString {
            append(src)
            var offset = 0
            var inFence = false
            src.split("\n").forEach { line ->
                fun style(st: SpanStyle, from: Int, to: Int) = addStyle(st, offset + from, offset + to)
                when {
                    line.startsWith("```") -> { inFence = !inFence; style(s.editorSyntax, 0, line.length) }
                    inFence -> style(s.editorFence, 0, line.length)
                    else -> {
                        Regex("""^(#{1,3} )(.*)""").find(line)?.let { style(s.editorSyntax, 0, it.groups[1]!!.value.length); style(s.editorHeading, it.groups[1]!!.value.length, line.length) }
                        Regex("""^(\s*)(- \[[ x]] |[-*] |\d+\. |> ?)""").find(line)?.let { val g = it.groups[2]!!; style(s.editorSyntax, g.range.first, g.range.last + 1) }
                        Regex("""\*\*([^*]+)\*\*""").findAll(line).forEach { m ->
                            style(s.editorSyntax, m.range.first, m.range.first + 2); style(s.editorStrong, m.range.first + 2, m.range.last - 1); style(s.editorSyntax, m.range.last - 1, m.range.last + 1)
                        }
                        Regex("""(?<!\*)\*([^*]+)\*(?!\*)""").findAll(line).forEach { m ->
                            style(s.editorSyntax, m.range.first, m.range.first + 1); style(s.editorEmphasis, m.range.first + 1, m.range.last); style(s.editorSyntax, m.range.last, m.range.last + 1)
                        }
                        Regex("""`[^`]+`""").findAll(line).forEach { m -> style(s.editorSyntax, m.range.first, m.range.first + 1); style(s.editorSyntax, m.range.last, m.range.last + 1) }
                        Regex("""\[([^\]]+)]\(([^)]+)\)""").findAll(line).forEach { m ->
                            val t = m.groups[1]!!.range; val u = m.groups[2]!!.range
                            style(s.editorSyntax, m.range.first, t.first); style(s.editorLinkText, t.first, t.last + 1)
                            style(s.editorSyntax, t.last + 1, u.first); style(s.editorUrl, u.first, u.last + 1); style(s.editorSyntax, m.range.last, m.range.last + 1)
                        }
                    }
                }
                offset += line.length + 1
            }
        }
        return TransformedText(out, OffsetMapping.Identity)
    }
}

@Composable
private fun NodePair(source: String, rendered: @Composable () -> Unit) {
    val styles = markyMarkdownStyles()
    val tint = remember(styles) { PreviewSyntaxTint(styles) }
    MarkyPreviewPanels(panelWidth = 420.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MarkyEditorField(value = TextFieldValue(source), onValueChange = {}, visualTransformation = tint, modifier = Modifier)
            MarkyHairline()
            MarkyMarkdownColumn { rendered() }
        }
    }
}

@Preview(name = "Headings", widthDp = 880)
@Composable
private fun HeadingsPreview() {
    NodePair("# Lisbon trip\n## To sort\n### Day 2") {
        MarkyMarkdownHeading(1, AnnotatedString("Lisbon trip"), isFirst = true)
        MarkyMarkdownHeading(2, AnnotatedString("To sort"))
        MarkyMarkdownHeading(3, AnnotatedString("Day 2"))
    }
}

@Preview(name = "Paragraph and emphasis", widthDp = 880)
@Composable
private fun ParagraphPreview() {
    val md = "Pack **light**. The *early* tram is quieter, and the stop is `Martim Moniz`."
    NodePair(md) { MarkyMarkdownParagraph(inline(md, markyMarkdownStyles())) }
}

@Preview(name = "Web link", widthDp = 880)
@Composable
private fun LinkPreview() {
    val md = "Opening times on [visitlisboa.com](https://www.visitlisboa.com)."
    NodePair(md) { MarkyMarkdownParagraph(inline(md, markyMarkdownStyles())) }
}

@Preview(name = "Bulleted list", widthDp = 880)
@Composable
private fun BulletListPreview() {
    NodePair("- Oat milk\n- Lemons\n  - Unwaxed if possible\n- Sourdough") {
        MarkyMarkdownList {
            MarkyMarkdownBulletItem(AnnotatedString("Oat milk"))
            MarkyMarkdownBulletItem(AnnotatedString("Lemons"))
            MarkyMarkdownBulletItem(AnnotatedString("Unwaxed if possible"), depth = 1)
            MarkyMarkdownBulletItem(AnnotatedString("Sourdough"))
        }
    }
}

@Preview(name = "Numbered list", widthDp = 880)
@Composable
private fun NumberedListPreview() {
    NodePair("1. Export the note as .md\n2. Open it in any editor\n3. Import it back") {
        MarkyMarkdownList {
            MarkyMarkdownNumberedItem(1, AnnotatedString("Export the note as .md"))
            MarkyMarkdownNumberedItem(2, AnnotatedString("Open it in any editor"))
            MarkyMarkdownNumberedItem(3, AnnotatedString("Import it back"))
        }
    }
}

@Preview(name = "Checklist", widthDp = 880)
@Composable
private fun ChecklistNodePreview() {
    NodePair("- [x] Book flights\n- [ ] Tram 28 pass\n- [ ] Sintra day trip") {
        Column {
            MarkyChecklistItem("Book flights", checked = true, onCheckedChange = {})
            MarkyChecklistItem("Tram 28 pass", checked = false, onCheckedChange = {})
            MarkyChecklistItem("Sintra day trip", checked = false, onCheckedChange = {})
        }
    }
}

@Preview(name = "Quote", widthDp = 880)
@Composable
private fun QuotePreview() {
    NodePair("> Writing comes first. Nothing should ask you to organize before you write.") {
        MarkyMarkdownQuote(AnnotatedString("Writing comes first. Nothing should ask you to organize before you write."))
    }
}

private const val KOTLIN_SAMPLE = "fun greet(name: String) {\n    println(\"Hello, \$name\") // 1 note\n}"

@Preview(name = "Code block", widthDp = 880)
@Composable
private fun CodeBlockPreview() {
    NodePair("```kotlin\n$KOTLIN_SAMPLE\n```") {
        MarkyCodeBlock(code = codeTokens(KOTLIN_SAMPLE, markyMarkdownStyles()), language = "kotlin", onCopyClick = {})
    }
}

@Preview(name = "Code block · long line scrolls, no language", widthDp = 880)
@Composable
private fun CodeBlockLongPreview() {
    MarkyPreviewPanels(panelWidth = 420.dp) {
        MarkyCodeBlock(
            code = AnnotatedString("queryFlow.debounce(300).flatMapLatest { repo.search(it) }.stateIn(scope, WhileSubscribed(5000), initial)"),
            language = "",
            onCopyClick = {},
        )
    }
}

@Preview(name = "Copy · default, hover, copied", widthDp = 880)
@Composable
private fun CopyStatesPreview() {
    MarkyPreviewPanels(panelWidth = 420.dp) {
        val code = codeTokens("val note = Note(title = \"Ideas\")", markyMarkdownStyles())
        MarkyCodeBlockLayout(code = code, language = "kotlin", copied = false, onCopyClick = {})
        ForcedInteraction(MarkyInteraction.Hovered) { MarkyCodeBlockLayout(code = code, language = "kotlin", copied = false, onCopyClick = {}) }
        MarkyCodeBlockLayout(code = code, language = "kotlin", copied = true, onCopyClick = {})
    }
}

@Preview(name = "Link · default, hover, pressed", widthDp = 880)
@Composable
private fun LinkStatesPreview() {
    MarkyPreviewPanels(panelWidth = 420.dp) {
        val s = markyMarkdownStyles()
        MarkyMarkdownParagraph(inline("Open [commonmark.org](https://commonmark.org)", s))
        MarkyMarkdownParagraph(inline("[commonmark.org](https://commonmark.org)", s, TextLinkStyles(style = s.link.hoveredStyle)))
        MarkyMarkdownParagraph(inline("[commonmark.org](https://commonmark.org)", s, TextLinkStyles(style = s.link.pressedStyle)))
    }
}

@Preview(name = "Editor · selection and caret", widthDp = 880)
@Composable
private fun EditorSelectionPreview() {
    val focus = remember { FocusRequester() }
    val text = "Hotel is near *Príncipe Real*."
    val start = text.indexOf("Príncipe Real")
    MarkyPreviewPanels(panelWidth = 420.dp) {
        val tint = PreviewSyntaxTint(markyMarkdownStyles())
        MarkyEditorField(
            value = TextFieldValue(text, TextRange(start, start + "Príncipe Real".length)),
            onValueChange = {},
            visualTransformation = tint,
            modifier = Modifier.focusRequester(focus),
        )
    }
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
}

@Preview(name = "Editor · empty", widthDp = 880)
@Composable
private fun EditorEmptyPreview() {
    MarkyPreviewPanels(panelWidth = 420.dp) { MarkyEditorField(value = TextFieldValue(""), onValueChange = {}) }
}

@Preview(name = "Note body · all nodes together", widthDp = 880)
@Composable
private fun AllNodesPreview() {
    MarkyPreviewPanels(panelWidth = 420.dp) {
        val s = markyMarkdownStyles()
        MarkyMarkdownColumn {
            MarkyMarkdownParagraph(inline("Marky keeps your notes as **plain Markdown**. Type the syntax yourself, or use the toolbar above the keyboard.", s))
            MarkyMarkdownHeading(2, AnnotatedString("Try it"))
            Column {
                MarkyChecklistItem("Open this note", checked = true, onCheckedChange = {})
                MarkyChecklistItem("Tick this box in preview", checked = false, onCheckedChange = {})
                MarkyChecklistItem(inline("Switch to *Edit* and look at the source", s), checked = false, onCheckedChange = {})
            }
            MarkyMarkdownHeading(2, AnnotatedString("Formatting"))
            MarkyMarkdownList {
                MarkyMarkdownNumberedItem(1, inline("Wrap words in `**` for **bold**", s))
                MarkyMarkdownNumberedItem(2, inline("Wrap words in `*` for *italic*", s))
                MarkyMarkdownNumberedItem(3, inline("Start a line with `>` for a quote", s))
            }
            MarkyMarkdownQuote(AnnotatedString("Your notes stay on this device unless you export them."))
        }
    }
}

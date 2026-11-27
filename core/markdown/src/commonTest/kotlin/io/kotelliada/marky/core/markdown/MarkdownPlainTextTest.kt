package io.kotelliada.marky.core.markdown

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class MarkdownPlainTextTest {

    private fun plain(markdown: String) = MarkdownPlainText.from(markdown)

    @Test
    fun `blank input gives empty text`() {
        listOf("", "   ", "\n\n\t\n", "\r\n").forEach { input ->
            assertThat(plain(input), name = "input=\"$input\"").isEqualTo("")
        }
    }

    @Test
    fun `lines and paragraphs are joined with single spaces and trimmed`() {
        assertThat(plain("  first line\nsecond   line\n\n\nthird  ")).isEqualTo("first line second line third")
    }

    @Test
    fun `windows and old mac line endings separate words like LF`() {
        assertThat(plain("one\r\ntwo\rthree")).isEqualTo("one two three")
    }

    @Test
    fun `heading markers of every level are removed`() {
        (1..6).forEach { level ->
            assertThat(plain("${"#".repeat(level)} Title"), name = "level=$level").isEqualTo("Title")
        }
    }

    @Test
    fun `hash without a space is text and not a heading`() {
        assertThat(plain("#hashtag")).isEqualTo("#hashtag")
    }

    @Test
    fun `setext heading underline is removed`() {
        assertThat(plain("Title\n=====\n\nSub\n---")).isEqualTo("Title Sub")
    }

    @Test
    fun `bold italic and strikethrough delimiters are removed`() {
        assertThat(plain("**bold** *italic* _under_ ***both*** ~~gone~~")).isEqualTo("bold italic under both gone")
    }

    @Test
    fun `emphasis characters that are not delimiters stay`() {
        assertThat(plain("2 * 3 = snake_case_name")).isEqualTo("2 * 3 = snake_case_name")
    }

    @Test
    fun `inline code keeps its content without backticks`() {
        assertThat(plain("Run `gradle build` now")).isEqualTo("Run gradle build now")
    }

    @Test
    fun `fenced code drops fence lines and language but keeps code`() {
        assertThat(plain("Before\n```kotlin\nval a = 1\nval b = 2\n```\nAfter"))
            .isEqualTo("Before val a = 1 val b = 2 After")
    }

    @Test
    fun `tilde fence is treated like a backtick fence`() {
        assertThat(plain("~~~\ncode\n~~~")).isEqualTo("code")
    }

    @Test
    fun `bullet and numbered list markers are removed including nested items`() {
        assertThat(plain("- one\n* two\n  + nested\n1. first\n10) tenth")).isEqualTo("one two nested first tenth")
    }

    @Test
    fun `checklist boxes are removed whether ticked or not`() {
        assertThat(plain("- [ ] open\n- [x] done\n- [X] shouted")).isEqualTo("open done shouted")
    }

    @Test
    fun `quote markers are removed on every quoted line`() {
        assertThat(plain("> quoted\n> more\n>> nested")).isEqualTo("quoted more nested")
    }

    @Test
    fun `inline link keeps its text and drops destination and title`() {
        assertThat(plain("See [the docs](https://example.com \"Title\") here")).isEqualTo("See the docs here")
    }

    @Test
    fun `angle bracket autolink keeps the address without brackets`() {
        assertThat(plain("<https://example.com>")).isEqualTo("https://example.com")
    }

    @Test
    fun `bare web address stays as written`() {
        assertThat(plain("Go to https://example.com/a_b?q=1 now")).isEqualTo("Go to https://example.com/a_b?q=1 now")
    }

    @Test
    fun `reference link keeps its text and the definition line disappears`() {
        assertThat(plain("Read [the guide][g].\n\n[g]: https://example.com")).isEqualTo("Read the guide.")
    }

    @Test
    fun `horizontal rule does not leave dashes between paragraphs`() {
        assertThat(plain("above\n\n---\n\nbelow")).isEqualTo("above below")
    }

    @Test
    fun `emoji sequences and non latin scripts pass through untouched`() {
        val text = "👨‍👩‍👧‍👦 👍🏽 日本語 مرحبا Привет"
        assertThat(plain("# $text")).isEqualTo(text)
    }

    @Test
    fun `combining characters are not normalised`() {
        val decomposed = "café"
        assertThat(plain("**$decomposed**")).isEqualTo(decomposed)
    }

    @Test
    fun `markdown inside inline code is kept literally`() {
        assertThat(plain("`**not bold**`")).isEqualTo("**not bold**")
    }

    @Test
    fun `nested emphasis inside a link inside a list item is fully unwrapped`() {
        assertThat(plain("- [ ] Book [**hotel** in _Lisbon_](https://x.y)")).isEqualTo("Book hotel in Lisbon")
    }
}

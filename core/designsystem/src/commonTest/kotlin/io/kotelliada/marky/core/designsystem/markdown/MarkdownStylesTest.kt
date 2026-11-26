package io.kotelliada.marky.core.designsystem.markdown

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.em
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isInstanceOf
import androidx.compose.ui.text.AnnotatedString
import assertk.assertions.isEqualTo
import io.kotelliada.marky.core.designsystem.theme.MarkyDarkColors
import io.kotelliada.marky.core.designsystem.theme.MarkyLightColors
import io.kotelliada.marky.core.designsystem.theme.markyTypography
import kotlin.test.Test

class MarkdownStylesTest {

    // Break vectors:
    // - heading marker: wrong count of #, marker style leaking onto the heading text, the
    //   heading's own inline styles (bold, emphasis) lost when the marker is prepended,
    //   emoji / combining characters in the title altered
    // - styles: inline code not in mono or at the wrong size, comments not italic, the pressed
    //   link losing its accent-soft background, editor URL not dimmed, styles reading the wrong theme
    private val t = markyTypography(FontFamily.SansSerif, FontFamily.Monospace)

    @Test
    fun `heading marker repeats hash per level and leaves the title untouched`() {
        val title = buildAnnotatedString {
            append("Día 2 👨‍👩‍👧 ")
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("trip") }
        }
        (1..3).forEach { level ->
            val result = headingWithMarker(level, title, t, MarkyLightColors)
            val marker = "#".repeat(level)
            assertThat(result.text, name = "level $level").isEqualTo("$marker ${title.text}")
            val markerSpan = result.spanStyles.first { it.start == 0 }
            assertThat(markerSpan.end, name = "level $level marker end").isEqualTo(level)
            assertThat(markerSpan.item.color).isEqualTo(MarkyLightColors.marker)
            assertThat(markerSpan.item.fontSize).isEqualTo(0.6.em)
            assertThat(markerSpan.item.baselineShift).isEqualTo(BaselineShift(0.18f))
            val bold = result.spanStyles.single { it.item.fontWeight == FontWeight.SemiBold }
            assertThat(result.text.substring(bold.start, bold.end)).isEqualTo("trip")
        }
    }

    @Test
    fun `heading levels outside one to three are rejected`() {
        listOf(0, 4, -1).forEach { level ->
            assertFailure { headingWithMarker(level, AnnotatedString("Day 2"), t, MarkyLightColors) }
                .isInstanceOf(IllegalArgumentException::class)
        }
    }

    @Test
    fun `inline styles match components css`() {
        val s = markdownStyles(MarkyLightColors, t)
        assertThat(s.strong.fontWeight).isEqualTo(FontWeight.SemiBold)
        assertThat(s.emphasis.fontStyle).isEqualTo(FontStyle.Italic)
        assertThat(s.inlineCode.fontFamily).isEqualTo(FontFamily.Monospace)
        assertThat(s.inlineCode.fontSize).isEqualTo(0.86.em)
        assertThat(s.inlineCode.background).isEqualTo(MarkyLightColors.surface)
        assertThat(s.codeComment.fontStyle).isEqualTo(FontStyle.Italic)
    }

    @Test
    fun `links are accent and underlined and pressed adds accent soft`() {
        val s = markdownStyles(MarkyLightColors, t)
        assertThat(s.link.style!!.color).isEqualTo(MarkyLightColors.accent)
        assertThat(s.link.style!!.textDecoration).isEqualTo(TextDecoration.Underline)
        assertThat(s.link.pressedStyle!!.color).isEqualTo(MarkyLightColors.accentPressed)
        assertThat(s.link.pressedStyle!!.background).isEqualTo(MarkyLightColors.accentSoft)
    }

    @Test
    fun `editor tints use accent syntax and dimmed urls`() {
        val s = markdownStyles(MarkyDarkColors, t)
        assertThat(s.editorSyntax.color).isEqualTo(MarkyDarkColors.accent)
        assertThat(s.editorUrl.color).isEqualTo(MarkyDarkColors.text3)
        assertThat(s.editorFence.color).isEqualTo(MarkyDarkColors.text2)
        assertThat(s.codeKeyword.color).isEqualTo(MarkyDarkColors.codeKeyword)
    }
}

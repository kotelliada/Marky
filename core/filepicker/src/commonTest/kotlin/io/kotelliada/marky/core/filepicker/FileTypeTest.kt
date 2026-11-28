package io.kotelliada.marky.core.filepicker

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import kotlin.test.Test

class FileTypeTest {

    private val markdown = FileType(extensions = listOf("md", "markdown"), mimeTypes = listOf("text/markdown"))

    @Test
    fun `matches any of the extensions in any case`() {
        listOf("a.md", "a.MD", "a.Markdown", "archive.tar.md", ".md", "👨‍👩‍👧‍👦.md").forEach { name ->
            assertThat(markdown.matches(name), name = name).isTrue()
        }
    }

    @Test
    fun `does not match a name that only contains the extension`() {
        listOf("amd", "a.md.pdf", "a.md ", "md", "", "a.mdown", "a.markdow").forEach { name ->
            assertThat(markdown.matches(name), name = "name=\"$name\"").isFalse()
        }
    }

    @Test
    fun `withExtension keeps a name that already has one of the extensions`() {
        assertThat(markdown.withExtension("Trip.MD")).isEqualTo("Trip.MD")
        assertThat(markdown.withExtension("Trip.markdown")).isEqualTo("Trip.markdown")
    }

    @Test
    fun `withExtension appends the first extension otherwise`() {
        assertThat(markdown.withExtension("Trip")).isEqualTo("Trip.md")
        assertThat(markdown.withExtension("Trip.txt")).isEqualTo("Trip.txt.md")
        assertThat(markdown.withExtension("v1.2")).isEqualTo("v1.2.md")
        assertThat(markdown.withExtension("🏨 Hotels")).isEqualTo("🏨 Hotels.md")
        assertThat(markdown.withExtension("")).isEqualTo(".md")
    }

    @Test
    fun `extensions must be lowercase non empty and without the dot`() {
        listOf(emptyList(), listOf(""), listOf(".md"), listOf("MD"), listOf("md", "tar.gz")).forEach { extensions ->
            assertFailure { FileType(extensions, mimeTypes = emptyList()) }
                .isInstanceOf<IllegalArgumentException>()
        }
    }
}

package io.kotelliada.marky.core.data.file

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.kotelliada.marky.core.domain.file.ImportedNote
import io.kotelliada.marky.core.domain.file.NoteImportError
import io.kotelliada.marky.core.domain.util.Result
import kotlin.test.Test

class MarkdownNoteFileCodecTest {

    private val codec = MarkdownNoteFileCodec()

    private fun decode(fileName: String, text: String = "body") =
        codec.decode(fileName, text.encodeToByteArray())

    private fun exportName(title: String) = codec.encode(title, body = "", untitled = UNTITLED).fileName

    // region decode

    @Test
    fun `md extension matches in any case`() {
        listOf("Ideas.md", "Ideas.MD", "Ideas.Md", "Ideas.mD").forEach { name ->
            assertThat(decode(name), name = name).isEqualTo(Result.Success(ImportedNote("Ideas", "body")))
        }
    }

    @Test
    fun `files not ending in md are not markdown`() {
        listOf("scan.pdf", "Ideas.md.pdf", "notes.markdown", "notes.txt", "README", "md", "Ideasmd", "Ideas.md ", "")
            .forEach { name ->
                assertThat(decode(name), name = "name=\"$name\"").isEqualTo(Result.Error(NoteImportError.NOT_MARKDOWN))
            }
    }

    @Test
    fun `only the last md extension is removed from the title`() {
        assertThat(decode("archive.tar.md")).isEqualTo(Result.Success(ImportedNote("archive.tar", "body")))
        assertThat(decode("notes.md.md")).isEqualTo(Result.Success(ImportedNote("notes.md", "body")))
    }

    @Test
    fun `a file named only md gives an empty title`() {
        assertThat(decode(".md")).isEqualTo(Result.Success(ImportedNote("", "body")))
    }

    @Test
    fun `title keeps the file name unchanged`() {
        listOf("  Ideas  ", "👨‍👩‍👧‍👦 Family 👍🏽", "日本語のメモ", "مرحبا", "Café", "Cafe\u0301", "%s {0} ../..")
            .forEach { title ->
                assertThat(decode("$title.md"), name = title).isEqualTo(Result.Success(ImportedNote(title, "body")))
            }
    }

    @Test
    fun `an empty file gives an empty body`() {
        assertThat(codec.decode("Empty.md", ByteArray(0))).isEqualTo(Result.Success(ImportedNote("Empty", "")))
    }

    @Test
    fun `body keeps the text exactly including line endings and emoji`() {
        val text = "# Trip\r\n\r\n- [x] Book 🏨\r\n\tindented\n> quote\rold mac\u0000"
        assertThat(decode("Trip.md", text)).isEqualTo(Result.Success(ImportedNote("Trip", text)))
    }

    @Test
    fun `a leading byte order mark is removed but one inside the text is kept`() {
        assertThat(decode("Bom.md", "\uFEFF# Hi \uFEFF there"))
            .isEqualTo(Result.Success(ImportedNote("Bom", "# Hi \uFEFF there")))
    }

    @Test
    fun `bytes that are not utf-8 are unreadable`() {
        val invalid = listOf(
            byteArrayOf(0xFF.toByte()),
            byteArrayOf('a'.code.toByte(), 0xC3.toByte()), // truncated two-byte sequence
            byteArrayOf(0xE2.toByte(), 0x28, 0xA1.toByte()), // bad continuation byte
            byteArrayOf(0x25, 0x50, 0x44, 0x46, 0xE2.toByte(), 0xE3.toByte(), 0xCF.toByte(), 0xD3.toByte()), // %PDF binary
        )
        invalid.forEachIndexed { i, bytes ->
            assertThat(codec.decode("Bad.md", bytes), name = "case $i").isEqualTo(Result.Error(NoteImportError.UNREADABLE))
        }
    }

    @Test
    fun `the extension is checked before the content`() {
        assertThat(codec.decode("scan.pdf", byteArrayOf(0xFF.toByte())))
            .isEqualTo(Result.Error(NoteImportError.NOT_MARKDOWN))
    }

    // endregion

    // region encode

    @Test
    fun `file is named after the title and holds the body as utf-8`() {
        val exported = codec.encode("Lisbon trip", body = "# Lisbon 🇵🇹\n- [ ] Tram 28", untitled = UNTITLED)

        assertThat(exported.fileName).isEqualTo("Lisbon trip.md")
        assertThat(exported.bytes.decodeToString()).isEqualTo("# Lisbon 🇵🇹\n- [ ] Tram 28")
    }

    @Test
    fun `characters file systems forbid become a dash`() {
        listOf('/', '\\', ':', '*', '?', '"', '<', '>', '|').forEach { char ->
            assertThat(exportName("a${char}b"), name = "char=$char").isEqualTo("a-b.md")
        }
        assertThat(exportName("Q3: plan/draft")).isEqualTo("Q3- plan-draft.md")
    }

    @Test
    fun `control characters become a dash`() {
        assertThat(exportName("a\nb\tc\u0000d\u007Fe\u001Ff")).isEqualTo("a-b-c-d-e-f.md")
    }

    @Test
    fun `surrounding whitespace and dots are trimmed but inner ones are kept`() {
        assertThat(exportName("  Plan.  ")).isEqualTo("Plan.md")
        assertThat(exportName("...hidden")).isEqualTo("hidden.md")
        assertThat(exportName("v1.2  notes")).isEqualTo("v1.2  notes.md")
    }

    @Test
    fun `a title without a usable name falls back to untitled`() {
        listOf("", "   ", "\u3000", "...", " . . ").forEach { title ->
            assertThat(exportName(title), name = "title=\"$title\"").isEqualTo("Untitled.md")
        }
    }

    @Test
    fun `a title made only of forbidden characters keeps its dashes`() {
        assertThat(exportName("///")).isEqualTo("---.md")
    }

    @Test
    fun `unicode titles are kept`() {
        listOf("👨‍👩‍👧‍👦 Family", "日本語のメモ", "مرحبا", "Café").forEach { title ->
            assertThat(exportName(title), name = title).isEqualTo("$title.md")
        }
    }

    @Test
    fun `name fits in 255 utf-8 bytes`() {
        assertThat(exportName("a".repeat(252))).isEqualTo("a".repeat(252) + ".md")
        assertThat(exportName("a".repeat(253))).isEqualTo("a".repeat(252) + ".md")
        assertThat(exportName("日".repeat(85))).isEqualTo("日".repeat(84) + ".md")
    }

    @Test
    fun `cutting never splits an emoji surrogate pair`() {
        assertThat(exportName("a".repeat(248) + "😀")).isEqualTo("a".repeat(248) + "😀.md")
        assertThat(exportName("a".repeat(249) + "😀")).isEqualTo("a".repeat(249) + ".md")
    }

    @Test
    fun `whitespace left at the cut is trimmed`() {
        assertThat(exportName("a".repeat(251) + " b")).isEqualTo("a".repeat(251) + ".md")
    }

    @Test
    fun `an exported note imports back with the same body`() {
        val body = "# Round trip\r\n- [x] 👍🏽 done\n"
        val exported = codec.encode("Round trip", body, UNTITLED)

        assertThat(codec.decode(exported.fileName, exported.bytes))
            .isEqualTo(Result.Success(ImportedNote("Round trip", body)))
    }

    // endregion

    private companion object {
        const val UNTITLED = "Untitled"
    }
}

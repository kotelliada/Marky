package io.kotelliada.marky.core.designsystem.components

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class ShortcutFormatTest {

    // Break vectors: modifier order (design: "Ctrl Shift S" and "⇧⌘S", Shift before Command on
    // macOS), separators (spaces off-mac, none on mac), lower-case letters, Backspace and Esc
    // naming per platform, a key with no modifiers, and the names coming from resources.
    private val names = ShortcutKeyNames(ctrl = "Ctrl", shift = "Shift", backspace = "Backspace", escape = "Esc")

    @Test
    fun `ctrl shift letter`() {
        val s = MarkyShortcut.ctrl('s', shift = true)
        assertThat(s.format(commandKey = false, names)).isEqualTo("Ctrl Shift S")
        assertThat(s.format(commandKey = true, names)).isEqualTo("⇧⌘S")
    }

    @Test
    fun `ctrl letter is upper cased`() {
        val s = MarkyShortcut.ctrl('n')
        assertThat(s.format(commandKey = false, names)).isEqualTo("Ctrl N")
        assertThat(s.format(commandKey = true, names)).isEqualTo("⌘N")
    }

    @Test
    fun `backspace is named off mac and a symbol on mac`() {
        val s = MarkyShortcut(MarkyShortcut.Key.Backspace, ctrl = true)
        assertThat(s.format(commandKey = false, names)).isEqualTo("Ctrl Backspace")
        assertThat(s.format(commandKey = true, names)).isEqualTo("⌘⌫")
    }

    @Test
    fun `esc alone has no modifier on either platform`() {
        val s = MarkyShortcut(MarkyShortcut.Key.Escape)
        assertThat(s.format(commandKey = false, names)).isEqualTo("Esc")
        assertThat(s.format(commandKey = true, names)).isEqualTo("Esc")
    }

    @Test
    fun `key names come from the given resources`() {
        val german = ShortcutKeyNames(ctrl = "Strg", shift = "Umschalt", backspace = "Rücktaste", escape = "Esc")
        assertThat(MarkyShortcut(MarkyShortcut.Key.Backspace, ctrl = true, shift = true).format(false, german)).isEqualTo("Strg Umschalt Rücktaste")
    }
}

package io.kotelliada.marky.core.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import io.kotelliada.marky.core.designsystem.platform.usesCommandKey
import io.kotelliada.marky.core.designsystem.preview.MarkyPreviewPanels
import io.kotelliada.marky.core.designsystem.resources.Res
import io.kotelliada.marky.core.designsystem.resources.key_backspace
import io.kotelliada.marky.core.designsystem.resources.key_ctrl
import io.kotelliada.marky.core.designsystem.resources.key_esc
import io.kotelliada.marky.core.designsystem.resources.key_shift
import io.kotelliada.marky.core.designsystem.theme.MarkyShapes
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme
import org.jetbrains.compose.resources.stringResource

/**
 * A keyboard shortcut from the design system's shortcut table, for example Ctrl N or Ctrl Shift S.
 * It reads "Ctrl Shift S" on Windows and Linux and "⇧⌘S" where shortcuts use the Command key.
 */
@Immutable
data class MarkyShortcut(
    val key: Key,
    val ctrl: Boolean = false,
    val shift: Boolean = false,
) {
    sealed interface Key {
        /** A letter key, shown upper case. */
        data class Letter(val char: Char) : Key

        data object Backspace : Key

        data object Escape : Key
    }

    companion object {
        /** Ctrl + [char] (⌘ on macOS). */
        fun ctrl(char: Char, shift: Boolean = false) = MarkyShortcut(Key.Letter(char), ctrl = true, shift = shift)
    }
}

/** Platform names of the modifier keys, resolved from string resources. */
internal data class ShortcutKeyNames(
    val ctrl: String,
    val shift: String,
    val backspace: String,
    val escape: String,
)

/**
 * Pure formatting so it can be unit-tested. [commandKey] true gives the macOS form: modifier
 * symbols without spaces, Shift before Command (⇧⌘S), Backspace as ⌫.
 */
internal fun MarkyShortcut.format(commandKey: Boolean, names: ShortcutKeyNames): String {
    val key = when (key) {
        is MarkyShortcut.Key.Letter -> key.char.uppercaseChar().toString()
        MarkyShortcut.Key.Backspace -> if (commandKey) "⌫" else names.backspace
        MarkyShortcut.Key.Escape -> names.escape
    }
    return if (commandKey) {
        buildString {
            if (shift) append("⇧")
            if (ctrl) append("⌘")
            append(key)
        }
    } else {
        listOfNotNull(names.ctrl.takeIf { ctrl }, names.shift.takeIf { shift }, key).joinToString(" ")
    }
}

/** The label of [shortcut] for this platform. */
@Composable
fun markyShortcutLabel(shortcut: MarkyShortcut): String = shortcut.format(
    commandKey = usesCommandKey,
    names = ShortcutKeyNames(
        ctrl = stringResource(Res.string.key_ctrl),
        shift = stringResource(Res.string.key_shift),
        backspace = stringResource(Res.string.key_backspace),
        escape = stringResource(Res.string.key_esc),
    ),
)

/**
 * A shortcut key chip (`.mk-kbd`): shown in the desktop search entry, menus and tooltips.
 * [inverse] is the variant inside a tooltip.
 */
@Composable
fun MarkyShortcutKey(
    shortcut: MarkyShortcut,
    modifier: Modifier = Modifier,
    inverse: Boolean = false,
) {
    MarkyShortcutKeyText(text = markyShortcutLabel(shortcut), modifier = modifier, inverse = inverse)
}

@Composable
internal fun MarkyShortcutKeyText(text: String, modifier: Modifier = Modifier, inverse: Boolean = false) {
    val colors = MarkyTheme.colors
    val style = MarkyTheme.typography.caption.copy(fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp)
    if (inverse) {
        MarkyText(
            text = text,
            style = style,
            maxLines = 1,
            modifier = modifier
                .alpha(0.72f)
                .border(1.dp, colors.onInverse, MarkyShapes.xs)
                .padding(horizontal = 5.dp),
            color = colors.onInverse,
        )
    } else {
        MarkyText(
            text = text,
            style = style,
            maxLines = 1,
            color = colors.text3,
            modifier = modifier
                .background(colors.bg, MarkyShapes.xs)
                .border(1.dp, colors.border, MarkyShapes.xs)
                .padding(horizontal = 5.dp),
        )
    }
}

@Preview(name = "Shortcut key", widthDp = 760)
@Composable
private fun ShortcutKeyPreview() {
    MarkyPreviewPanels {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MarkyShortcutKey(MarkyShortcut.ctrl('f'))
            MarkyShortcutKey(MarkyShortcut.ctrl('s', shift = true))
            MarkyShortcutKey(MarkyShortcut(MarkyShortcut.Key.Backspace, ctrl = true))
            MarkyShortcutKey(MarkyShortcut(MarkyShortcut.Key.Escape))
        }
    }
}

@Preview(name = "Shortcut key · inverse (in a tooltip)", widthDp = 760)
@Composable
private fun ShortcutKeyInversePreview() {
    MarkyPreviewPanels {
        Row(
            modifier = Modifier.background(MarkyTheme.colors.inverse, MarkyShapes.xs).padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MarkyShortcutKey(MarkyShortcut.ctrl('o'), inverse = true)
            MarkyShortcutKey(MarkyShortcut.ctrl('b'), inverse = true)
        }
    }
}


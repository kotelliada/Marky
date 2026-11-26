package io.kotelliada.marky.core.designsystem.markdown

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.kotelliada.marky.core.designsystem.components.MarkyText
import io.kotelliada.marky.core.designsystem.resources.Res
import io.kotelliada.marky.core.designsystem.resources.write_in_markdown
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme
import org.jetbrains.compose.resources.stringResource

/**
 * The raw Markdown editor (edit mode): Plex Mono 14/24, accent caret, selection in the
 * selection color, "Write in Markdown…" when empty. [visualTransformation] tints the syntax
 * with [MarkyMarkdownStyles]' editor styles; it must keep offsets 1:1 (only add styles).
 * [value] is a TextFieldValue so the formatting toolbar can wrap the selection.
 */
@Composable
fun MarkyEditorField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource? = null,
) {
    val colors = MarkyTheme.colors
    val style = MarkyTheme.typography.editor
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth().heightIn(min = 96.dp),
        textStyle = style.copy(color = colors.text),
        cursorBrush = SolidColor(colors.accent),
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Box {
                if (value.text.isEmpty()) MarkyText(text = stringResource(Res.string.write_in_markdown), style = style, color = colors.text3)
                innerTextField()
            }
        },
    )
}

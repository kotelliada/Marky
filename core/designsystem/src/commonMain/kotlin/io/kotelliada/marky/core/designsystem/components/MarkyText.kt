package io.kotelliada.marky.core.designsystem.components

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme

/**
 * Text in a Margin type style. Defaults to body-l in the surrounding content color
 * (text on the page, on-accent inside a primary button, and so on).
 */
@Composable
fun MarkyText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MarkyTheme.typography.bodyL,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color.takeOrElse(style),
        style = style,
        textAlign = textAlign ?: TextAlign.Unspecified,
        maxLines = maxLines,
        minLines = minLines,
        overflow = overflow,
        softWrap = softWrap,
    )
}

/** [MarkyText] for styled text: search highlights, Markdown inline styles, links. */
@Composable
fun MarkyText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = MarkyTheme.typography.bodyL,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color.takeOrElse(style),
        style = style,
        textAlign = textAlign ?: TextAlign.Unspecified,
        maxLines = maxLines,
        minLines = minLines,
        overflow = overflow,
        softWrap = softWrap,
    )
}

@Composable
private fun Color.takeOrElse(style: TextStyle): Color = when {
    this != Color.Unspecified -> this
    style.color != Color.Unspecified -> style.color
    else -> LocalContentColor.current
}

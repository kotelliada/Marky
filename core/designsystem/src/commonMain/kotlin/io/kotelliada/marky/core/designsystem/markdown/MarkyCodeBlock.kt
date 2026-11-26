package io.kotelliada.marky.core.designsystem.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import io.kotelliada.marky.core.designsystem.components.MarkyIcon
import io.kotelliada.marky.core.designsystem.components.MarkyText
import io.kotelliada.marky.core.designsystem.icons.MarkyIcons
import io.kotelliada.marky.core.designsystem.interaction.collectMarkyInteraction
import io.kotelliada.marky.core.designsystem.interaction.markyFocusRing
import io.kotelliada.marky.core.designsystem.resources.Res
import io.kotelliada.marky.core.designsystem.resources.copied
import io.kotelliada.marky.core.designsystem.resources.copy
import io.kotelliada.marky.core.designsystem.theme.MarkyShapes
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

/** How long the copy button reads "Copied". */
internal const val CopiedFeedbackMs = 1_500L

/**
 * A fenced code block: language label, Copy button, and code that scrolls sideways instead of
 * wrapping. [code] carries the token colors from [MarkyMarkdownStyles]. [onCopyClick] does the
 * copying; the button then shows "Copied" for 1.5 s. A blank [language] shows as "text".
 */
@Composable
fun MarkyCodeBlock(
    code: AnnotatedString,
    language: String,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            delay(CopiedFeedbackMs)
            copied = false
        }
    }
    MarkyCodeBlockLayout(
        code = code,
        language = language,
        copied = copied,
        onCopyClick = {
            onCopyClick()
            copied = true
        },
        modifier = modifier,
    )
}

@Composable
internal fun MarkyCodeBlockLayout(
    code: AnnotatedString,
    language: String,
    copied: Boolean,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MarkyTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MarkyShapes.sm)
            .background(colors.codeBg)
            .border(1.dp, colors.border, MarkyShapes.sm),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val y = size.height - 0.5.dp.toPx()
                    drawLine(colors.border, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                }
                .padding(start = 12.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            MarkyText(
                text = language.ifBlank { "text" }.uppercase(),
                style = MarkyTheme.typography.overline,
                color = colors.text3,
                maxLines = 1,
            )
            CopyButton(copied = copied, onClick = onCopyClick)
        }
        Box(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
            MarkyText(
                text = code,
                style = MarkyTheme.typography.code,
                color = colors.codeText,
                softWrap = false,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            )
        }
    }
}

@Composable
private fun CopyButton(copied: Boolean, onClick: () -> Unit) {
    val source = remember { MutableInteractionSource() }
    val interaction = source.collectMarkyInteraction()
    val colors = MarkyTheme.colors
    val container = when {
        interaction.pressed -> colors.statePressed
        interaction.hovered -> colors.stateHover
        else -> Color.Transparent
    }
    val content = when {
        copied -> colors.accent
        interaction.hovered || interaction.pressed -> colors.text
        else -> colors.text2
    }
    Row(
        modifier = Modifier
            .height(28.dp)
            .markyFocusRing(interaction.focused, MarkyShapes.xs, colors.focus)
            .background(container, MarkyShapes.xs)
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick)
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        MarkyIcon(icon = if (copied) MarkyIcons.Check else MarkyIcons.Copy, contentDescription = null, size = 14.dp, tint = content)
        MarkyText(
            text = stringResource(if (copied) Res.string.copied else Res.string.copy),
            style = MarkyTheme.typography.control,
            color = content,
            maxLines = 1,
        )
    }
}

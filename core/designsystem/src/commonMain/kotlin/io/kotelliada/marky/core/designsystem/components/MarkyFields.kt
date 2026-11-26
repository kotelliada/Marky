package io.kotelliada.marky.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.kotelliada.marky.core.designsystem.icons.MarkyIcons
import io.kotelliada.marky.core.designsystem.interaction.LocalForcedInteraction
import io.kotelliada.marky.core.designsystem.interaction.MarkyInteraction
import io.kotelliada.marky.core.designsystem.interaction.collectMarkyInteraction
import io.kotelliada.marky.core.designsystem.interaction.markyFocusRing
import io.kotelliada.marky.core.designsystem.preview.ForcedInteraction
import io.kotelliada.marky.core.designsystem.preview.MarkyPreviewPanels
import io.kotelliada.marky.core.designsystem.resources.Res
import io.kotelliada.marky.core.designsystem.resources.clear_search
import io.kotelliada.marky.core.designsystem.resources.search_notes
import io.kotelliada.marky.core.designsystem.resources.untitled
import io.kotelliada.marky.core.designsystem.theme.MarkyDimens
import io.kotelliada.marky.core.designsystem.theme.MarkyMotion
import io.kotelliada.marky.core.designsystem.theme.MarkyShapes
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme
import org.jetbrains.compose.resources.stringResource

/**
 * The search field on the Search screen. Focus gives an accent border and a 3 dp accent-soft
 * halo; the clear button appears once there is text.
 *
 * Built on BasicTextField: Material's TextField has a fixed 56 dp minimum height and label
 * paddings, and Margin's field is 40 dp.
 */
@Composable
fun MarkySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String = stringResource(Res.string.search_notes),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interaction = source.collectMarkyInteraction()
    val colors = MarkyTheme.colors
    val borderColor = animateColorAsState(
        targetValue = when {
            !enabled -> colors.border
            interaction.focused -> colors.accent
            interaction.hovered -> colors.borderStrong
            else -> colors.border
        },
        animationSpec = tween(MarkyMotion.DURATION_FAST_MS, easing = MarkyMotion.ease),
    ).value
    val textStyle = MarkyTheme.typography.bodyL.copy(color = if (enabled) colors.text else colors.textDisabled)

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(MarkyDimens.fieldHeight)
            .markyFocusRing(enabled && interaction.focused, MarkyShapes.md, colors.accentSoft, offset = 0.dp, width = 3.dp)
            .hoverable(source, enabled),
        enabled = enabled,
        singleLine = true,
        textStyle = textStyle,
        cursorBrush = SolidColor(colors.accent),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = keyboardActions,
        interactionSource = source,
        decorationBox = { innerTextField ->
            // The field's own forced state must not leak into the clear button.
            CompositionLocalProvider(LocalForcedInteraction provides null) {
                Row(
                    modifier = Modifier
                        .background(if (enabled) colors.bg else colors.surface, MarkyShapes.md)
                        .border(1.dp, borderColor, MarkyShapes.md)
                        .padding(start = 12.dp, end = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MarkyDimens.space2),
                ) {
                    MarkyIcon(
                        icon = MarkyIcons.Search,
                        contentDescription = null,
                        size = 18.dp,
                        tint = if (enabled) colors.text3 else colors.textDisabled,
                    )
                    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        if (query.isEmpty()) {
                            MarkyText(
                                text = placeholder,
                                style = MarkyTheme.typography.bodyL,
                                color = if (enabled) colors.text3 else colors.textDisabled,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        innerTextField()
                    }
                    if (query.isNotEmpty() && enabled) ClearButton(onClick = onClearClick)
                }
            }
        },
    )
}

@Composable
private fun ClearButton(onClick: () -> Unit) {
    val source = remember { MutableInteractionSource() }
    val interaction = source.collectMarkyInteraction()
    val colors = MarkyTheme.colors
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(32.dp)
            .markyFocusRing(interaction.focused, MarkyShapes.sm, colors.focus, visualSize = DpSize(32.dp, 32.dp))
            .pointerHoverIcon(PointerIcon.Hand),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (interaction.hovered || interaction.pressed) colors.stateHover else Color.Transparent,
            contentColor = if (interaction.hovered || interaction.pressed) colors.text else colors.text3,
        ),
        interactionSource = source,
        shape = MarkyShapes.sm,
    ) {
        MarkyIcon(icon = MarkyIcons.Clear, contentDescription = stringResource(Res.string.clear_search), size = 18.dp)
    }
}

/**
 * The search entry on the notes list: looks like the search field and opens the Search screen.
 * Desktop shows the [shortcut] (Ctrl F / ⌘F) at its end.
 */
@Composable
fun MarkySearchEntry(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shortcut: MarkyShortcut? = null,
    placeholder: String = stringResource(Res.string.search_notes),
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interaction = source.collectMarkyInteraction()
    val colors = MarkyTheme.colors
    val borderColor = animateColorAsState(
        targetValue = if (interaction.hovered) colors.borderStrong else colors.border,
        animationSpec = tween(MarkyMotion.DURATION_FAST_MS, easing = MarkyMotion.ease),
    ).value
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(MarkyDimens.fieldHeight)
            .markyFocusRing(interaction.focused, MarkyShapes.md, colors.focus, visualSize = DpSize(Dp.Unspecified, MarkyDimens.fieldHeight))
            .pointerHoverIcon(PointerIcon.Hand),
        shape = MarkyShapes.md,
        color = colors.bg,
        contentColor = colors.text3,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        interactionSource = source,
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MarkyDimens.space2),
        ) {
            MarkyIcon(icon = MarkyIcons.Search, contentDescription = null, size = 18.dp)
            MarkyText(
                text = placeholder,
                style = MarkyTheme.typography.bodyL,
                color = LocalContentColor.current,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (shortcut != null) MarkyShortcutKey(shortcut)
        }
    }
}

/**
 * The note title, edited in place with no visible box. Empty shows "Untitled" in text-3.
 */
@Composable
fun MarkyTitleField(
    title: String,
    onTitleChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
) {
    val colors = MarkyTheme.colors
    val style = MarkyTheme.typography.titleL
    BasicTextField(
        value = title,
        onValueChange = onTitleChange,
        modifier = modifier.fillMaxWidth().heightIn(min = 30.dp),
        textStyle = style.copy(color = colors.text),
        cursorBrush = SolidColor(colors.accent),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Box {
                if (title.isEmpty()) MarkyText(text = stringResource(Res.string.untitled), style = style, color = colors.text3)
                innerTextField()
            }
        },
    )
}

// ---------- Previews ----------

@Preview(name = "Search · placeholder", widthDp = 760)
@Composable
private fun SearchPlaceholderPreview() {
    MarkyPreviewPanels { MarkySearchField(query = "", onQueryChange = {}, onClearClick = {}) }
}

@Preview(name = "Search · hover", widthDp = 760)
@Composable
private fun SearchHoverPreview() {
    MarkyPreviewPanels { ForcedInteraction(MarkyInteraction.Hovered) { MarkySearchField(query = "", onQueryChange = {}, onClearClick = {}) } }
}

@Preview(name = "Search · focused", widthDp = 760)
@Composable
private fun SearchFocusedPreview() {
    MarkyPreviewPanels { ForcedInteraction(MarkyInteraction.Focused) { MarkySearchField(query = "", onQueryChange = {}, onClearClick = {}) } }
}

@Preview(name = "Search · with text", widthDp = 760)
@Composable
private fun SearchWithTextPreview() {
    MarkyPreviewPanels { ForcedInteraction(MarkyInteraction.Focused) { MarkySearchField(query = "lisbon", onQueryChange = {}, onClearClick = {}) } }
}

@Preview(name = "Search · disabled", widthDp = 760)
@Composable
private fun SearchDisabledPreview() {
    MarkyPreviewPanels { MarkySearchField(query = "", onQueryChange = {}, onClearClick = {}, enabled = false) }
}

@Preview(name = "Search entry · phone, hover, focused", widthDp = 760)
@Composable
private fun SearchEntryPreview() {
    MarkyPreviewPanels {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MarkySearchEntry(onClick = {})
            ForcedInteraction(MarkyInteraction.Hovered) { MarkySearchEntry(onClick = {}) }
            ForcedInteraction(MarkyInteraction.Focused) { MarkySearchEntry(onClick = {}) }
        }
    }
}

@Preview(name = "Search entry · desktop shortcut", widthDp = 760)
@Composable
private fun SearchEntryShortcutPreview() {
    MarkyPreviewPanels { MarkySearchEntry(onClick = {}, shortcut = MarkyShortcut.ctrl('f')) }
}

@Preview(name = "Title · filled", widthDp = 760)
@Composable
private fun TitleFilledPreview() {
    MarkyPreviewPanels { MarkyTitleField(title = "Lisbon trip", onTitleChange = {}) }
}

@Preview(name = "Title · empty", widthDp = 760)
@Composable
private fun TitleEmptyPreview() {
    MarkyPreviewPanels { MarkyTitleField(title = "", onTitleChange = {}) }
}

@Preview(name = "Title · long, wraps", widthDp = 760)
@Composable
private fun TitleLongPreview() {
    MarkyPreviewPanels { MarkyTitleField(title = "Notes from the product offsite in Porto, day two afternoon", onTitleChange = {}) }
}


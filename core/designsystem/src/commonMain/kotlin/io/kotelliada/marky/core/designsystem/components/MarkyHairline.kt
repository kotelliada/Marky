package io.kotelliada.marky.core.designsystem.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.kotelliada.marky.core.designsystem.theme.MarkyDimens
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme

/** The 1 dp border-color line Margin uses instead of cards and shadows. */
@Composable
fun MarkyHairline(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier, thickness = MarkyDimens.hairline, color = MarkyTheme.colors.border)
}

/** Vertical [MarkyHairline], for example between the desktop sidebar and the note pane. */
@Composable
fun MarkyVerticalHairline(modifier: Modifier = Modifier) {
    VerticalDivider(modifier = modifier, thickness = MarkyDimens.hairline, color = MarkyTheme.colors.border)
}

package io.kotelliada.marky.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember

/**
 * Applies Margin. Light and dark follow the system; dark is its own palette, not an inversion.
 *
 * Margin shows hover and press as flat state layers and draws its own focus ring, so the
 * Material ripple (and the focus indication that comes with it) is switched off here.
 */
@Composable
fun MarkyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) MarkyDarkColors else MarkyLightColors
    val typography = rememberMarkyTypography()
    val colorScheme = remember(colors) { colors.toMaterialColorScheme() }
    val materialTypography = remember(typography) { typography.toMaterialTypography() }
    val selectionColors = remember(colors) {
        TextSelectionColors(handleColor = colors.accent, backgroundColor = colors.selection)
    }

    MaterialTheme(colorScheme = colorScheme, typography = materialTypography) {
        CompositionLocalProvider(
            LocalMarkyColors provides colors,
            LocalMarkyTypography provides typography,
            LocalRippleConfiguration provides null,
            LocalTextSelectionColors provides selectionColors,
            LocalContentColor provides colors.text,
            content = content,
        )
    }
}

/** Access to the current Margin tokens. Dimensions, shapes and motion are constants: [MarkyDimens], [MarkyShapes], [MarkyMotion]. */
object MarkyTheme {
    val colors: MarkyColors
        @Composable @ReadOnlyComposable
        get() = LocalMarkyColors.current

    val typography: MarkyTypography
        @Composable @ReadOnlyComposable
        get() = LocalMarkyTypography.current
}

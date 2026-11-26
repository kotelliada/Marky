package io.kotelliada.marky.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Every Margin color token (designs/margin/tokens.css). Names follow the `--mk-*` tokens.
 * Read them through `MarkyTheme.colors`, never from [MarkyLightColors] / [MarkyDarkColors] directly.
 */
@Immutable
data class MarkyColors(
    // Surfaces
    val bg: Color,
    val surface: Color,
    val surface2: Color,
    val float: Color,
    val border: Color,
    val borderStrong: Color,
    // Text
    val text: Color,
    val text2: Color,
    val text3: Color,
    val textDisabled: Color,
    val marker: Color,
    // Accent (cobalt)
    val accent: Color,
    val accentHover: Color,
    val accentPressed: Color,
    val onAccent: Color,
    val accentSoft: Color,
    val onAccentSoft: Color,
    val focus: Color,
    val selection: Color,
    // Danger (delete only)
    val danger: Color,
    val dangerHover: Color,
    val dangerPressed: Color,
    val onDanger: Color,
    val dangerSoft: Color,
    // Feedback
    val highlight: Color,
    val onHighlight: Color,
    val inverse: Color,
    val onInverse: Color,
    val inverseAccent: Color,
    val scrim: Color,
    // State layers, painted over a surface
    val stateHover: Color,
    val statePressed: Color,
    // Code
    val codeBg: Color,
    val codeText: Color,
    val codeKeyword: Color,
    val codeString: Color,
    val codeComment: Color,
    val codeNumber: Color,
    // Elevation
    val shadowColor: Color,
    val isDark: Boolean,
)

val MarkyLightColors: MarkyColors = MarkyColors(
    bg = cssColor("#FFFFFF"),
    surface = cssColor("#F4F6F9"),
    surface2 = cssColor("#E9EDF3"),
    float = cssColor("#FFFFFF"),
    border = cssColor("#E2E6EC"),
    borderStrong = cssColor("#C3CAD5"),
    text = cssColor("#14171C"),
    text2 = cssColor("#4A5363"),
    text3 = cssColor("#667080"),
    textDisabled = cssColor("#A3ABB8"),
    marker = cssColor("#9AA3B1"),
    accent = cssColor("#2F5BEA"),
    accentHover = cssColor("#2850D6"),
    accentPressed = cssColor("#2145BF"),
    onAccent = cssColor("#FFFFFF"),
    accentSoft = cssColor("#E7EDFF"),
    onAccentSoft = cssColor("#1D3FB8"),
    focus = cssColor("#2F5BEA"),
    selection = cssColor("#CAD6FB"),
    danger = cssColor("#C8322B"),
    dangerHover = cssColor("#B12B25"),
    dangerPressed = cssColor("#99241F"),
    onDanger = cssColor("#FFFFFF"),
    dangerSoft = cssColor("#FDECEA"),
    highlight = cssColor("#FFE27A"),
    onHighlight = cssColor("#14171C"),
    inverse = cssColor("#1E232B"),
    onInverse = cssColor("#F1F3F6"),
    inverseAccent = cssColor("#A9BDFF"),
    scrim = cssColor("#0E11167A"),
    stateHover = cssColor("#14171C0A"),
    statePressed = cssColor("#14171C17"),
    codeBg = cssColor("#F6F8FA"),
    codeText = cssColor("#1E2530"),
    codeKeyword = cssColor("#B4236A"),
    codeString = cssColor("#1C7C4A"),
    codeComment = cssColor("#667080"),
    codeNumber = cssColor("#9A5B00"),
    shadowColor = cssColor("#0E11162E"),
    isDark = false,
)

val MarkyDarkColors: MarkyColors = MarkyColors(
    bg = cssColor("#0E1116"),
    surface = cssColor("#161A21"),
    surface2 = cssColor("#1F2530"),
    float = cssColor("#1A1F27"),
    border = cssColor("#262D38"),
    borderStrong = cssColor("#3A4352"),
    text = cssColor("#E6E9EF"),
    text2 = cssColor("#A9B1BF"),
    text3 = cssColor("#8A93A3"),
    textDisabled = cssColor("#4E5766"),
    marker = cssColor("#5A6473"),
    accent = cssColor("#7C9BFF"),
    accentHover = cssColor("#94AEFF"),
    accentPressed = cssColor("#6888F5"),
    onAccent = cssColor("#0B1020"),
    accentSoft = cssColor("#1B2442"),
    onAccentSoft = cssColor("#B4C5FF"),
    focus = cssColor("#7C9BFF"),
    selection = cssColor("#2C3A66"),
    danger = cssColor("#FF7A70"),
    dangerHover = cssColor("#FF948C"),
    dangerPressed = cssColor("#F0625A"),
    onDanger = cssColor("#1A0705"),
    dangerSoft = cssColor("#3A1715"),
    highlight = cssColor("#6B5714"),
    onHighlight = cssColor("#FFF6D6"),
    inverse = cssColor("#E6E9EF"),
    onInverse = cssColor("#14171C"),
    inverseAccent = cssColor("#2549C9"),
    scrim = cssColor("#000000A3"),
    stateHover = cssColor("#E6E9EF0F"),
    statePressed = cssColor("#E6E9EF1F"),
    codeBg = cssColor("#12161C"),
    codeText = cssColor("#D6DCE6"),
    codeKeyword = cssColor("#F08BC0"),
    codeString = cssColor("#7FD3A3"),
    codeComment = cssColor("#8A93A3"),
    codeNumber = cssColor("#F2B366"),
    shadowColor = cssColor("#00000099"),
    isDark = true,
)

internal val LocalMarkyColors = staticCompositionLocalOf { MarkyLightColors }

/**
 * Parses a CSS hex color as written in tokens.css: `#RRGGBB`, or `#RRGGBBAA` with the alpha
 * LAST. Compose's `Color(Long)` expects alpha FIRST (`0xAARRGGBB`), which is why the tokens
 * are parsed instead of being retyped as Long literals.
 */
internal fun cssColor(hex: String): Color {
    val digits = hex.removePrefix("#")
    require(digits.length == 6 || digits.length == 8) { "Expected #RRGGBB or #RRGGBBAA, got $hex" }
    require(digits.all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }) { "Not a hex color: $hex" }
    val rgb = digits.substring(0, 6).toLong(16)
    val alpha = if (digits.length == 8) digits.substring(6, 8).toLong(16) else 0xFF
    return Color((alpha shl 24) or rgb)
}

/**
 * Maps Margin onto Material 3 per the design system's "Mapping to Compose" table, so the
 * Material components that Margin components wrap pick up the right colors.
 * Slots the table doesn't list are pointed at the nearest Margin token so no Material
 * default (purple) can leak into a wrapped component.
 */
internal fun MarkyColors.toMaterialColorScheme(): ColorScheme {
    val base = if (isDark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = accent,
        onPrimary = onAccent,
        primaryContainer = accentSoft,
        onPrimaryContainer = onAccentSoft,
        inversePrimary = inverseAccent,
        secondary = accent,
        onSecondary = onAccent,
        secondaryContainer = accentSoft,
        onSecondaryContainer = onAccentSoft,
        tertiary = accent,
        onTertiary = onAccent,
        tertiaryContainer = accentSoft,
        onTertiaryContainer = onAccentSoft,
        background = bg,
        onBackground = text,
        surface = bg,
        onSurface = text,
        surfaceVariant = surface2,
        onSurfaceVariant = text2,
        surfaceTint = Color.Transparent,
        inverseSurface = inverse,
        inverseOnSurface = onInverse,
        error = danger,
        onError = onDanger,
        errorContainer = dangerSoft,
        onErrorContainer = danger,
        outline = borderStrong,
        outlineVariant = border,
        scrim = scrim,
        surfaceBright = bg,
        surfaceDim = surface,
        surfaceContainerLowest = bg,
        surfaceContainerLow = bg,
        surfaceContainer = surface,
        surfaceContainerHigh = surface2,
        surfaceContainerHighest = float,
    )
}

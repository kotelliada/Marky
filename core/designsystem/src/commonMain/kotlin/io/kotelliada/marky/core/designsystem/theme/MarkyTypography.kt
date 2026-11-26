package io.kotelliada.marky.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.kotelliada.marky.core.designsystem.resources.Res
import io.kotelliada.marky.core.designsystem.resources.ibm_plex_mono_italic
import io.kotelliada.marky.core.designsystem.resources.ibm_plex_mono_medium
import io.kotelliada.marky.core.designsystem.resources.ibm_plex_mono_regular
import io.kotelliada.marky.core.designsystem.resources.ibm_plex_mono_semibold
import io.kotelliada.marky.core.designsystem.resources.ibm_plex_sans_italic
import io.kotelliada.marky.core.designsystem.resources.ibm_plex_sans_medium
import io.kotelliada.marky.core.designsystem.resources.ibm_plex_sans_regular
import io.kotelliada.marky.core.designsystem.resources.ibm_plex_sans_semibold
import io.kotelliada.marky.core.designsystem.resources.ibm_plex_sans_semibold_italic
import org.jetbrains.compose.resources.Font

/**
 * The fourteen Margin type styles (tokens.css `--mk-type-*` plus the tracking in `.mk-type-*`).
 * Plex Sans for prose and interface, Plex Mono for anything machine-like.
 * `overline` text is shown uppercase: components uppercase the string, a TextStyle can't.
 */
@Immutable
data class MarkyTypography(
    val sans: FontFamily,
    val mono: FontFamily,
    val display: TextStyle,
    val titleL: TextStyle,
    val titleM: TextStyle,
    val titleS: TextStyle,
    val bodyL: TextStyle,
    val bodyM: TextStyle,
    val bodyS: TextStyle,
    val labelL: TextStyle,
    val labelM: TextStyle,
    val control: TextStyle,
    val caption: TextStyle,
    val overline: TextStyle,
    val code: TextStyle,
    val editor: TextStyle,
)

/** CSS line-height spreads the extra leading evenly above and below the text. */
private val CssLineHeight = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

private fun style(
    family: FontFamily,
    weight: FontWeight,
    size: Float,
    lineHeight: Float,
    trackingEm: Float = 0f,
) = TextStyle(
    fontFamily = family,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = trackingEm.em,
    lineHeightStyle = CssLineHeight,
)

internal fun markyTypography(sans: FontFamily, mono: FontFamily) = MarkyTypography(
    sans = sans,
    mono = mono,
    display = style(sans, FontWeight.SemiBold, 26f, 32f, -0.02f),
    titleL = style(sans, FontWeight.SemiBold, 24f, 30f, -0.02f),
    titleM = style(sans, FontWeight.SemiBold, 18f, 24f, -0.01f),
    titleS = style(sans, FontWeight.SemiBold, 15f, 22f),
    bodyL = style(sans, FontWeight.Normal, 15f, 24f),
    bodyM = style(sans, FontWeight.Normal, 14f, 20f),
    bodyS = style(sans, FontWeight.Normal, 13f, 19f),
    labelL = style(sans, FontWeight.SemiBold, 15f, 20f),
    labelM = style(sans, FontWeight.Medium, 14f, 20f),
    control = style(mono, FontWeight.Medium, 12f, 16f),
    caption = style(mono, FontWeight.Normal, 12f, 16f),
    overline = style(mono, FontWeight.Medium, 10.5f, 14f, 0.14f),
    code = style(mono, FontWeight.Normal, 12.5f, 20f),
    editor = style(mono, FontWeight.Normal, 14f, 24f),
)

/** Used before fonts load and outside a MarkyTheme; real screens always get Plex. */
internal val LocalMarkyTypography = staticCompositionLocalOf {
    markyTypography(FontFamily.SansSerif, FontFamily.Monospace)
}

/** Loads the Plex files that design-system.html lists (and only those). */
@Composable
internal fun rememberMarkyTypography(): MarkyTypography {
    val sans = FontFamily(
        Font(Res.font.ibm_plex_sans_regular, FontWeight.Normal),
        Font(Res.font.ibm_plex_sans_italic, FontWeight.Normal, FontStyle.Italic),
        Font(Res.font.ibm_plex_sans_medium, FontWeight.Medium),
        Font(Res.font.ibm_plex_sans_semibold, FontWeight.SemiBold),
        Font(Res.font.ibm_plex_sans_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
    )
    val mono = FontFamily(
        Font(Res.font.ibm_plex_mono_regular, FontWeight.Normal),
        Font(Res.font.ibm_plex_mono_italic, FontWeight.Normal, FontStyle.Italic),
        Font(Res.font.ibm_plex_mono_medium, FontWeight.Medium),
        Font(Res.font.ibm_plex_mono_semibold, FontWeight.SemiBold),
    )
    return remember(sans, mono) { markyTypography(sans, mono) }
}

/** Lets the Material 3 internals of wrapped components render in Plex too. */
internal fun MarkyTypography.toMaterialTypography() = Typography(
    displayLarge = display,
    displayMedium = display,
    displaySmall = display,
    headlineLarge = display,
    headlineMedium = titleL,
    headlineSmall = titleM,
    titleLarge = titleL,
    titleMedium = titleM,
    titleSmall = titleS,
    bodyLarge = bodyL,
    bodyMedium = bodyM,
    bodySmall = bodyS,
    labelLarge = labelL,
    labelMedium = labelM,
    labelSmall = caption,
)

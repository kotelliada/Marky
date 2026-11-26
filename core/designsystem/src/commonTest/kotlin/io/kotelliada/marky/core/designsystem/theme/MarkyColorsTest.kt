package io.kotelliada.marky.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEqualTo
import assertk.assertions.isTrue
import kotlin.test.Test

class MarkyColorsTest {

    // Break vectors:
    // - translucent tokens (scrim, state layers, shadow) losing or misplacing their alpha
    // - dark being a copy/inversion of light, or the isDark flag being wrong (it picks the M3 base)
    // - a Material slot mapped to the wrong token, swapped light/dark, or left at the M3 default
    //   (purple) where a wrapped component would pick it up

    @Test
    fun `translucent tokens keep their alpha`() {
        val expected = mapOf(
            "light scrim" to (MarkyLightColors.scrim to 0x7A),
            "light state-hover" to (MarkyLightColors.stateHover to 0x0A),
            "light state-pressed" to (MarkyLightColors.statePressed to 0x17),
            "light shadow" to (MarkyLightColors.shadowColor to 0x2E),
            "dark scrim" to (MarkyDarkColors.scrim to 0xA3),
            "dark state-hover" to (MarkyDarkColors.stateHover to 0x0F),
            "dark state-pressed" to (MarkyDarkColors.statePressed to 0x1F),
            "dark shadow" to (MarkyDarkColors.shadowColor to 0x99),
        )
        expected.forEach { (name, pair) ->
            val (color, alpha) = pair
            assertThat((color.alpha * 255f + 0.5f).toInt(), name = name).isEqualTo(alpha)
        }
    }

    @Test
    fun `dark is its own palette flagged as dark`() {
        assertThat(MarkyLightColors.isDark).isFalse()
        assertThat(MarkyDarkColors.isDark).isTrue()
        assertThat(MarkyDarkColors.bg).isEqualTo(Color(0xFF0E1116))
        assertThat(MarkyDarkColors.float).isNotEqualTo(MarkyDarkColors.bg)
    }

    @Test
    fun `material slots follow the mapping table in both themes`() {
        listOf(MarkyLightColors, MarkyDarkColors).forEach { c ->
            val s = c.toMaterialColorScheme()
            val theme = if (c.isDark) "dark" else "light"
            mapOf(
                "primary" to (s.primary to c.accent),
                "onPrimary" to (s.onPrimary to c.onAccent),
                "primaryContainer" to (s.primaryContainer to c.accentSoft),
                "onPrimaryContainer" to (s.onPrimaryContainer to c.onAccentSoft),
                "background" to (s.background to c.bg),
                "surface" to (s.surface to c.bg),
                "surfaceContainer" to (s.surfaceContainer to c.surface),
                "surfaceContainerHigh" to (s.surfaceContainerHigh to c.surface2),
                "surfaceContainerHighest" to (s.surfaceContainerHighest to c.float),
                "onBackground" to (s.onBackground to c.text),
                "onSurface" to (s.onSurface to c.text),
                "onSurfaceVariant" to (s.onSurfaceVariant to c.text2),
                "outlineVariant" to (s.outlineVariant to c.border),
                "outline" to (s.outline to c.borderStrong),
                "error" to (s.error to c.danger),
                "onError" to (s.onError to c.onDanger),
                "errorContainer" to (s.errorContainer to c.dangerSoft),
                "inverseSurface" to (s.inverseSurface to c.inverse),
                "inverseOnSurface" to (s.inverseOnSurface to c.onInverse),
                "inversePrimary" to (s.inversePrimary to c.inverseAccent),
                "scrim" to (s.scrim to c.scrim),
            ).forEach { (slot, pair) ->
                assertThat(pair.first, name = "$theme $slot").isEqualTo(pair.second)
            }
        }
    }

    @Test
    fun `unlisted accent slots point at the accent instead of the material default`() {
        listOf(MarkyLightColors, MarkyDarkColors).forEach { c ->
            val s = c.toMaterialColorScheme()
            assertThat(s.secondary).isEqualTo(c.accent)
            assertThat(s.tertiary).isEqualTo(c.accent)
            assertThat(s.surfaceTint).isEqualTo(Color.Transparent)
        }
    }
}

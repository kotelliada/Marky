package io.kotelliada.marky.core.designsystem.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class MarkyTypographyTest {

    // Break vectors:
    // - a style on the wrong family (mono vs sans decides "machine vs prose")
    // - fractional sizes (10.5, 12.5) rounded away
    // - tracking given in sp instead of em, or with the sign flipped
    // - a weight typo (500 vs 600)
    private val sans = FontFamily.SansSerif
    private val mono = FontFamily.Monospace
    private val t = markyTypography(sans, mono)

    private data class Spec(val family: FontFamily, val weight: Int, val size: Float, val line: Float, val trackingEm: Float = 0f)

    @Test
    fun `every style matches tokens css`() {
        mapOf(
            "display" to (t.display to Spec(sans, 600, 26f, 32f, -0.02f)),
            "title-l" to (t.titleL to Spec(sans, 600, 24f, 30f, -0.02f)),
            "title-m" to (t.titleM to Spec(sans, 600, 18f, 24f, -0.01f)),
            "title-s" to (t.titleS to Spec(sans, 600, 15f, 22f)),
            "body-l" to (t.bodyL to Spec(sans, 400, 15f, 24f)),
            "body-m" to (t.bodyM to Spec(sans, 400, 14f, 20f)),
            "body-s" to (t.bodyS to Spec(sans, 400, 13f, 19f)),
            "label-l" to (t.labelL to Spec(sans, 600, 15f, 20f)),
            "label-m" to (t.labelM to Spec(sans, 500, 14f, 20f)),
            "control" to (t.control to Spec(mono, 500, 12f, 16f)),
            "caption" to (t.caption to Spec(mono, 400, 12f, 16f)),
            "overline" to (t.overline to Spec(mono, 500, 10.5f, 14f, 0.14f)),
            "code" to (t.code to Spec(mono, 400, 12.5f, 20f)),
            "editor" to (t.editor to Spec(mono, 400, 14f, 24f)),
        ).forEach { (name, pair) -> assertSpec(name, pair.first, pair.second) }
    }

    private fun assertSpec(name: String, style: TextStyle, spec: Spec) {
        assertThat(style.fontFamily, name = "$name family").isEqualTo(spec.family)
        assertThat(style.fontWeight, name = "$name weight").isEqualTo(FontWeight(spec.weight))
        assertThat(style.fontSize, name = "$name size").isEqualTo(spec.size.sp)
        assertThat(style.lineHeight, name = "$name line height").isEqualTo(spec.line.sp)
        assertThat(style.letterSpacing, name = "$name tracking").isEqualTo(spec.trackingEm.em)
    }
}

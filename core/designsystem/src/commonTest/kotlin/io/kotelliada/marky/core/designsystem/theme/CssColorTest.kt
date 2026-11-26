package io.kotelliada.marky.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlin.test.Test

class CssColorTest {

    // Break vectors for cssColor:
    // - alpha position: CSS puts alpha LAST (#RRGGBBAA), Compose expects it FIRST (0xAARRGGBB)
    // - alpha extremes 00 and FF, and the absence of alpha (6 digits = opaque)
    // - case: tokens.css uses upper case, nothing stops lower case
    // - shapes that must be rejected instead of misparsed: 3/4-digit shorthand, 7 digits,
    //   missing digits, non-hex characters, a sign that Long.parse would accept
    // - the '#' prefix being optional must not change the value

    @Test
    fun `six digits are opaque`() {
        assertThat(cssColor("#2F5BEA")).isEqualTo(Color(0xFF2F5BEA))
    }

    @Test
    fun `eight digits read alpha from the last two digits`() {
        assertThat(cssColor("#0E11167A")).isEqualTo(Color(0x7A0E1116))
    }

    @Test
    fun `alpha 00 is fully transparent and keeps its rgb`() {
        val color = cssColor("#14171C00")
        assertThat(color.alpha).isEqualTo(0f)
        assertThat(color.copy(alpha = 1f)).isEqualTo(Color(0xFF14171C))
    }

    @Test
    fun `lower case parses like upper case`() {
        assertThat(cssColor("#e6e9ef0f")).isEqualTo(cssColor("#E6E9EF0F"))
    }

    @Test
    fun `hash prefix is optional`() {
        assertThat(cssColor("FFE27A")).isEqualTo(cssColor("#FFE27A"))
    }

    @Test
    fun `malformed colors are rejected`() {
        listOf("", "#", "#FFF", "#FFFF", "#12345", "#1234567", "#123456789", "#GGGGGG", "#-12345", "#+12345", "# 12345").forEach { hex ->
            assertFailure { cssColor(hex) }.isInstanceOf(IllegalArgumentException::class)
        }
    }
}

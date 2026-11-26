package io.kotelliada.marky.core.designsystem.components

import androidx.compose.ui.graphics.Color
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.kotelliada.marky.core.designsystem.interaction.MarkyInteraction
import io.kotelliada.marky.core.designsystem.theme.MarkyDarkColors
import io.kotelliada.marky.core.designsystem.theme.MarkyLightColors
import kotlin.test.Test

class InteractionColorsTest {

    // Break vectors for the color resolvers:
    // - priority: disabled must beat pressed and hover; pressed must beat hover (a mouse press
    //   is always also a hover); "on" states (selected, active, checked) must beat both
    // - focus must not change colors (the ring is drawn separately)
    // - a variant picking up another variant's palette (secondary using accent, danger using accent)
    // - light/dark: resolvers must read the palette given, never a hard-coded one
    private val c = MarkyLightColors
    private val hoverAndPress = MarkyInteraction(hovered = true, pressed = true)

    @Test
    fun `primary button walks rest hover pressed disabled`() {
        val v = MarkyButtonVariant.Primary
        assertThat(markyButtonColors(v, MarkyInteraction.Rest, true, c).container).isEqualTo(c.accent)
        assertThat(markyButtonColors(v, MarkyInteraction.Hovered, true, c).container).isEqualTo(c.accentHover)
        assertThat(markyButtonColors(v, hoverAndPress, true, c).container).isEqualTo(c.accentPressed)
        assertThat(markyButtonColors(v, hoverAndPress, false, c)).isEqualTo(MarkyButtonColors(c.surface2, c.textDisabled, Color.Transparent))
    }

    @Test
    fun `danger button uses the danger palette never the accent`() {
        val v = MarkyButtonVariant.Danger
        assertThat(markyButtonColors(v, MarkyInteraction.Rest, true, c)).isEqualTo(MarkyButtonColors(c.danger, c.onDanger, Color.Transparent))
        assertThat(markyButtonColors(v, MarkyInteraction.Hovered, true, c).container).isEqualTo(c.dangerHover)
        assertThat(markyButtonColors(v, hoverAndPress, true, c).container).isEqualTo(c.dangerPressed)
    }

    @Test
    fun `secondary button keeps a border and greys it when disabled`() {
        val v = MarkyButtonVariant.Secondary
        assertThat(markyButtonColors(v, MarkyInteraction.Rest, true, c)).isEqualTo(MarkyButtonColors(c.bg, c.text, c.borderStrong))
        assertThat(markyButtonColors(v, MarkyInteraction.Hovered, true, c).container).isEqualTo(c.surface)
        assertThat(markyButtonColors(v, hoverAndPress, true, c).container).isEqualTo(c.surface2)
        assertThat(markyButtonColors(v, MarkyInteraction.Rest, false, c)).isEqualTo(MarkyButtonColors(c.bg, c.textDisabled, c.border))
    }

    @Test
    fun `ghost button paints state layers over nothing`() {
        val v = MarkyButtonVariant.Ghost
        assertThat(markyButtonColors(v, MarkyInteraction.Rest, true, c).container).isEqualTo(Color.Transparent)
        assertThat(markyButtonColors(v, MarkyInteraction.Hovered, true, c).container).isEqualTo(c.stateHover)
        assertThat(markyButtonColors(v, hoverAndPress, true, c).container).isEqualTo(c.statePressed)
        assertThat(markyButtonColors(v, hoverAndPress, false, c).container).isEqualTo(Color.Transparent)
    }

    @Test
    fun `focus alone changes no colors`() {
        MarkyButtonVariant.entries.forEach { v ->
            assertThat(markyButtonColors(v, MarkyInteraction.Focused, true, c), name = v.name)
                .isEqualTo(markyButtonColors(v, MarkyInteraction.Rest, true, c))
        }
        assertThat(markyIconButtonColors(MarkyInteraction.Focused, true, false, c)).isEqualTo(markyIconButtonColors(MarkyInteraction.Rest, true, false, c))
        assertThat(markyNoteRowBackground(MarkyInteraction.Focused, false, c)).isEqualTo(Color.Transparent)
    }

    @Test
    fun `icon button stays accent when checked even while hovered`() {
        assertThat(markyIconButtonColors(MarkyInteraction.Rest, true, false, c)).isEqualTo(MarkyIconButtonColors(Color.Transparent, c.text2))
        assertThat(markyIconButtonColors(MarkyInteraction.Hovered, true, false, c)).isEqualTo(MarkyIconButtonColors(c.stateHover, c.text))
        assertThat(markyIconButtonColors(MarkyInteraction.Hovered, true, true, c)).isEqualTo(MarkyIconButtonColors(c.stateHover, c.accent))
        assertThat(markyIconButtonColors(hoverAndPress, false, true, c)).isEqualTo(MarkyIconButtonColors(Color.Transparent, c.textDisabled))
    }

    @Test
    fun `selected segment ignores hover and press`() {
        assertThat(markySegmentColors(hoverAndPress, selected = true, enabled = true, c)).isEqualTo(MarkySegmentColors(c.surface2, c.text))
        assertThat(markySegmentColors(MarkyInteraction.Rest, selected = false, enabled = true, c)).isEqualTo(MarkySegmentColors(Color.Transparent, c.text3))
        assertThat(markySegmentColors(MarkyInteraction.Hovered, selected = false, enabled = true, c)).isEqualTo(MarkySegmentColors(c.stateHover, c.text))
    }

    @Test
    fun `disabled segment keeps a surface fill only when selected`() {
        assertThat(markySegmentColors(MarkyInteraction.Rest, selected = true, enabled = false, c)).isEqualTo(MarkySegmentColors(c.surface, c.textDisabled))
        assertThat(markySegmentColors(MarkyInteraction.Rest, selected = false, enabled = false, c)).isEqualTo(MarkySegmentColors(Color.Transparent, c.textDisabled))
    }

    @Test
    fun `active tool beats pressed and disabled beats active`() {
        assertThat(markyToolColors(hoverAndPress, active = true, enabled = true, c)).isEqualTo(MarkyToolColors(c.accentSoft, c.accent))
        assertThat(markyToolColors(MarkyInteraction.Rest, active = true, enabled = false, c)).isEqualTo(MarkyToolColors(Color.Transparent, c.textDisabled))
        assertThat(markyToolColors(MarkyInteraction.Pressed, active = false, enabled = true, c)).isEqualTo(MarkyToolColors(c.statePressed, c.text))
    }

    @Test
    fun `danger menu item uses danger soft for both hover and press`() {
        assertThat(markyMenuItemColors(MarkyInteraction.Hovered, true, true, c).container).isEqualTo(c.dangerSoft)
        assertThat(markyMenuItemColors(MarkyInteraction.Pressed, true, true, c).container).isEqualTo(c.dangerSoft)
        assertThat(markyMenuItemColors(MarkyInteraction.Rest, true, true, c)).isEqualTo(MarkyMenuItemColors(Color.Transparent, c.danger, c.danger))
        assertThat(markyMenuItemColors(MarkyInteraction.Pressed, false, true, c).container).isEqualTo(c.statePressed)
        assertThat(markyMenuItemColors(MarkyInteraction.Hovered, true, false, c)).isEqualTo(MarkyMenuItemColors(Color.Transparent, c.textDisabled, c.textDisabled))
    }

    @Test
    fun `selected row beats pressed`() {
        assertThat(markyNoteRowBackground(hoverAndPress, selected = true, c)).isEqualTo(c.accentSoft)
        assertThat(markyNoteRowBackground(hoverAndPress, selected = false, c)).isEqualTo(c.statePressed)
        assertThat(markyNoteRowBackground(MarkyInteraction.Hovered, selected = false, c)).isEqualTo(c.stateHover)
    }

    @Test
    fun `checkbox halo follows hover and press and disappears when disabled`() {
        assertThat(markyCheckboxColors(MarkyInteraction.Rest, false, true, c)).isEqualTo(MarkyCheckboxColors(c.bg, c.text3, Color.Transparent))
        assertThat(markyCheckboxColors(MarkyInteraction.Hovered, false, true, c)).isEqualTo(MarkyCheckboxColors(c.bg, c.text2, c.stateHover))
        assertThat(markyCheckboxColors(hoverAndPress, true, true, c)).isEqualTo(MarkyCheckboxColors(c.accent, c.accent, c.statePressed))
        assertThat(markyCheckboxColors(hoverAndPress, true, false, c)).isEqualTo(MarkyCheckboxColors(c.textDisabled, c.textDisabled, Color.Transparent))
        assertThat(markyCheckboxColors(hoverAndPress, false, false, c)).isEqualTo(MarkyCheckboxColors(c.bg, c.textDisabled, Color.Transparent))
    }

    @Test
    fun `resolvers read the palette they are given`() {
        val d = MarkyDarkColors
        assertThat(markyButtonColors(MarkyButtonVariant.Primary, MarkyInteraction.Rest, true, d).container).isEqualTo(d.accent)
        assertThat(markyToolColors(MarkyInteraction.Rest, true, true, d).container).isEqualTo(d.accentSoft)
        assertThat(markyNoteRowBackground(MarkyInteraction.Hovered, false, d)).isEqualTo(d.stateHover)
    }
}

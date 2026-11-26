package io.kotelliada.marky.core.designsystem.interaction

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * The interaction states Margin styles: hover (desktop only), pressed and keyboard focus.
 */
@Immutable
internal data class MarkyInteraction(
    val hovered: Boolean = false,
    val pressed: Boolean = false,
    val focused: Boolean = false,
) {
    companion object {
        val Rest = MarkyInteraction()
        val Hovered = MarkyInteraction(hovered = true)
        val Pressed = MarkyInteraction(pressed = true)
        val Focused = MarkyInteraction(focused = true)
    }
}

/**
 * Lets a preview show a component hovered, pressed or focused without real input.
 * Never provided in app code. Components that host other interactive components reset it to
 * null for their children, so a forced state only reaches the component it was meant for.
 */
internal val LocalForcedInteraction = staticCompositionLocalOf<MarkyInteraction?> { null }

@Composable
internal fun InteractionSource.collectMarkyInteraction(): MarkyInteraction {
    val hovered = collectIsHoveredAsState().value
    val pressed = collectIsPressedAsState().value
    val focused = collectIsFocusedAsState().value
    return LocalForcedInteraction.current ?: MarkyInteraction(hovered, pressed, focused)
}

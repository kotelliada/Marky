package io.kotelliada.marky.core.designsystem.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

/** Margin motion: short and functional. */
object MarkyMotion {
    /** Hover and press color changes, checkbox. */
    const val DURATION_FAST_MS = 120

    /** Menu and dialog entering. */
    const val DURATION_BASE_MS = 200

    /** Snackbar entering. */
    const val DURATION_SLOW_MS = 320

    /** Fade of the just-imported row highlight. */
    const val ROW_HIGHLIGHT_MS = 2400

    /** Share of [ROW_HIGHLIGHT_MS] the highlight holds before fading (`0%, 35%` keyframes). */
    const val ROW_HIGHLIGHT_HOLD_FRACTION = 0.35f

    /** `--mk-ease`: cubic-bezier(0.2, 0, 0, 1). */
    val ease: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
}

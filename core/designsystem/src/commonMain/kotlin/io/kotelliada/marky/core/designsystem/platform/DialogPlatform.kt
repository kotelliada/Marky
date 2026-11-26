package io.kotelliada.marky.core.designsystem.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties

/** Dialog properties that let Margin size the dialog and, where supported, color the scrim. */
internal expect fun markyDialogProperties(scrim: Color): DialogProperties

/**
 * Applies the scrim where the platform draws it outside Compose. Call inside the dialog content.
 * Android dims the window in black, so only the scrim's alpha can be matched there.
 */
@Composable
internal expect fun MarkyDialogScrim(scrim: Color)

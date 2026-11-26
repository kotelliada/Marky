package io.kotelliada.marky.core.designsystem.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

internal actual fun markyDialogProperties(scrim: Color): DialogProperties =
    DialogProperties(usePlatformDefaultWidth = false)

@Composable
internal actual fun MarkyDialogScrim(scrim: Color) {
    val window = (LocalView.current.parent as? DialogWindowProvider)?.window
    SideEffect { window?.setDimAmount(scrim.alpha) }
}

package io.kotelliada.marky.core.designsystem.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalComposeUiApi::class)
internal actual fun markyDialogProperties(scrim: Color): DialogProperties =
    DialogProperties(usePlatformDefaultWidth = false, scrimColor = scrim)

@Composable
internal actual fun MarkyDialogScrim(scrim: Color) = Unit

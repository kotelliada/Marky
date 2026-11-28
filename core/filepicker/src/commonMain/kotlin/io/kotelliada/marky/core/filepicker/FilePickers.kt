package io.kotelliada.marky.core.filepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

/** Opens the platform's file picker for one file. Get one from [rememberFileOpenLauncher]. */
@Stable
class FileOpenLauncher internal constructor(
    private val onLaunch: () -> Unit,
) {
    fun launch() = onLaunch()
}

/** Opens the platform's save dialog for one file. Get one from [rememberFileSaveLauncher]. */
@Stable
class FileSaveLauncher internal constructor(
    private val onLaunch: (suggestedName: String, bytes: ByteArray) -> Unit,
) {
    /** @param suggestedName the name offered in the dialog, including the extension. */
    fun launch(suggestedName: String, bytes: ByteArray) = onLaunch(suggestedName, bytes)
}

/**
 * A launcher for picking one file of [type] and reading it.
 *
 * Android: system document picker. iOS: Files document picker. Desktop: native open dialog.
 * [onResult] is called on the main thread once the picker closes; reading happens off it.
 *
 * @param dialogTitle shown where the platform's dialog has a title (desktop).
 */
@Composable
expect fun rememberFileOpenLauncher(
    type: FileType,
    dialogTitle: String,
    onResult: (OpenFileResult) -> Unit,
): FileOpenLauncher

/**
 * A launcher for saving one file of [type] where the user chooses.
 *
 * Android: create-document picker. iOS: Save to Files. Desktop: native save dialog.
 * [onResult] is called on the main thread once the file is written or the dialog closes.
 *
 * @param dialogTitle shown where the platform's dialog has a title (desktop).
 */
@Composable
expect fun rememberFileSaveLauncher(
    type: FileType,
    dialogTitle: String,
    onResult: (SaveFileResult) -> Unit,
): FileSaveLauncher

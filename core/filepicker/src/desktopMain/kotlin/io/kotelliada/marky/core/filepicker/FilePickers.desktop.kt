package io.kotelliada.marky.core.filepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.IOException

@Composable
actual fun rememberFileOpenLauncher(
    type: FileType,
    dialogTitle: String,
    onResult: (OpenFileResult) -> Unit,
): FileOpenLauncher {
    val scope = rememberCoroutineScope()
    val currentType by rememberUpdatedState(type)
    val currentTitle by rememberUpdatedState(dialogTitle)
    val currentOnResult by rememberUpdatedState(onResult)
    return remember {
        FileOpenLauncher {
            scope.launch { currentOnResult(openFile(currentType, currentTitle)) }
        }
    }
}

@Composable
actual fun rememberFileSaveLauncher(
    type: FileType,
    dialogTitle: String,
    onResult: (SaveFileResult) -> Unit,
): FileSaveLauncher {
    val scope = rememberCoroutineScope()
    val currentType by rememberUpdatedState(type)
    val currentTitle by rememberUpdatedState(dialogTitle)
    val currentOnResult by rememberUpdatedState(onResult)
    return remember {
        FileSaveLauncher { suggestedName, bytes ->
            scope.launch { currentOnResult(saveFile(currentType, currentTitle, suggestedName, bytes)) }
        }
    }
}

/** Shows the native open dialog (modal, on the UI thread), then reads the file off it. */
private suspend fun openFile(type: FileType, title: String): OpenFileResult {
    val dialog = FileDialog(null as Frame?, title, FileDialog.LOAD).apply {
        isMultipleMode = false
        // macOS and Linux honor the filter; Windows ignores it and uses the file pattern instead.
        setFilenameFilter { _, name -> type.matches(name) }
        if (isWindows) file = type.extensions.joinToString(";") { "*.$it" }
    }
    dialog.isVisible = true
    val name = dialog.file ?: return OpenFileResult.Cancelled
    val file = File(dialog.directory, name)
    return withContext(Dispatchers.IO) {
        try {
            OpenFileResult.Picked(name, file.readBytes())
        } catch (_: IOException) {
            OpenFileResult.Unreadable(name)
        } catch (_: SecurityException) {
            OpenFileResult.Unreadable(name)
        }
    }
}

/** Shows the native save dialog (modal, on the UI thread), then writes the file off it. */
private suspend fun saveFile(type: FileType, title: String, suggestedName: String, bytes: ByteArray): SaveFileResult {
    val dialog = FileDialog(null as Frame?, title, FileDialog.SAVE).apply {
        file = suggestedName
    }
    dialog.isVisible = true
    val chosen = dialog.file ?: return SaveFileResult.Cancelled
    val name = type.withExtension(chosen)
    val file = File(dialog.directory, name)
    return withContext(Dispatchers.IO) {
        try {
            file.writeBytes(bytes)
            SaveFileResult.Saved(name)
        } catch (_: IOException) {
            SaveFileResult.Failed
        } catch (_: SecurityException) {
            SaveFileResult.Failed
        }
    }
}

private val isWindows: Boolean =
    System.getProperty("os.name").orEmpty().startsWith("Windows", ignoreCase = true)

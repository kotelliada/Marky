package io.kotelliada.marky.core.filepicker

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun rememberFileOpenLauncher(
    type: FileType,
    dialogTitle: String,
    onResult: (OpenFileResult) -> Unit,
): FileOpenLauncher {
    val resolver = LocalContext.current.applicationContext.contentResolver
    val scope = rememberCoroutineScope()
    val currentType by rememberUpdatedState(type)
    val currentOnResult by rememberUpdatedState(onResult)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) {
            currentOnResult(OpenFileResult.Cancelled)
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            currentOnResult(withContext(Dispatchers.IO) { resolver.read(uri) })
        }
    }
    return remember(launcher) {
        FileOpenLauncher {
            launcher.launch(currentType.mimeTypes.ifEmpty { listOf(ANY_MIME_TYPE) }.toTypedArray())
        }
    }
}

@Composable
actual fun rememberFileSaveLauncher(
    type: FileType,
    dialogTitle: String,
    onResult: (SaveFileResult) -> Unit,
): FileSaveLauncher {
    val resolver = LocalContext.current.applicationContext.contentResolver
    val scope = rememberCoroutineScope()
    val currentOnResult by rememberUpdatedState(onResult)
    // Held only in memory: after process death the bytes are gone and the save reports Failed.
    val pending = remember { PendingSave() }
    val mimeType = type.mimeTypes.firstOrNull() ?: BINARY_MIME_TYPE

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(mimeType)) { uri ->
        val save = pending.take()
        when {
            uri == null -> currentOnResult(SaveFileResult.Cancelled)
            save == null -> currentOnResult(SaveFileResult.Failed)
            else -> scope.launch {
                currentOnResult(withContext(Dispatchers.IO) { resolver.write(uri, save) })
            }
        }
    }
    return remember(launcher) {
        FileSaveLauncher { suggestedName, bytes ->
            pending.put(suggestedName, bytes)
            launcher.launch(suggestedName)
        }
    }
}

private class PendingSave {
    private var save: Pair<String, ByteArray>? = null

    fun put(name: String, bytes: ByteArray) {
        save = name to bytes
    }

    fun take(): Pair<String, ByteArray>? = save.also { save = null }
}

private fun ContentResolver.read(uri: Uri): OpenFileResult {
    val name = displayName(uri) ?: uri.lastPathSegment.orEmpty()
    return try {
        val bytes = openInputStream(uri)?.use { it.readBytes() }
        if (bytes == null) OpenFileResult.Unreadable(name) else OpenFileResult.Picked(name, bytes)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        OpenFileResult.Unreadable(name)
    }
}

private fun ContentResolver.write(uri: Uri, save: Pair<String, ByteArray>): SaveFileResult {
    val (suggestedName, bytes) = save
    return try {
        val stream = openOutputStream(uri, "wt") ?: return SaveFileResult.Failed
        stream.use { it.write(bytes) }
        SaveFileResult.Saved(displayName(uri) ?: suggestedName)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        SaveFileResult.Failed
    }
}

/** The name the document provider shows, or null when it doesn't report one. */
private fun ContentResolver.displayName(uri: Uri): String? = try {
    query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst() && !cursor.isNull(0)) cursor.getString(0) else null
    }
} catch (e: Exception) {
    if (e is CancellationException) throw e
    null
}

private const val ANY_MIME_TYPE = "*/*"
private const val BINARY_MIME_TYPE = "application/octet-stream"

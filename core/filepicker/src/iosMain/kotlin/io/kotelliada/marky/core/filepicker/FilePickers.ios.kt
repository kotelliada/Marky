// Foundation's NSError out-parameters, NSData(bytes:length:) and raw byte copies are C interop.
@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package io.kotelliada.marky.core.filepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.uikit.LocalUIViewController
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.create
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.writeToURL
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UniformTypeIdentifiers.UTType
import platform.UniformTypeIdentifiers.UTTypeData
import platform.UniformTypeIdentifiers.UTTypeItem
import platform.darwin.NSObject
import platform.posix.memcpy

@Composable
actual fun rememberFileOpenLauncher(
    type: FileType,
    dialogTitle: String,
    onResult: (OpenFileResult) -> Unit,
): FileOpenLauncher {
    val viewController = LocalUIViewController.current
    val scope = rememberCoroutineScope()
    val currentType by rememberUpdatedState(type)
    val currentOnResult by rememberUpdatedState(onResult)

    // UIDocumentPickerViewController holds its delegate weakly, so the composition keeps it alive.
    val delegate = remember {
        DocumentPickerDelegate(
            onPicked = { url ->
                scope.launch { currentOnResult(withContext(Dispatchers.IO) { readCopy(url) }) }
            },
            onCancelled = { currentOnResult(OpenFileResult.Cancelled) },
        )
    }
    return remember(viewController) {
        FileOpenLauncher {
            val picker = UIDocumentPickerViewController(
                forOpeningContentTypes = currentType.contentTypes(),
                asCopy = true,
            )
            picker.delegate = delegate
            picker.allowsMultipleSelection = false
            viewController.presentViewController(picker, animated = true, completion = null)
        }
    }
}

@Composable
actual fun rememberFileSaveLauncher(
    type: FileType,
    dialogTitle: String,
    onResult: (SaveFileResult) -> Unit,
): FileSaveLauncher {
    val viewController = LocalUIViewController.current
    val scope = rememberCoroutineScope()
    val currentOnResult by rememberUpdatedState(onResult)
    val pending = remember { PendingExport() }

    val delegate = remember {
        DocumentPickerDelegate(
            onPicked = { url ->
                val export = pending.take()
                export?.let { removeTempDirectory(it.directory) }
                currentOnResult(SaveFileResult.Saved(url.lastPathComponent ?: export?.name.orEmpty()))
            },
            onCancelled = {
                pending.take()?.let { removeTempDirectory(it.directory) }
                currentOnResult(SaveFileResult.Cancelled)
            },
        )
    }
    return remember(viewController) {
        FileSaveLauncher { suggestedName, bytes ->
            scope.launch {
                val export = withContext(Dispatchers.IO) { writeTempFile(suggestedName, bytes) }
                if (export == null) {
                    currentOnResult(SaveFileResult.Failed)
                    return@launch
                }
                pending.put(export)
                val picker = UIDocumentPickerViewController(
                    forExportingURLs = listOf(export.file),
                    asCopy = true,
                )
                picker.delegate = delegate
                viewController.presentViewController(picker, animated = true, completion = null)
            }
        }
    }
}

private class DocumentPickerDelegate(
    private val onPicked: (NSURL) -> Unit,
    private val onCancelled: () -> Unit,
) : NSObject(), UIDocumentPickerDelegateProtocol {

    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
        if (url == null) onCancelled() else onPicked(url)
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) = onCancelled()
}

/** A file written to its own temp directory so the system can copy it to where the user chooses. */
private class TempExport(val name: String, val directory: NSURL, val file: NSURL)

private class PendingExport {
    private var export: TempExport? = null

    fun put(value: TempExport) {
        export?.let { removeTempDirectory(it.directory) }
        export = value
    }

    fun take(): TempExport? = export.also { export = null }
}

/** Content types for the picker. "Any data" types are left out so the filter still means something. */
private fun FileType.contentTypes(): List<UTType> {
    val types = extensions.mapNotNull { UTType.typeWithFilenameExtension(it) } +
        mimeTypes.mapNotNull { UTType.typeWithMIMEType(it) }
    return types
        .filter { it != UTTypeData && it != UTTypeItem }
        .distinctBy { it.identifier }
        .ifEmpty { listOf(UTTypeData) }
}

/** Reads the picker's private copy of the file, then deletes the copy. */
private fun readCopy(url: NSURL): OpenFileResult {
    val name = url.lastPathComponent.orEmpty()
    val accessing = url.startAccessingSecurityScopedResource()
    return try {
        val data = NSData.dataWithContentsOfURL(url)
        if (data == null) OpenFileResult.Unreadable(name) else OpenFileResult.Picked(name, data.toByteArray())
    } finally {
        if (accessing) url.stopAccessingSecurityScopedResource()
        NSFileManager.defaultManager.removeItemAtURL(url, error = null)
    }
}

private fun writeTempFile(name: String, bytes: ByteArray): TempExport? {
    val directory = NSURL.fileURLWithPath(NSTemporaryDirectory())
        .URLByAppendingPathComponent(NSUUID().UUIDString, isDirectory = true) ?: return null
    val created = NSFileManager.defaultManager.createDirectoryAtURL(
        url = directory,
        withIntermediateDirectories = true,
        attributes = null,
        error = null,
    )
    if (!created) return null
    val file = directory.URLByAppendingPathComponent(name, isDirectory = false) ?: return null
    if (!bytes.toNSData().writeToURL(file, atomically = true)) {
        removeTempDirectory(directory)
        return null
    }
    return TempExport(name = name, directory = directory, file = file)
}

private fun removeTempDirectory(directory: NSURL) {
    NSFileManager.defaultManager.removeItemAtURL(directory, error = null)
}

private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    if (size == 0) return ByteArray(0)
    val source = bytes ?: return ByteArray(0)
    return ByteArray(size).apply {
        usePinned { memcpy(it.addressOf(0), source, length) }
    }
}

private fun ByteArray.toNSData(): NSData {
    if (isEmpty()) return NSData()
    return usePinned { NSData.create(bytes = it.addressOf(0), length = size.toULong()) }
}

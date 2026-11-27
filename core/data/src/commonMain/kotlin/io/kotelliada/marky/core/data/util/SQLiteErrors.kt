package io.kotelliada.marky.core.data.util

import androidx.sqlite.SQLiteException
import io.kotelliada.marky.core.domain.util.DataError

private const val SQLITE_FULL = 13
private val ERROR_CODE = Regex("""Error code: (\d+)""")

internal fun SQLiteException.toDataError(): DataError.Local = sqliteMessageToDataError(message)

/**
 * Maps the message of a failure from the bundled SQLite driver, which looks like
 * `Error code: 13, message: database or disk is full`.
 */
internal fun sqliteMessageToDataError(message: String?): DataError.Local {
    val text = message.orEmpty()
    val code = ERROR_CODE.find(text)?.groupValues?.get(1)?.toIntOrNull()
    val diskFull = code == SQLITE_FULL || text.contains("disk is full", ignoreCase = true)
    return if (diskFull) DataError.Local.DISK_FULL else DataError.Local.UNKNOWN
}

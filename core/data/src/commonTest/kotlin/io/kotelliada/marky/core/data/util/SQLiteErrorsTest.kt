package io.kotelliada.marky.core.data.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.kotelliada.marky.core.domain.util.DataError
import kotlin.test.Test

class SQLiteErrorsTest {

    @Test
    fun `sqlite full error code is disk full`() {
        assertThat(sqliteMessageToDataError("Error code: 13, message: database or disk is full"))
            .isEqualTo(DataError.Local.DISK_FULL)
    }

    @Test
    fun `disk full message without a code is disk full`() {
        assertThat(sqliteMessageToDataError("Disk is full")).isEqualTo(DataError.Local.DISK_FULL)
    }

    @Test
    fun `other error codes and missing messages are unknown including codes that start with 13`() {
        listOf(
            "Error code: 1, message: SQL logic error",
            "Error code: 5, message: database is locked",
            "Error code: 130, message: something else",
            "Error code: 3, message: access permission denied",
            "",
            null,
        ).forEach { message ->
            assertThat(sqliteMessageToDataError(message), name = "message=\"$message\"")
                .isEqualTo(DataError.Local.UNKNOWN)
        }
    }
}

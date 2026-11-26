package io.kotelliada.marky.core.designsystem.components

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class SnackbarTimingTest {

    // Break vectors: the two durations swapped, or seconds given where millis are expected.
    @Test
    fun `errors stay six seconds and other messages four`() {
        assertThat(markySnackbarTimeoutMillis(isError = false)).isEqualTo(4_000L)
        assertThat(markySnackbarTimeoutMillis(isError = true)).isEqualTo(6_000L)
    }
}

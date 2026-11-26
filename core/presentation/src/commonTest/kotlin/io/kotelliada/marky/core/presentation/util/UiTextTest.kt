package io.kotelliada.marky.core.presentation.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class UiTextTest {

    // Break vectors for DynamicString: it must hand back user text verbatim. No trimming,
    // no unicode normalization, no splitting of multi-codepoint graphemes, and no treating
    // the text as a format string (Resource interpolates args, DynamicString must not).
    @Test
    fun `dynamic string resolves to its exact value`() = runTest {
        listOf(
            "",
            "   ",
            "\n\t",
            "  leading and trailing  ",
            "👍🏽",
            "👨‍👩‍👧‍👦 family",
            "é", // e + combining acute, must not be normalized to U+00E9
            "é",
            "مرحبا بالعالم",
            "‮evil‬",
            "zero​width",
            "%s %1\$s %d {0}",
            "# Heading **bold** [link](https://example.com)",
            "a".repeat(10_000),
        ).forEach { value ->
            assertThat(UiText.DynamicString(value).asStringAsync(), name = "value=\"$value\"")
                .isEqualTo(value)
        }
    }
}

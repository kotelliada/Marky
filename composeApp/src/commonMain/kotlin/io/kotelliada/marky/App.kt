package io.kotelliada.marky

import androidx.compose.runtime.Composable
import io.kotelliada.marky.core.designsystem.theme.MarkyTheme

/**
 * Root of the app on every platform. MarkyTheme is applied here; the content stays empty
 * until the notes list exists: no UI may be shown that isn't defined in designs/margin/.
 */
@Composable
fun App() {
    MarkyTheme {
    }
}

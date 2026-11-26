package io.kotelliada.marky.core.designsystem.platform

/** True where shortcuts use the Command key (macOS, iPadOS hardware keyboards): Ctrl reads as ⌘. */
internal expect val usesCommandKey: Boolean

/** Tooltips are a desktop-only affordance in Margin ("Tooltip and shortcuts: Desktop only"). */
internal expect val hasHoverTooltips: Boolean

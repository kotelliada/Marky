package io.kotelliada.marky.core.designsystem.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Margin line icons (designs/margin/marky.js `P`): 24 dp grid, 1.75 stroke, round caps and joins.
 * Draw them with `MarkyIcon`, which sizes them (20 dp, or 16 dp in rows and fields) and tints them
 * with the surrounding content color. [PinFilled] is the only filled state.
 */
object MarkyIcons {
    /** Search entry, search screen. */
    val Search: ImageVector by lazy {
        icon("Search") {
            stroke(circle(11f, 11f, 6.5f))
            stroke("M20 20l-4.2-4.2")
        }
    }

    /** New note: bottom bar, desktop sidebar. */
    val Plus: ImageVector by lazy { icon("Plus") { stroke("M12 5v14M5 12h14") } }

    /** Import .md: notes list app bar. */
    val Import: ImageVector by lazy {
        icon("Import") {
            stroke(FILE_OUTLINE)
            stroke(FILE_FOLD)
            stroke("M12 11v6M9.5 14.5 12 17l2.5-2.5")
        }
    }

    /** Export as .md: note menu. */
    val Export: ImageVector by lazy {
        icon("Export") {
            stroke(FILE_OUTLINE)
            stroke(FILE_FOLD)
            stroke("M12 17v-6M9.5 13.5 12 11l2.5 2.5")
        }
    }

    /** Pin: note app bar (off state). */
    val Pin: ImageVector by lazy {
        icon("Pin") {
            stroke(PIN_HEAD)
            stroke("M12 14v6")
        }
    }

    /** Pin, on: note app bar when pinned, and the row's pinned indicator. */
    val PinFilled: ImageVector by lazy {
        icon("PinFilled") {
            stroke(PIN_HEAD, filled = true)
            stroke("M12 14v6")
        }
    }

    /** Unpin: note menu when the note is pinned. */
    val Unpin: ImageVector by lazy {
        icon("Unpin") {
            stroke(PIN_HEAD)
            stroke("M12 14v6M4 4l16 16")
        }
    }

    /** More actions: note app bar. */
    val More: ImageVector by lazy {
        icon("More") {
            dot(12f, 5.5f)
            dot(12f, 12f)
            dot(12f, 18.5f)
        }
    }

    /** Back: note and search app bars. */
    val Back: ImageVector by lazy { icon("Back") { stroke("M15 5l-7 7 7 7") } }

    /** Close: dialogs on desktop, dismiss. */
    val Close: ImageVector by lazy { icon("Close") { stroke("M6 6l12 12M18 6 6 18") } }

    /** Clear: search field. */
    val Clear: ImageVector by lazy {
        icon("Clear") {
            stroke(circle(12f, 12f, 8.5f))
            stroke("M9.5 9.5l5 5M14.5 9.5l-5 5")
        }
    }

    /** Edit: empty preview action. */
    val Edit: ImageVector by lazy {
        icon("Edit") {
            stroke("M4 20h4L19.5 8.5a2.1 2.1 0 0 0-3-3L5 17z")
            stroke("M14.5 7.5l2 2")
        }
    }

    /** Preview: reserved for an icon-only mode switch. */
    val Eye: ImageVector by lazy {
        icon("Eye") {
            stroke("M2.5 12S6 5.5 12 5.5 21.5 12 21.5 12 18 18.5 12 18.5 2.5 12 2.5 12z")
            stroke(circle(12f, 12f, 2.8f))
        }
    }

    /** Delete: note menu. */
    val Trash: ImageVector by lazy { icon("Trash") { stroke("M4 7h16M10 11v6M14 11v6M9 7V4.5h6V7M6 7l1 13h10l1-13") } }

    /** Formatting toolbar: heading. */
    val Heading: ImageVector by lazy { icon("Heading") { stroke("M6 5v14M17 5v14M6 12h11") } }

    /** Formatting toolbar: bold. */
    val Bold: ImageVector by lazy { icon("Bold") { stroke("M7 5h6a3.5 3.5 0 0 1 0 7H7zM7 12h7a3.5 3.5 0 0 1 0 7H7z") } }

    /** Formatting toolbar: italic. */
    val Italic: ImageVector by lazy { icon("Italic") { stroke("M10 5h8M6 19h8M14.5 5l-5 14") } }

    /** Formatting toolbar: bulleted list. */
    val List: ImageVector by lazy {
        icon("List") {
            stroke("M10 6h10M10 12h10M10 18h10")
            dot(5f, 6f)
            dot(5f, 12f)
            dot(5f, 18f)
        }
    }

    /** Formatting toolbar: numbered list. */
    val OrderedList: ImageVector by lazy {
        icon("OrderedList") {
            stroke("M11 6h9M11 12h9M11 18h9")
            stroke("M4.5 4.5h1.2V9M4 9h3M4 14.6c.4-.7 2.8-.9 2.8.6 0 1.2-2.8 2-2.8 3.3h3")
        }
    }

    /** Formatting toolbar: checklist. */
    val Task: ImageVector by lazy {
        icon("Task") {
            stroke(roundedRect(4f, 4f, 16f, 16f, 3f))
            stroke("M8.5 12.2l2.4 2.4 4.6-5")
        }
    }

    /** Formatting toolbar: quote. */
    val Quote: ImageVector by lazy { icon("Quote") { stroke("M5 5v14M9.5 8h9.5M9.5 12h9.5M9.5 16h6") } }

    /** Formatting toolbar: code block. */
    val Code: ImageVector by lazy { icon("Code") { stroke("M8.5 7 3.5 12l5 5M15.5 7l5 5-5 5") } }

    /** Formatting toolbar: link. */
    val Link: ImageVector by lazy {
        icon("Link") {
            stroke("M10 14a4 4 0 0 0 5.7 0l3-3a4 4 0 0 0-5.7-5.7l-1 1")
            stroke("M14 10a4 4 0 0 0-5.7 0l-3 3a4 4 0 0 0 5.7 5.7l1-1")
        }
    }

    /** Check: the code block's copied state. */
    val Check: ImageVector by lazy { icon("Check") { stroke(CHECK) } }

    /** Copy: code block. */
    val Copy: ImageVector by lazy {
        icon("Copy") {
            stroke(roundedRect(8f, 8f, 12f, 12f, 2f))
            stroke("M16 8V6a2 2 0 0 0-2-2H6a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h2")
        }
    }

    /** Opens in browser: link affordance on desktop hover. */
    val External: ImageVector by lazy { icon("External") { stroke("M14 4h6v6M20 4l-9 9M18 14v4a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4") } }

    /** Split view: desktop, wide windows. */
    val Split: ImageVector by lazy {
        icon("Split") {
            stroke(roundedRect(3.5f, 4.5f, 17f, 15f, 2f))
            stroke("M12 4.5v15")
        }
    }

    /** Error: snackbar for a failed import. */
    val Alert: ImageVector by lazy {
        icon("Alert") {
            stroke(circle(12f, 12f, 8.5f))
            stroke("M12 7.5v5.5")
            dot(12f, 16.3f)
        }
    }

    /** Note: file and note references. */
    val Note: ImageVector by lazy {
        icon("Note") {
            stroke(FILE_OUTLINE)
            stroke("M14 3v5h5M8.5 13h7M8.5 17h5")
        }
    }

    /** Every public icon, in the order of the design system's iconography grid. */
    val All: List<ImageVector>
        get() = listOf(
            Search, Plus, Import, Export, Pin, PinFilled, Unpin, More, Back, Close, Clear, Edit, Eye, Trash,
            Heading, Bold, Italic, List, OrderedList, Task, Quote, Code, Link, Check, Copy, External, Split, Alert, Note,
        )
}

/** The checkbox tick: the same check path drawn at stroke 2.75 (marky.js `M.task`). */
internal val CheckHeavy: ImageVector by lazy { icon("CheckHeavy", strokeWidth = 2.75f) { stroke(CHECK) } }

private const val FILE_OUTLINE = "M14 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8z"
private const val FILE_FOLD = "M14 3v5h5"
private const val PIN_HEAD = "M9 4h6l-1 5 3 3v2H7v-2l3-3z"
private const val CHECK = "M5.5 12.5l4 4 9-9.5"
private const val DOT_RADIUS = 1.4f

private class IconScope(private val builder: ImageVector.Builder, private val strokeWidth: Float) {
    fun stroke(pathData: String, filled: Boolean = false) {
        builder.addPath(
            pathData = addPathNodes(pathData),
            fill = if (filled) SolidColor(Color.Black) else null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = strokeWidth,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
    }

    /** `<circle r="1.4" fill="currentColor" stroke="none">`. */
    fun dot(cx: Float, cy: Float) {
        builder.addPath(pathData = addPathNodes(circle(cx, cy, DOT_RADIUS)), fill = SolidColor(Color.Black))
    }
}

private inline fun icon(name: String, strokeWidth: Float = 1.75f, block: IconScope.() -> Unit): ImageVector {
    val builder = ImageVector.Builder(
        name = "MarkyIcons.$name",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    )
    IconScope(builder, strokeWidth).block()
    return builder.build()
}

/** SVG `<circle>` as path data: two half arcs. */
internal fun circle(cx: Float, cy: Float, r: Float): String =
    "M${cx - r} ${cy}a$r $r 0 1 0 ${r * 2} 0a$r $r 0 1 0 ${-r * 2} 0z"

/** SVG `<rect rx>` as path data. */
internal fun roundedRect(x: Float, y: Float, width: Float, height: Float, rx: Float): String =
    "M${x + rx} ${y}h${width - rx * 2}a$rx $rx 0 0 1 $rx ${rx}v${height - rx * 2}a$rx $rx 0 0 1 ${-rx} ${rx}" +
        "h${-(width - rx * 2)}a$rx $rx 0 0 1 ${-rx} ${-rx}v${-(height - rx * 2)}a$rx $rx 0 0 1 $rx ${-rx}z"

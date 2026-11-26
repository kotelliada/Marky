package io.kotelliada.marky.core.designsystem.icons

import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.VectorPath
import androidx.compose.ui.graphics.vector.addPathNodes
import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import kotlin.test.Test

class MarkyIconsTest {

    // Break vectors:
    // - a path string the parser rejects only throws when the lazy icon is first read
    // - an icon accidentally aliasing another (copy-paste), or missing from All
    // - circle/rect conversion: arcs that don't return to the start leave a gap or a spur;
    //   the start point being off by the radius shifts the shape
    // - the filled pin losing its fill, or the plain pin gaining one
    // - the heavy check sharing the regular stroke width

    private fun paths(icon: androidx.compose.ui.graphics.vector.ImageVector): List<VectorPath> =
        (0 until icon.root.size).map { icon.root[it] as VectorPath }

    @Test
    fun `every icon builds on a 24 grid with at least one path`() {
        MarkyIcons.All.forEach { icon ->
            assertThat(icon.viewportWidth, name = icon.name).isEqualTo(24f)
            assertThat(icon.viewportHeight, name = icon.name).isEqualTo(24f)
            assertThat(paths(icon), name = icon.name).isNotEmpty()
            paths(icon).forEach { assertThat(it.pathData, name = icon.name).isNotEmpty() }
        }
    }

    @Test
    fun `every icon is distinct`() {
        val names = MarkyIcons.All.map { it.name }
        assertThat(names.toSet().size).isEqualTo(names.size)
        assertThat(MarkyIcons.All.size).isEqualTo(29)
    }

    @Test
    fun `only the filled pin fills its head`() {
        assertThat(paths(MarkyIcons.PinFilled).first().fill).isNotNull()
        assertThat(paths(MarkyIcons.Pin).first().fill).isNull()
    }

    @Test
    fun `checkbox tick is heavier than the regular check`() {
        assertThat(paths(CheckHeavy).single().strokeLineWidth).isEqualTo(2.75f)
        assertThat(paths(MarkyIcons.Check).single().strokeLineWidth).isEqualTo(1.75f)
    }

    @Test
    fun `circle starts at its left edge and closes`() {
        val nodes = addPathNodes(circle(11f, 11f, 6.5f))
        val move = nodes.first() as PathNode.MoveTo
        assertThat(move.x).isCloseTo(4.5f, 0.0001f)
        assertThat(move.y).isCloseTo(11f, 0.0001f)
        assertThat(endsWhereItStarted(nodes)).isTrue()
    }

    @Test
    fun `rounded rect stays inside its box and closes`() {
        val nodes = addPathNodes(roundedRect(3.5f, 4.5f, 17f, 15f, 2f))
        val move = nodes.first() as PathNode.MoveTo
        assertThat(move.x).isCloseTo(5.5f, 0.0001f)
        assertThat(move.y).isCloseTo(4.5f, 0.0001f)
        assertThat(endsWhereItStarted(nodes)).isTrue()
    }

    /** Sums the relative moves; a closed outline returns to (0, 0). */
    private fun endsWhereItStarted(nodes: List<PathNode>): Boolean {
        var dx = 0f
        var dy = 0f
        nodes.forEach { node ->
            when (node) {
                is PathNode.RelativeArcTo -> { dx += node.arcStartDx; dy += node.arcStartDy }
                is PathNode.RelativeHorizontalTo -> dx += node.dx
                is PathNode.RelativeVerticalTo -> dy += node.dy
                else -> Unit
            }
        }
        return kotlin.math.abs(dx) < 0.0001f && kotlin.math.abs(dy) < 0.0001f
    }
}

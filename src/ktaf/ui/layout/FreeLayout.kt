package ktaf.ui.layout

import ktaf.core.KTAFMutableValue
import ktaf.core.vec2
import ktaf.ui.RelativeSize
import ktaf.ui.node.UINode
import kotlin.math.max

class FreeLayout: UILayout() {
    val alignment = KTAFMutableValue(vec2(0f))

    // TODO: this process needs better documenting
    //// width allocated to child is based on left|right lines OR the child's width if one or both lines are missing
    //// content width computed is the rightmost of the children's right lines
    ////  children without a right line have a virtual line generated at (left + width) where left defaults to 0 if
    ////  there is no line
    override fun computeChildrenWidth(widthAllocatedForContent: Float): Lazy<Float> {
        fun eval(l: LayoutLineValue) = l.fixed + widthAllocatedForContent * l.ratio

        children.forEach { child ->
            val left = vLines[nodeLefts[child]]
            val right = vLines[nodeRights[child]]
            val width = left ?.let { right ?.let { eval(right) - eval(left) + 1 } } ?: child.width.get() ?: widthAllocatedForContent

            println("width: $width")

            child.layout.get().computeWidthFor(child, width - child.margin.get().width)
        }

        return lazy { children.map {
            vLines[nodeRights[it]] ?.let(::eval) ?: (vLines[nodeLefts[it]] ?.let(::eval) ?: 0f) + it.computedWidthInternal - 1
        } .fold(0f, ::max) }
    }

    // TODO: this process needs better documenting
    override fun computeChildrenHeight(width: Float, heightAllocatedForContent: Float?): Lazy<Float> {
        fun eval(l: LayoutLineValue) = l.fixed + (heightAllocatedForContent ?: 0f) * l.ratio

        children.forEach { child ->
            val top = hLines[nodeTops[child]]
            val bottom = hLines[nodeBottoms[child]]
            val height = top ?.let { bottom ?.let { eval(bottom) - eval(top) + 1 } } ?: child.height.get() ?: heightAllocatedForContent

            println("height: $height")

            child.layout.get().computeHeightFor(child, height?.let { it - child.margin.get().height })
        }

        return lazy { children.map {
            hLines[nodeBottoms[it]] ?.let(::eval) ?: (hLines[nodeTops[it]] ?.let(::eval) ?: 0f) + it.computedHeightInternal - 1
        } .fold(0f, ::max) }
    }

    // TODO: this process needs better documenting
    override fun position(width: Float, height: Float) {
        fun evalw(l: LayoutLineValue?) = l ?.let { l.fixed + width * l.ratio }
        fun evalh(l: LayoutLineValue?) = l ?.let { l.fixed + height * l.ratio }

        children.forEach { it.layout.get().computePositionForChildren(it) }

        children.forEach { child ->
            val top = evalh(hLines[nodeTops[child]]) ?: 0f
            val left = evalw(vLines[nodeLefts[child]]) ?: 0f
            val bottom = evalh(hLines[nodeBottoms[child]]) ?: top + child.computedHeightInternal + child.margin.get().height
            val right = evalw(vLines[nodeRights[child]]) ?: left + child.computedWidthInternal + child.margin.get().width

            align(child, vec2(left, top), vec2(right - left, bottom - top), alignment.get())
        }
    }

    internal val vLines: LayoutLineMapping = mutableMapOf()
    internal val hLines: LayoutLineMapping = mutableMapOf()
    internal val nodeTops: LayoutNodeMapping = mutableMapOf()
    internal val nodeBottoms: LayoutNodeMapping = mutableMapOf()
    internal val nodeLefts: LayoutNodeMapping = mutableMapOf()
    internal val nodeRights: LayoutNodeMapping = mutableMapOf()

    init {
        hline("top") { percentage = 0f }
        hline("bottom") { percentage = 100f }
        vline("left") { percentage = 0f }
        vline("right") { percentage = 100f }
    }
}

typealias LayoutLine = String
internal typealias LayoutLineValue = RelativeSize
internal typealias LayoutLineMapping = MutableMap<LayoutLine, LayoutLineValue>
internal typealias LayoutNodeMapping = MutableMap<UINode, LayoutLine>

class LineEditor internal constructor(private val lines: LayoutLineMapping, private val line: String) {
    var offset
        get() = lines.computeIfAbsent(line) { ktaf.ui.fixed(0f) } .fixed
        set(value) { lines[line] = RelativeSize(value, lines[line]?.ratio ?: 0f) }

    var percentage
        get() = lines.computeIfAbsent(line) { ktaf.ui.fixed(0f) } .ratio
        set(value) { lines[line] = RelativeSize(lines[line]?.fixed ?: 0f, value / 100f) }
}

class NodeEditor internal constructor(private val node: UINode, private val layout: FreeLayout) {
    var top
        get() = layout.nodeTops[node]
        set(value) { value ?.let { layout.nodeTops[node] = value } }

    var bottom
        get() = layout.nodeBottoms[node]
        set(value) { value ?.let { layout.nodeBottoms[node] = value } }

    var left
        get() = layout.nodeLefts[node]
        set(value) { value ?.let { layout.nodeLefts[node] = value } }

    var right
        get() = layout.nodeRights[node]
        set(value) { value ?.let { layout.nodeRights[node] = value } }

    var topOffset
        get() = layout.hLines[layout.nodeTops[node]] ?.fixed ?: 0f
        set(value) { top = genLine(layout.hLines, RelativeSize(value, topPercentage))
        }

    var bottomOffset
        get() = layout.hLines[layout.nodeBottoms[node]] ?.fixed ?: 0f
        set(value) { bottom = genLine(layout.hLines, RelativeSize(value, bottomPercentage))
        }

    var leftOffset
        get() = layout.vLines[layout.nodeLefts[node]] ?.fixed ?: 0f
        set(value) { left = genLine(layout.vLines, RelativeSize(value, leftPercentage))
        }

    var rightOffset
        get() = layout.vLines[layout.nodeRights[node]] ?.fixed ?: 0f
        set(value) { right = genLine(layout.vLines, RelativeSize(value, rightPercentage))
        }

    var topPercentage
        get() = layout.hLines[layout.nodeTops[node]] ?.fixed ?: 0f
        set(value) { top = genLine(layout.hLines, RelativeSize(topOffset, value / 100f))
        }

    var bottomPercentage
        get() = layout.hLines[layout.nodeBottoms[node]] ?.ratio ?: 0f
        set(value) { bottom = genLine(layout.hLines, RelativeSize(bottomOffset, value / 100f))
        }

    var leftPercentage
        get() = layout.vLines[layout.nodeLefts[node]] ?.ratio ?: 0f
        set(value) { left = genLine(layout.vLines, RelativeSize(leftOffset, value / 100f))
        }

    var rightPercentage
        get() = layout.vLines[layout.nodeRights[node]] ?.ratio ?: 0f
        set(value) { right = genLine(layout.vLines, RelativeSize(rightOffset, value / 100f))
        }
}

fun FreeLayout.hline(line: String, fn: LineEditor.() -> Unit) = fn(LineEditor(hLines, line))
fun FreeLayout.vline(line: String, fn: LineEditor.() -> Unit) = fn(LineEditor(vLines, line))
fun FreeLayout.hline(line: String, value: RelativeSize) { hLines[line] = value }
fun FreeLayout.vline(line: String, value: RelativeSize) { vLines[line] = value }
fun FreeLayout.line(line: String, fn: LineEditor.() -> Unit) { fn(LineEditor(hLines, line)); fn(LineEditor(vLines, line)) }
fun FreeLayout.line(line: String, value: RelativeSize) { hLines[line] = value; vLines[line] = value }

fun FreeLayout.elem(node: UINode, fn: NodeEditor.() -> Unit) = fn(NodeEditor(node, this))

private fun genLine(lines: LayoutLineMapping, value: LayoutLineValue): String {
    val i = generateSequence(0) { it + 1 } .first { !lines.contains("__bounds$it") }
    lines["__bounds$i"] = value
    return "__bounds$i"
}

private fun dispatch(default: Float, a: Float?, b: Float?, c: Float?, abf: (Float, Float) -> Float, bcf: (Float, Float) -> Float, acf: (Float, Float) -> Float): Float {
    if (a != null && b != null) return abf(a, b)
    if (a != null && c != null) return acf(a, c)
    if (b != null && c != null) return bcf(b, c)
    return default
}

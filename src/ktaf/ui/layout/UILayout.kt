package ktaf.ui.layout
import ktaf.KTAFMutableValue
import ktaf.core.vec2
import ktaf.ui.*

sealed class UILayout

class FillLayout: UILayout() {
    var alignment = KTAFMutableValue(vec2(0f))
}

class GridLayout: UILayout() {
    var alignment = KTAFMutableValue(vec2(0.5f))
    var spacing = KTAFMutableValue(vec2(0f))
    var columns = KTAFMutableValue(1)
    var rows = KTAFMutableValue(1)
}

class FreeLayout: UILayout() {
    var alignment = KTAFMutableValue(vec2(0f))
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

class ListLayout: UILayout() {
    var alignment = KTAFMutableValue(0.5f)
    var spacing = KTAFMutableValue(Spacing.SPACE_AFTER)
    // TODO: add spacing between elements
}

class FlowLayout: UILayout() {
    var horizontalSpacing = KTAFMutableValue(Spacing.SPACE_AFTER)
    var verticalSpacing = KTAFMutableValue(Spacing.SPACE_AFTER)
    // TODO: add vertical alignment within row
    // TODO: add spacing between elements
}

typealias LayoutLine = String
internal typealias LayoutLineValue = RelativeSize
internal typealias LayoutLineMapping = MutableMap<LayoutLine, LayoutLineValue>
internal typealias LayoutNodeMapping = MutableMap<UINode, LayoutLine>

class LineEditor internal constructor(private val lines: LayoutLineMapping, private val line: String) {
    var offset
        get() = lines.computeIfAbsent(line) { fixed(0f) } .fixed
        set(value) { lines[line] = RelativeSize(value, lines[line]?.ratio ?: 0f) }

    var percentage
        get() = lines.computeIfAbsent(line) { fixed(0f) } .ratio
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

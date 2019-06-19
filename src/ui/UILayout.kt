package ui
import core.vec2

sealed class UILayout: UI_t

class FillLayout: UILayout() {
    var alignment by property(vec2(0f))
}

class GridLayout: UILayout() {
    var alignment by property(vec2(0.5f))
    var spacing by property(vec2(0f))
    var horizontal by property(1)
    var vertical by property(1)
}

class FreeLayout: UILayout() {
    var alignment by property(vec2(0f))
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
    var alignment by property(0.5f)
    var spacing by property(Spacing.SPACE_AFTER)
    // TODO: add spacing between elements
}

class FlowLayout: UILayout() {
    var horizontalSpacing by property(Spacing.SPACE_AFTER)
    var verticalSpacing by property(Spacing.SPACE_AFTER)
    // TODO: add vertical alignment within row
    // TODO: add spacing between elements
}

typealias LayoutLine = String
internal typealias LayoutLineValue = vec2
internal typealias LayoutLineMapping = MutableMap<LayoutLine, LayoutLineValue>
internal typealias LayoutNodeMapping = MutableMap<UINode, LayoutLine>

class LineEditor internal constructor(private val lines: LayoutLineMapping, private val line: String) {
    var offset
        get() = lines.computeIfAbsent(line) { vec2(0f) } .x
        set(value) { lines[line] = vec2(value, lines[line]?.y ?: 0f)
        }

    var percentage
        get() = lines.computeIfAbsent(line) { vec2(0f) } .y
        set(value) { lines[line] = vec2(lines[line]?.x ?: 0f, value)
        }
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
        get() = layout.hLines[layout.nodeTops[node]] ?.x ?: 0f
        set(value) { top = genLine(layout.hLines, vec2(value, topPercentage)) }

    var bottomOffset
        get() = layout.hLines[layout.nodeBottoms[node]] ?.x ?: 0f
        set(value) { bottom = genLine(layout.hLines, vec2(value, bottomPercentage)) }

    var leftOffset
        get() = layout.vLines[layout.nodeLefts[node]] ?.x ?: 0f
        set(value) { left = genLine(layout.vLines, vec2(value, leftPercentage)) }

    var rightOffset
        get() = layout.vLines[layout.nodeRights[node]] ?.x ?: 0f
        set(value) { right = genLine(layout.vLines, vec2(value, rightPercentage)) }

    var topPercentage
        get() = layout.hLines[layout.nodeTops[node]] ?.y ?: 0f
        set(value) { top = genLine(layout.hLines, vec2(topOffset, value)) }

    var bottomPercentage
        get() = layout.hLines[layout.nodeBottoms[node]] ?.y ?: 0f
        set(value) { bottom = genLine(layout.hLines, vec2(bottomOffset, value)) }

    var leftPercentage
        get() = layout.vLines[layout.nodeLefts[node]] ?.y ?: 0f
        set(value) { left = genLine(layout.vLines, vec2(leftOffset, value)) }

    var rightPercentage
        get() = layout.vLines[layout.nodeRights[node]] ?.y ?: 0f
        set(value) { right = genLine(layout.vLines, vec2(rightOffset, value)) }
}

fun FreeLayout.hline(line: String, fn: LineEditor.() -> Unit) = fn(LineEditor(hLines, line))
fun FreeLayout.vline(line: String, fn: LineEditor.() -> Unit) = fn(LineEditor(vLines, line))
fun FreeLayout.hline(line: String, value: vec2) { hLines[line] = value }
fun FreeLayout.vline(line: String, value: vec2) { vLines[line] = value }
fun FreeLayout.line(line: String, fn: LineEditor.() -> Unit) { fn(LineEditor(hLines, line)); fn(LineEditor(vLines, line)) }
fun FreeLayout.line(line: String, value: vec2) { hLines[line] = value; vLines[line] = value }

fun FreeLayout.elem(node: UINode, fn: NodeEditor.() -> Unit) = fn(NodeEditor(node, this))

private fun genLine(lines: LayoutLineMapping, value: LayoutLineValue): String {
    val i = generateSequence(0) { it + 1 } .first { !lines.contains("__bounds$it") }
    lines["__bounds$i"] = value
    return "__bounds$i"
}

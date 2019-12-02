package ktaf.gui.core

import geometry.vec2
import ktaf.graphics.DrawContext2D
import lwjglkt.glfw.CursorPosition
import lwjglkt.glfw.KeyEvent
import kotlin.math.max

/** A node containing a protected list of children. */
abstract class UIParent: UINode(), Positioner {
    protected open val children: List<UINode> get() = internalChildren

    protected open fun <T: UINode> addChild(index: Int, child: T): T {
        child.parent?.removeChild(child)
        internalChildren.add(index, child)
        child.positioned = false
        child.parent = this
        if (hasDrawContext()) child.setDrawContext(drawContext)
        return child
    }

    protected open fun <T: UINode> addChild(child: T): T {
        child.parent?.removeChild(child)
        internalChildren.add(child)
        child.positioned = false
        child.parent = this
        if (hasDrawContext()) child.setDrawContext(drawContext)
        return child
    }

    protected open fun <T: UINode> removeChild(child: T): T {
        if (internalChildren.remove(child)) {
            child.parent = null
        }
        return child
    }

    protected open fun clearChildren(): Unit
            = internalChildren.map { it }.forEach { removeChild(it) }

    ////////////////////////////////////////////////////////////////////////////

    override fun getMouseHandler(position: CursorPosition): UINode? = children
            .reversed().stream()
            .map { it.getMouseHandler(position) }
            .filter { it != null }
            .findFirst().orElse(null)

    override fun getKeyHandler(event: KeyEvent): UINode? = children
            .reversed().stream()
            .map { it.getKeyHandler(event) }
            .filter { it != null }
            .findFirst().orElse(null)

    override fun getInputHandler(): UINode? = children
            .reversed().stream()
            .map { it.getInputHandler() }
            .filter { it != null }
            .findFirst().orElse(null)

    override fun calculateWidth(availableWidth: Float) {
        calculateChildrenWidths(availableWidth)
        super.calculateWidth(availableWidth)
    }

    override fun calculateHeight(availableHeight: Float?) {
        calculateChildrenHeights(availableHeight)
        super.calculateHeight(availableHeight)
    }

    override fun position(position: vec2) {
        super.position(position)
        positionChildren()
    }

    override fun setDrawContext(context: DrawContext2D) {
        super.setDrawContext(context)
        children.forEach { it.setDrawContext(context) }
    }

    override fun draw() {
        internalChildren.forEach { it.draw() }
    }

    override fun update(dt: Float) {
        super.update(dt)
        internalChildren.forEach { it.update(dt) }
    }

    ////////////////////////////////////////////////////////////////////////////

    private val internalChildren: MutableList<UINode> = mutableListOf()

    ////////////////////////////////////////////////////////////////////////////

    init {
        expand.value = true
    }

    ////////////////////////////////////////////////////////////////////////////

    protected val childrenWidthTotal
        get() = children.map { it.calculatedSize.x } .sum()

    protected val childrenHeightTotal
        get() = children.map { it.calculatedSize.y } .sum()

    protected val childrenWidthMaximum
        get() = children.map { it.calculatedSize.x } .fold(0f, ::max)

    protected val childrenHeightMaximum
        get() = children.map { it.calculatedSize.y } .fold(0f, ::max)
}

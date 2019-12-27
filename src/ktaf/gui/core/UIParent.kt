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
        if (child.parented) return child // TODO: return null or something?
        if (hasDrawContext()) child.setDrawContext(drawContext)

        internalChildren.add(index, child)
        child.parented = true
        child.positioned = false

        return child
    }

    protected open fun <T: UINode> addChild(child: T): T {
        if (child.parented) return child // TODO: return null or something?
        if (hasDrawContext()) child.setDrawContext(drawContext)

        internalChildren.add(child)
        child.parented = true
        child.positioned = false

        return child
    }

    protected open fun <T: UINode> removeChild(child: T): T {
        if (internalChildren.remove(child)) {
            val exit = child.exit
            val p = exit.position?.invoke(child.calculatedPosition, child.calculatedSize)
            val s = exit.size?.invoke(child.calculatedPosition, child.calculatedSize)

            if (p != null && p != child.calculatedPosition || s != null && s != child.calculatedSize) {
                child.exitTo(p ?: child.calculatedPosition, s ?: child.calculatedSize)
                animatedChildrenExiting.add(child)
            }
            else child.parented = false
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
        animatedChildrenExiting.forEach { it.draw() }
    }

    override fun update(dt: Float) {
        super.update(dt)
        internalChildren.forEach { it.update(dt) }

        animatedChildrenExiting.forEach { it.update(dt) }
        animatedChildrenExiting.forEach { if (!it.animating) it.parented = false }
        animatedChildrenExiting.removeAll { !it.animating }
    }

    ////////////////////////////////////////////////////////////////////////////

    private val animatedChildrenExiting: MutableList<UINode> = mutableListOf()
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

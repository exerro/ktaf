package ktaf.gui.core

import geometry.vec2
import ktaf.graphics.DrawContext2D
import kotlin.math.max

/** A node containing a protected list of children. */
abstract class UIParent<Child: UINode>: UINode(), Positioner {
    protected open val children: List<Child> get() = internalChildren

    protected open fun <T: Child> addChild(index: Int, child: T)
            = internalChildren.add(index, child).also { child.positioned = false } .let { child }

    protected open fun <T: Child> addChild(child: T)
            = internalChildren.add(child).also { child.positioned = false } .let { child }

    protected open fun <T: Child> removeChild(child: T)
            = internalChildren.remove(child) .let { child }

    protected open fun clearChildren()
            = internalChildren.clear()

    ////////////////////////////////////////////////////////////////////////////

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

    override fun draw(context: DrawContext2D) {
        internalChildren.forEach { it.draw(context) }
    }

    override fun update(dt: Float) {
        super.update(dt)
        internalChildren.forEach { it.update(dt) }
    }

    ////////////////////////////////////////////////////////////////////////////

    private val internalChildren: MutableList<Child> = mutableListOf()

    ////////////////////////////////////////////////////////////////////////////

    init {
        expand()
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

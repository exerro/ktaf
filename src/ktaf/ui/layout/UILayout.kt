package ktaf.ui.layout

import ktaf.core.times
import ktaf.core.vec2
import ktaf.typeclass.minus
import ktaf.typeclass.plus
import ktaf.ui.node.UIContainer
import ktaf.ui.node.UINode
import ktaf.ui.node.orderedChildren
import kotlin.math.max

abstract class UILayout {
    protected lateinit var children: List<UINode>
    /** Computes the width of children */
    abstract fun computeChildrenWidths(widthAllocatedForContent: Float?)
    /** Computes the height of children */
    abstract fun computeChildrenHeights(width: Float, heightAllocatedForContent: Float?)
    /** Returns the content width of children */
    abstract fun computeChildrenWidth(): Float
    /** Returns the content height of children */
    abstract fun computeChildrenHeight(): Float
    /** Positions the children */
    abstract fun position(width: Float, height: Float)
    /** Called prior to positioning */
    open fun begin(children: List<UINode>) { this.children = children }
    /** Called after positioning */
    open fun finish() {}

    companion object
}

/** Begins positioning of a node */
fun UILayout.beginPositioning(node: UIContainer) {
    begin(node.orderedChildren())
    node.children.map { when (it) { is UIContainer -> it.layout.get().beginPositioning(it) } }
}

/** Completes positioning of a node */
fun UILayout.finishPositioning(node: UIContainer) {
    finish()
    node.children.map { when (it) { is UIContainer -> it.layout.get().finishPositioning(it) } }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Utility functions                                                                                                  //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

fun UILayout.Companion.alignw(node: UINode, offset: vec2, width: Float, alignment: Float) {
    val x = (width - node.margin.get().width - node.computedWidth) * alignment + offset.x
    node.computedX = node.margin.get().left + x
    node.computedY = node.margin.get().top + offset.y
}

fun UILayout.Companion.alignh(node: UINode, offset: vec2, height: Float, alignment: Float) {
    val y = (height - node.margin.get().height - node.computedHeight) * alignment + offset.y
    node.computedX = node.margin.get().left + offset.x
    node.computedY = node.margin.get().top + y
}

fun UILayout.Companion.align(node: UINode, offset: vec2, area: vec2, alignment: vec2) {
    val (x, y) = (area - node.margin.get().size - vec2(node.computedWidth, node.computedHeight)) * alignment + offset
    node.computedX = node.margin.get().left + x
    node.computedY = node.margin.get().top + y
}

fun UILayout.Companion.fillChildrenWidths(children: List<UINode>, widthAllocatedForChildren: Float?) {
    children.forEach { it.computeWidth(widthAllocatedForChildren ?.let { w -> w - it.margin.get().width }) }
}

fun UILayout.Companion.fillChildrenHeights(children: List<UINode>, heightAllocatedForChildren: Float?) {
    children.forEach { it.computeHeight(heightAllocatedForChildren ?.let { h -> h - it.margin.get().height }) }
}

fun UILayout.Companion.setChildrenWidths(children: List<UINode>, widthAllocatedForChildren: Float?) {
    children.forEach { it.computeWidth(widthAllocatedForChildren ?.let { w -> w - it.margin.get().width }) }
}

fun UILayout.Companion.setChildrenHeights(children: List<UINode>, heightAllocatedForChildren: Float?) {
    children.forEach { it.computeHeight(heightAllocatedForChildren ?.let { h -> h - it.margin.get().width }) }
}

fun UILayout.Companion.maximumChildWidth(children: List<UINode>)
        = children.map { it.computedWidth + it.margin.get().width } .fold(0f, ::max)

fun UILayout.Companion.maximumChildHeight(children: List<UINode>)
        = children.map { it.computedHeight + it.margin.get().height } .fold(0f, ::max)

fun UILayout.Companion.sumChildrenWidth(children: List<UINode>)
        = children.map { it.computedWidth + it.margin.get().width } .sum()

fun UILayout.Companion.sumChildrenHeight(children: List<UINode>)
        = children.map { it.computedHeight + it.margin.get().height } .sum()

fun <T> UILayout.Companion.positionChildren(children: List<UINode>, start: T, fn: (T, UINode) -> T)
        = children.fold(start, fn)

fun UILayout.Companion.positionChildrenChildren(children: List<UINode>)
        = children.forEach { when (it) { is UIContainer -> it.computePositionForChildren() } }

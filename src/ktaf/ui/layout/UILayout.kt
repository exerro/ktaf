package ktaf.ui.layout

import ktaf.core.times
import ktaf.core.vec2
import ktaf.typeclass.minus
import ktaf.typeclass.plus
import ktaf.ui.node.UINode
import ktaf.ui.node.orderedChildren
import kotlin.math.max

abstract class UILayout {
    protected lateinit var children: List<UINode>
    /** Sets the width of children and returns the content width of those children */
    abstract fun computeChildrenWidth(widthAllocatedForContent: Float): Lazy<Float>
    /** Sets the height of children and returns the content height of those children */
    abstract fun computeChildrenHeight(width: Float, heightAllocatedForContent: Float?): Lazy<Float>
    /** Positions the children */
    abstract fun position(width: Float, height: Float)
    /** Called prior to positioning */
    open fun begin(children: List<UINode>) { this.children = children }
    /** Called after positioning */
    open fun finish() {}

    companion object
}

/** Begins positioning of a node */
fun UILayout.beginPositioning(node: UINode) {
    begin(node.orderedChildren())
    node.children.map { it.layout.get().beginPositioning(it) }
}

/** Completes positioning of a node */
fun UILayout.finishPositioning(node: UINode) {
    finish()
    node.children.map { it.layout.get().finishPositioning(it) }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Utility functions                                                                                                  //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

fun UILayout.Companion.alignw(node: UINode, offset: vec2, width: Float, alignment: Float) {
    val x = (width - node.margin.get().width - node.computedWidthInternal) * alignment + offset.x
    node.computedXInternal = node.margin.get().left + x
    node.computedYInternal = node.margin.get().top + offset.y
}

fun UILayout.Companion.alignh(node: UINode, offset: vec2, height: Float, alignment: Float) {
    val y = (height - node.margin.get().height - node.computedHeightInternal) * alignment + offset.y
    node.computedXInternal = node.margin.get().left + offset.x
    node.computedYInternal = node.margin.get().top + y
}

fun UILayout.Companion.align(node: UINode, offset: vec2, area: vec2, alignment: vec2) {
    val (x, y) = (area - node.margin.get().size - vec2(node.computedWidthInternal, node.computedHeightInternal)) * alignment + offset
    node.computedXInternal = node.margin.get().left + x
    node.computedYInternal = node.margin.get().top + y
}

fun UILayout.Companion.fillChildrenWidths(children: List<UINode>, widthAllocatedForChildren: Float) {
    children.forEach { it.computeInternalWidth(widthAllocatedForChildren - it.margin.get().width) }
}

fun UILayout.Companion.fillChildrenHeights(children: List<UINode>, heightAllocatedForChildren: Float?) {
    children.forEach { it.computeHeight(heightAllocatedForChildren ?.let { h -> h - it.margin.get().height }) }
}

fun UILayout.Companion.setChildrenWidths(children: List<UINode>, widthAllocatedForChildren: Float) {
    children.forEach { it.computeInternalWidth(widthAllocatedForChildren - it.margin.get().width) }
}

fun UILayout.Companion.setChildrenHeights(children: List<UINode>, heightAllocatedForChildren: Float?) {
    children.forEach { it.computeHeight(heightAllocatedForChildren?.let { h -> h - it.margin.get().width }) }
}

fun UILayout.Companion.maximumChildWidth(children: List<UINode>)
        = children.map { it.computedWidthInternal + it.margin.get().width } .fold(0f, ::max)

fun UILayout.Companion.maximumChildHeight(children: List<UINode>)
        = children.map { it.computedHeightInternal + it.margin.get().height } .fold(0f, ::max)

fun UILayout.Companion.sumChildrenWidth(children: List<UINode>)
        = children.map { it.computedWidthInternal + it.margin.get().width } .sum()

fun UILayout.Companion.sumChildrenHeight(children: List<UINode>)
        = children.map { it.computedHeightInternal + it.margin.get().height } .sum()

fun <T> UILayout.Companion.positionChildren(children: List<UINode>, start: T, fn: (T, UINode) -> T)
        = children.fold(start, fn)

fun UILayout.Companion.positionChildrenChildren(children: List<UINode>)
        = children.forEach { it.computePositionForChildren() }

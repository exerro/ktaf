package ktaf.ui.layout

import ktaf.core.KTAFMutableValue
import ktaf.core.vec2
import kotlin.math.max

class FillLayout: UILayout() {
    var alignment = KTAFMutableValue(vec2(0f))

    override fun computeChildrenWidth(widthAllocatedForChildren: Float): Lazy<Float> {
        // compute the width for each child where allocated width fills the area
        children.forEach { it.layout.get().computeWidthFor(it, widthAllocatedForChildren - it.margin.get().width) }
        // return the largest of the children's widths as the content width
        return lazy { children.map { it.computedWidthInternal + it.margin.get().width } .fold(0f, ::max) }
    }

    override fun computeChildrenHeight(heightAllocatedForChildren: Float?): Lazy<Float> {
        // compute the height for each child where allocated height fills the area
        children.forEach { it.layout.get().computeHeightFor(it, heightAllocatedForChildren ?.let { h -> h - it.margin.get().height }) }
        // return the largest of the children's heights as the content height
        return lazy { children .map { it.computedHeightInternal + it.margin.get().height } .fold(0f, ::max) }
    }

    override fun position(width: Float, height: Float) {
        // position each child in the area with an alignment
        children.forEach { align(it, it.margin.get().tl, vec2(width, height), alignment.get()) }
        children.forEach { it.layout.get().computePositionForChildren(it) }
    }
}

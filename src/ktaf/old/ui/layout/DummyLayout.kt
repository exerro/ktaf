package ktaf.ui.layout

import kotlin.math.max

open class DummyLayout: UILayout() {
    override fun computeChildrenWidths(widthAllocatedForContent: Float?) {}
    override fun computeChildrenHeights(width: Float, heightAllocatedForContent: Float?) {}
    override fun position(width: Float, height: Float) {}

    override fun computeChildrenWidth(): Float
            = children.map { it.currentComputedX.get() + it.currentComputedWidth.get() + it.margin.get().width } .fold(0f, ::max)

    override fun computeChildrenHeight(): Float
            = children.map { it.currentComputedY.get() + it.currentComputedHeight.get() + it.margin.get().height } .fold(0f, ::max)
}

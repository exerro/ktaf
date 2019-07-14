package ktaf.ui.layout

import kotlin.math.max

open class DummyLayout: UILayout() {
    override fun computeChildrenWidths(widthAllocatedForContent: Float?) {}
    override fun computeChildrenHeights(width: Float, heightAllocatedForContent: Float?) {}
    override fun position(width: Float, height: Float) {}

    override fun computeChildrenWidth(): Float
            = children.map { it.computedX.get() + it.computedWidth.get() + it.margin.get().width } .fold(0f, ::max)

    override fun computeChildrenHeight(): Float
            = children.map { it.computedY.get() + it.computedHeight.get() + it.margin.get().height } .fold(0f, ::max)
}

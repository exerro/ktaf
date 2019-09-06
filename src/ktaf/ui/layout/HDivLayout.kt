package ktaf.ui.layout

import geometry.vec2
import ktaf.core.KTAFList
import ktaf.core.KTAFValue

class HDivLayout(spacing: Float, vararg sections: LayoutValue): UILayout() {
    constructor(vararg sections: LayoutValue): this(0f, *sections)

    val sections = KTAFList(sections.map { KTAFValue(it) } .toMutableList())
    val alignment = KTAFValue(vec2(0.5f))
    val spacing = KTAFValue(spacing)

    // compute the width for each child where allocated width fills the area divided evenly amongst children
    override fun computeChildrenWidths(widthAllocatedForContent: Float?) {
        val widthAllocatedForChildren = widthAllocatedForContent ?.let { w -> w - (children.size - 1) * spacing.get() }
        val sectionValues = listOf(0f) + sections.map { widthAllocatedForChildren ?.let { w -> it.get().apply(w) } }
        val lastSectionDelta = widthAllocatedForChildren ?.let { w -> sectionValues.last() ?.let { l -> w - l } }
        val sectionValuesToAppend = (1 .. (children.size - sections.size)).map { i ->
            lastSectionDelta ?.let { sectionValues.last()!! + it * i / (children.size - sections.size) }
        }
        val sectionWidths = (sectionValues + sectionValuesToAppend).let { it.zip(it.drop(1)).map { (a, b) -> b ?.let { a ?.let { b - a } } } }

        children.zip(sectionWidths).forEach { (child, widthAllocatedForChild) ->
            child.computeWidth(widthAllocatedForChild ?.let { w -> w - child.margin.get().width })
        }
    }

    // compute the height for each child where allocated height fills the area
    override fun computeChildrenHeights(width: Float, heightAllocatedForContent: Float?) {
        UILayout.setChildrenHeights(children, heightAllocatedForContent)
    }

    // return the sum of the widths for each child plus appropriate spacing
    override fun computeChildrenWidth() = UILayout.sumChildrenWidth(children) + spacing.get() * (children.size - 1)

    // return the sum of the largest heights of each row plus spacing
    override fun computeChildrenHeight() = UILayout.maximumChildHeight(children)

    // TODO: this process needs better documenting
    override fun position(width: Float, height: Float) {
        UILayout.positionChildrenChildren(children)

        val widthAllocatedForChildren = width - (children.size - 1) * spacing.get()
        val sectionValues = listOf(0f) + sections.map { it.get().apply(widthAllocatedForChildren) }
        val lastSectionDelta = widthAllocatedForChildren - sectionValues.last()
        val sectionValuesToAppend = (1 .. (children.size - sections.size)).map { i ->
            sectionValues.last() + lastSectionDelta * i / (children.size - sections.size)
        }
        val sectionPositions = sectionValues + sectionValuesToAppend
        val sectionWidths = (sectionValues + sectionValuesToAppend).let { it.zip(it.drop(1)).map { (a, b) -> b - a } }

        UILayout.positionChildren(children, 0) { index, child ->
            align(child, vec2(sectionPositions[index] + spacing.get() * index, 0f), vec2(sectionWidths[index], height), alignment.get())
            index + 1
        }
    }
}

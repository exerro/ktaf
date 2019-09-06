package ktaf.ui.layout

import geometry.vec2
import ktaf.core.KTAFList
import ktaf.core.KTAFValue

class VDivLayout(spacing: Float, vararg sections: LayoutValue): UILayout() {
    constructor(vararg sections: LayoutValue): this(0f, *sections)

    val sections = KTAFList(sections.map { KTAFValue(it) } .toMutableList())
    val alignment = KTAFValue(vec2(0.5f))
    val spacing = KTAFValue(spacing)

    // compute the width for each child where allocated width fills the area divided evenly amongst children
    override fun computeChildrenWidths(widthAllocatedForContent: Float?) {
        UILayout.setChildrenWidths(children, widthAllocatedForContent)
    }

    // compute the height for each child where allocated height fills the area
    override fun computeChildrenHeights(width: Float, heightAllocatedForContent: Float?) {
        val heightAllocatedForChildren = heightAllocatedForContent ?.let { h -> h - (children.size - 1) * spacing.get() }
        val sectionValues = listOf(0f) + sections.map { heightAllocatedForChildren ?.let { h -> it.get().apply(h) } }
        val lastSectionDelta = heightAllocatedForChildren ?.let { h -> sectionValues.last() ?.let { l -> h - l } }
        val sectionValuesToAppend = (1 .. (children.size - sections.size)).map { i ->
            lastSectionDelta ?.let { sectionValues.last()!! + it * i / (children.size - sections.size) }
        }
        val sectionHeights = (sectionValues + sectionValuesToAppend).let { it.zip(it.drop(1)).map { (a, b) -> b ?.let { a ?.let { b - a } } } }

        children.zip(sectionHeights).forEach { (child, heightAllocatedForChild) ->
            child.computeHeight(heightAllocatedForChild ?.let { h -> h - child.margin.get().height })
        }
    }

    // return the sum of the widths for each child plus appropriate spacing
    override fun computeChildrenWidth() = UILayout.maximumChildWidth(children)

    // return the sum of the largest heights of each row plus spacing
    override fun computeChildrenHeight() = UILayout.sumChildrenHeight(children) + spacing.get() * (children.size - 1)

    // TODO: this process needs better documenting
    override fun position(width: Float, height: Float) {
        UILayout.positionChildrenChildren(children)

        val heightAllocatedForChildren = height - (children.size - 1) * spacing.get()
        val sectionValues = listOf(0f) + sections.map { it.get().apply(heightAllocatedForChildren) }
        val lastSectionDelta = heightAllocatedForChildren - sectionValues.last()
        val sectionValuesToAppend = (1 .. (children.size - sections.size)).map { i ->
            sectionValues.last() + lastSectionDelta * i / (children.size - sections.size)
        }
        val sectionPositions = sectionValues + sectionValuesToAppend
        val sectionHeights = (sectionValues + sectionValuesToAppend).let { it.zip(it.drop(1)).map { (a, b) -> b - a } }

        UILayout.positionChildren(children, 0) { index, child ->
            align(child, vec2(0f, sectionPositions[index] + spacing.get() * index), vec2(width, sectionHeights[index]), alignment.get())
            index + 1
        }
    }
}

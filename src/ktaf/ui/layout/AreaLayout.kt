package ktaf.ui.layout

import ktaf.core.KTAFMutableValue
import ktaf.core.vec2
import ktaf.typeclass.minus
import ktaf.typeclass.plus
import ktaf.ui.node.UINode

// TODO: this needs better documenting
class AreaLayout: UILayout() {
    val alignment = KTAFMutableValue(vec2(0.5f))

    fun areas(fn: Area.() -> Any?) { fn(area) }
    fun elem(node: UINode, area: String) { elements[node] = area }

    // TODO: this process needs better documenting
    override fun computeChildrenWidth(widthAllocatedForContent: Float): Lazy<Float> {
        val widths = area.widths("", 0f, widthAllocatedForContent)
        children.map { elements[it] ?.let { label -> widths[label] } ?.let { w -> it.layout.get().computeWidthFor(it, w) } }
        return lazy { widthAllocatedForContent }
    }

    // TODO: this process needs better documenting
    override fun computeChildrenHeight(width: Float, heightAllocatedForContent: Float?): Lazy<Float> {
        val heights = area.heights("", 0f, heightAllocatedForContent ?: 0f)
        children.map { elements[it] ?.let { label -> heights[label] } ?.let { h -> it.layout.get().computeHeightFor(it, h) } }
        return lazy { heightAllocatedForContent ?: 0f }
    }

    // TODO: this process needs better documenting
    override fun position(width: Float, height: Float) {
        val positions = area.build("", vec2(0f, 0f), vec2(width, height))
        children.forEach { it.layout.get().computePositionForChildren(it) }
        children.map { elements[it] ?.let { label -> positions[label] } ?.let { (pos, size) ->
            UILayout.align(it, pos, size, alignment.get())
        } }
    }

    fun Float.px() = Value(this, 0f)
    fun Float.pc() = Value(0f, this)
    fun Int.px() = toFloat().px()
    fun Int.pc() = toFloat().pc()

    private val area = Area()
    private val elements: MutableMap<UINode, String> = mutableMapOf()
}

// TODO: this needs better documenting
class Area {
    val topPercentage = KTAFMutableValue(0f)
    val topPixels = KTAFMutableValue(0f)
    val rightPercentage = KTAFMutableValue(100f)
    val rightPixels = KTAFMutableValue(0f)
    val bottomPercentage = KTAFMutableValue(100f)
    val bottomPixels = KTAFMutableValue(0f)
    val leftPercentage = KTAFMutableValue(0f)
    val leftPixels = KTAFMutableValue(0f)

    fun top(value: Value) {
        topPercentage(value.percentage)
        topPixels(value.pixels)
    }

    fun right(value: Value) {
        rightPercentage(value.percentage)
        rightPixels(value.pixels)
    }

    fun bottom(value: Value) {
        bottomPercentage(value.percentage)
        bottomPixels(value.pixels)
    }

    fun left(value: Value) {
        leftPercentage(value.percentage)
        leftPixels(value.pixels)
    }

    fun area(fn: Area.() -> Any?) {
        val area = Area()
        fn(area)
        subAreas.add(area)
    }

    fun area(label: String, fn: Area.() -> Any?) {
        labelLookup[label] ?.let {
            fn(subAreas[it])
        } ?: run {
            val area = Area()
            fn(area)
            subAreas.add(area)
            labels[subAreas.indexOf(area)] = label
            labelLookup[label] = subAreas.indexOf(area)
        }
    }

    fun label(source: String, target: String) {
        labelLookup[target] ?.let {
            labelLookup[source] = it
            labels[it] = source
        }
    }

    fun split(label: String, vararg values: Value) {
        val lefts = listOf(Value(0f, 0f)) + values
        val rights = values.toList() + listOf(Value(0f, 100f))

        lefts.zip(rights).mapIndexed { i, (left, right) -> area("$label[$i]") { left(left); right(right) } }
    }

    fun vsplit(label: String, vararg values: Value) {
        val tops = listOf(Value(0f, 0f)) + values
        val bottoms = values.toList() + listOf(Value(0f, 100f))

        tops.zip(bottoms).mapIndexed { i, (top, bottom) -> area("$label[$i]") { top(top); bottom(bottom) } }
    }

    fun split(vararg values: Value, fn: Labeller.() -> Any?) {
        val lefts = listOf(Value(0f, 0f)) + values
        val rights = values.toList() + listOf(Value(0f, 100f))
        val baseIndex = subAreas.size

        lefts.zip(rights).map { (left, right) -> area { left(left); right(right) } }
        fn(Labeller(baseIndex, labels, labelLookup))
    }

    fun vsplit(vararg values: Value, fn: Labeller.() -> Any?) {
        val tops = listOf(Value(0f, 0f)) + values
        val bottoms = values.toList() + listOf(Value(0f, 100f))
        val baseIndex = subAreas.size

        tops.zip(bottoms).map { (top, bottom) -> area { top(top); bottom(bottom) } }
        fn(Labeller(baseIndex, labels, labelLookup))
    }

    fun widths(label: String, x: Float, width: Float): Map<String, Float> {
        val left = leftPixels.get() + leftPercentage.get() / 100 * width
        val right = rightPixels.get() + rightPercentage.get() / 100 * width

        return (subAreas.mapIndexed { i, area ->
            area.widths(labels[i]!!, x + left, right - left).toList()
        } .flatten() + (label to right - left)) .toMap()
    }

    fun heights(label: String, y: Float, height: Float): Map<String, Float> {
        val top = topPixels.get() + topPercentage.get() / 100 * height
        val bottom = bottomPixels.get() + bottomPercentage.get() / 100 * height

        return (subAreas.mapIndexed { i, area ->
            area.heights(labels[i]!!, y + top, bottom - top).toList()
        } .flatten() + (label to bottom - top)) .toMap()
    }

    fun build(label: String, position: vec2, size: vec2): Map<String, Pair<vec2, vec2>> {
        val top = topPixels.get() + topPercentage.get() / 100 * size.y
        val bottom = bottomPixels.get() + bottomPercentage.get() / 100 * size.y
        val left = leftPixels.get() + leftPercentage.get() / 100 * size.x
        val right = rightPixels.get() + rightPercentage.get() / 100 * size.x

        return (subAreas.mapIndexed { i, area ->
            area.build(labels[i]!!, position + vec2(left, top), vec2(right - left, bottom - top)).toList()
        } .flatten() + (label to Pair(position + vec2(left, top), vec2(right - left, bottom - top)))) .toMap()
    }

    private val subAreas: MutableList<Area> = mutableListOf()
    private val labels: MutableMap<Int, String> = mutableMapOf()
    private val labelLookup: MutableMap<String, Int> = mutableMapOf()
}

data class Value internal constructor(val pixels: Float, val percentage: Float) {
    operator fun plus(other: Value) = Value(pixels + other.pixels, percentage + other.percentage)
    operator fun minus(other: Value) = Value(pixels - other.pixels, percentage - other.percentage)
}

class Labeller internal constructor(private var baseIndex: Int, private val labels: MutableMap<Int, String>, private val labelLookup: MutableMap<String, Int>) {
    fun labels(vararg labels: String) {
        labels.mapIndexed { i, v ->
            this.labels[i + baseIndex] = v
            this.labelLookup[v] = i + baseIndex
        }
        baseIndex += labels.size
    }
}

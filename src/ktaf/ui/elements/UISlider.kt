@file:JvmName("UISliderKt")

package ktaf.ui.elements

import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.typeclass.minus
import ktaf.ui.DEFAULT_STATE
import ktaf.ui.UIAnimatedProperty
import ktaf.ui.UIProperty
import ktaf.ui.elements.UISlider.Companion.PRESSED
import ktaf.ui.layout.DummyLayout
import ktaf.ui.layout.height
import ktaf.ui.layout.width
import ktaf.ui.node.*
import ktaf.util.Animation
import lwjglkt.glfw.GLFWCursor
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

open class UISlider(min: Float = 0f, max: Float = 1f): UIContainer() {
    constructor(steps: Int): this() { xSteps(steps) }
    constructor(min: Float, max: Float, steps: Int): this(min, max) { xSteps(steps) }

    private val slider = children.add(SliderObject()) {}

    val direction = KTAFValue(UISliderDirection.HORIZONTAL)
    val backgroundColour = UIAnimatedProperty(rgba(0f), this, "backgroundColour")

    val x = KTAFValue(0f)
    val y = KTAFValue(0f)
    val value = KTAFValue(vec2(0f))

    val xSteps = KTAFValue<Int?>(null)
    val xMin = KTAFValue(min)
    val xMax = KTAFValue(max)

    val ySteps = KTAFValue<Int?>(null)
    val yMin = KTAFValue(0f)
    val yMax = KTAFValue(1f)

    val sliderWidth = UIProperty(0f)
    val sliderHeight = UIProperty(0f)
    val sliderColour = slider.colour

    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        fillBackground(context, position, size, backgroundColour.get())
        super.draw(context, position, size)
    }

    private val xRatio = KTAFValue(0f)
    private val yRatio = KTAFValue(0f)

    private fun setX() { x(xMin.get() + xRatio.get() * (xMax.get() - xMin.get())) }
    private fun setY() { y(yMin.get() + yRatio.get() * (yMax.get() - yMin.get())) }
    private fun positionSliderX() { slider.currentComputedX.setValue(xRatio.get() * (currentComputedWidth.get() - padding.get().width - slider.currentComputedWidth.get())) }
    private fun positionSliderY() { slider.currentComputedY.setValue(yRatio.get() * (currentComputedHeight.get() - padding.get().height - slider.currentComputedHeight.get())) }

    init {
        propertyState(backgroundColour)
        propertyState(sliderWidth)
        propertyState(sliderHeight)

        // ratio <-> value
        x.connect { xRatio(divisions((it - xMin.get()) / (xMax.get() - xMin.get()), xSteps.get())); setX() }
        y.connect { yRatio(divisions((it - yMin.get()) / (yMax.get() - yMin.get()), ySteps.get())); setY() }
        xRatio.connect { x(xMin.get() + it * (xMax.get() - xMin.get())) }
        yRatio.connect { y(yMin.get() + it * (yMax.get() - yMin.get())) }

        // ensure ratio is in fact 0->1
        xRatio.connect { xRatio(max(0f, min(1f, it))) }
        yRatio.connect { yRatio(max(0f, min(1f, it))) }

        // value <-> (x, y)
        x.connect { value(vec2(x.get(), y.get())) }
        y.connect { value(vec2(x.get(), y.get())) }
        value.connect { x(it.x); y(it.y) }

        // update ratio on step count or range change
        xSteps.connect { xRatio(xRatio.get()) }
        ySteps.connect { yRatio(yRatio.get()) }

        // update value on range change
        xMin.connect { setX() }
        xMax.connect { setX() }
        yMin.connect { setY() }
        yMax.connect { setY() }

        // update slider position on ratio change
        xRatio.connect { positionSliderX() }
        yRatio.connect { positionSliderY() }

        slider.onMouseDrag.connect { event ->
            val dp = event.position - event.firstPosition

            if (direction.get() != UISliderDirection.VERTICAL && dp.x != 0f) {
                val size = currentComputedWidth.get() - padding.get().width - slider.currentComputedWidth.get()
                val pos = slider.currentComputedX.get() + dp.x
                val ratio = max(0f, min(1f, pos / size))
                xRatio(divisions(ratio, xSteps.get()))
            }

            if (direction.get() != UISliderDirection.HORIZONTAL && dp.y != 0f) {
                val size = currentComputedHeight.get() - padding.get().height - slider.currentComputedHeight.get()
                val pos = slider.currentComputedY.get() + dp.y
                val ratio = max(0f, min(1f, pos / size))
                yRatio(divisions(ratio, ySteps.get()))
            }
        }

        layout(SliderLayout(direction, sliderWidth, sliderHeight))
        direction(UISliderDirection.HORIZONTAL)
        x(0f)
        y(0f)
        sliderWidth(32f)
        sliderHeight(32f)
        sliderColour(Colour.blue)
        backgroundColour(rgba(0.8f))
    }

    companion object {
        const val PRESSED = "pressed"
    }
}

private class SliderObject: UIPane() {
    override fun cursor() = GLFWCursor.POINTER

    init {
        onMousePress { state.push(PRESSED) }
        onMouseRelease { state.remove(PRESSED) }

        colour.animationDuration = Animation.QUICK
        colour.setSetter {
            this[DEFAULT_STATE](it)
            this[HOVER](it.lighten())
            this[PRESSED](it.darken())
        }
    }
}

enum class UISliderDirection {
    VERTICAL,
    HORIZONTAL,
    BOTH
}

private class SliderLayout(
        val direction: KTAFValue<UISliderDirection>,
        val sliderWidth: KTAFValue<Float>,
        val sliderHeight: KTAFValue<Float>
): DummyLayout() {
    override fun computeChildrenWidths(widthAllocatedForContent: Float?) {
        children.forEach { it.currentComputedWidth(
                (widthAllocatedForContent.takeIf { direction.get() == UISliderDirection.VERTICAL } ?: sliderWidth.get())
        ) }
    }

    override fun computeChildrenHeights(width: Float, heightAllocatedForContent: Float?) {
        children.forEach { it.currentComputedHeight(
                (heightAllocatedForContent.takeIf { direction.get() == UISliderDirection.HORIZONTAL } ?: sliderHeight.get())
        ) }
    }
}

private fun divisions(ratio: Float, divisions: Int?): Float {
    if (divisions == null) return ratio
    return floor(ratio * (divisions - 1) + 0.5f) / (divisions - 1)
}

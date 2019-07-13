package ktaf.ui.elements

import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.typeclass.minus
import ktaf.ui.graphics.ColourBackground
import ktaf.ui.layout.height
import ktaf.ui.layout.tl
import ktaf.ui.layout.width
import ktaf.ui.node.*
import lwjglkt.GLFWCursor
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class UISlider: UINode() {
    val direction = KTAFValue(UISliderDirection.HORIZONTAL)
    val x = KTAFValue(0f)
    val y = KTAFValue(0f)
    val xSteps = KTAFValue<Int?>(null)
    val ySteps = KTAFValue<Int?>(null)
    val sliderWidth = KTAFValue(0f)
    val sliderHeight = KTAFValue(0f)
    val sliderColour = KTAFValue(rgba(0f))
    val backgroundColour = KTAFValue(rgba(0f))
    val value = KTAFValue(vec2(0f))

    override fun update(dt: Float) { slider.update(dt) }
    override fun getMouseHandler(position: vec2): UINode? = slider.getMouseHandler(position - padding.get().tl - slider.computedPosition.get())
    override fun getInputHandler(): UINode? = slider.getInputHandler()
    override fun getKeyboardHandler(key: GLFWKey, modifiers: Set<GLFWKeyModifier>): UINode? =
            slider.getKeyboardHandler(key, modifiers)

    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        super.draw(context, position, size)
        drawChildren(listOf(slider), context, position)
    }

    override fun computeWidth(widthAllocated: Float) {
        super.computeWidth(widthAllocated)
        slider.computeWidth(widthAllocated)
    }

    override fun computeHeight(heightAllocated: Float?) {
        super.computeHeight(heightAllocated)
        slider.computeHeight(heightAllocated)
    }

    private val slider = SliderObject()
    private var background = addBackground(ColourBackground())
    private var sliderBackground = slider.addBackground(ColourBackground())

    init {
        x.connect { slider.computedX.setValue(it * (computedWidth.get() - padding.get().width - slider.computedWidth.get())) }
        y.connect { slider.computedY.setValue(it * (computedHeight.get() - padding.get().height - slider.computedHeight.get())) }
        xSteps.connect { s -> x(divisions(x.get(), s ?.let { it - 1 })) }
        ySteps.connect { s -> y(divisions(y.get(), s ?.let { it - 1 })) }
        sliderWidth.connect { slider.width(it.takeIf { direction.get() != UISliderDirection.VERTICAL }) }
        sliderHeight.connect { slider.height(it.takeIf { direction.get() != UISliderDirection.HORIZONTAL }) }
        sliderColour.connect { sliderBackground = slider.replaceBackground(sliderBackground, sliderBackground.copy(colour = it)) }
        backgroundColour.connect { background = replaceBackground(background, background.copy(colour = it)) }

        scene.joinTo(slider.scene)

        x.connect { x(divisions(it, xSteps.get() ?.let { s -> s - 1 })) }
        y.connect { y(divisions(it, ySteps.get() ?.let { s -> s - 1 })) }
        x.connect { value(vec2(x.get(), y.get())) }
        y.connect { value(vec2(x.get(), y.get())) }
        value.connect { x(it.x); y(it.y) }

        direction.connect {
            slider.width(sliderWidth.get().takeIf { direction.get() != UISliderDirection.VERTICAL })
            slider.height(sliderHeight.get().takeIf { direction.get() != UISliderDirection.HORIZONTAL })
        }

        slider.onMouseDrag.connect { event ->
            val dp = event.position - event.firstPosition

            if (direction.get() != UISliderDirection.VERTICAL) {
                val size = computedWidth.get() - padding.get().width - slider.computedWidth.get()
                val pos = slider.computedX.get() + dp.x
                val ratio = max(0f, min(1f, pos / size))
                x(divisions(ratio, xSteps.get() ?.let { it - 1 }))
            }

            if (direction.get() != UISliderDirection.HORIZONTAL) {
                val size = computedHeight.get() - padding.get().height - slider.computedHeight.get()
                val pos = slider.computedY.get() + dp.y
                val ratio = max(0f, min(1f, pos / size))
                y(divisions(ratio, ySteps.get() ?.let { it - 1 }))
            }
        }

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

private class SliderObject: UINode() {
    override fun cursor() = GLFWCursor.POINTER

    init {
        onMousePress { state.push(UISlider.PRESSED) }
        onMouseRelease { state.remove(UISlider.PRESSED) }
    }
}

enum class UISliderDirection {
    VERTICAL,
    HORIZONTAL,
    BOTH
}

private fun divisions(ratio: Float, divisions: Int?): Float {
    if (divisions == null) return ratio
    return floor(ratio * divisions + 0.5f) / divisions
}

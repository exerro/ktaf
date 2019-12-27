package ktaf.gui.elements

import geometry.min
import geometry.vec2
import ktaf.data.property.floatAnimatedProperty
import ktaf.data.property.mutableProperty
import ktaf.graphics.Colour
import ktaf.gui.core.GUIBuilderContext
import ktaf.gui.core.UIContainer
import ktaf.gui.core.UINode

fun UIContainer.slider(min: Float, max: Float, value: Float, steps: Int = 100, fn: Slider.() -> Unit = {})
        = addChild(Slider(min, max, value, steps)).also(fn)

fun GUIBuilderContext.slider(min: Float, max: Float, value: Float, steps: Int = 100, fn: Slider.() -> Unit = {})
        = Slider(min, max, value, steps).also(fn)

//////////////////////////////////////////////////////////////////////////////////////////

class Slider(min: Float = 0f, max: Float = 0f, value: Float = 0f, steps: Int = 100): UINode() {
    val min = floatAnimatedProperty(min)
    val max = floatAnimatedProperty(max)
    val value = floatAnimatedProperty(value)
    val steps = mutableProperty(steps)
    val vertical = mutableProperty(false)

    override fun draw() {
        val s = size.min()
        val t = s * 0.4f
        val r = (value.value - min.value) / (max.value - min.value)
        val l = position + vec2(s / 2)
        val forward = if (vertical.value) vec2(0f, size.y - s) else vec2(size.x - s, 0f)

        drawContext.colour.value = Colour.red
        drawContext.line(l, l + forward, t)

        drawContext.colour.value = Colour.green
        for (i in 0 .. steps.value) {
            drawContext.crectangle(l + forward * (i / steps.value.toFloat()), vec2(t * 0.4f))
        }
        drawContext.colour.value = Colour.blue
        drawContext.crectangle(l + forward * r, vec2(s))
    }

    override fun getDefaultWidth(): Float? = null
    override fun getDefaultHeight(width: Float): Float? = null
}

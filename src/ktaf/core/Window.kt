package ktaf.core

import geometry.vec2
import ktaf.graphics.DrawContext
import ktaf.graphics.DrawContext2D
import ktaf.property.Operation
import ktaf.property.Value
import ktaf.property.mutable
import lwjglkt.glfw.GLFWWindow
import lwjglkt.glfw.Size
import observables.Signal
import observables.UnitSignal

class Window(
        val glfwWindow: GLFWWindow
) {
    private val sizeProperty = mutable(glfwWindow.size.toVec2())

    val update = Signal<Float>()
    val draw = UnitSignal()
    val closed = UnitSignal()
    val events = glfwWindow.events
    val size: Value<vec2> = sizeProperty
    val width: Value<Float> = Operation(size) { it.x }
    val height: Value<Float> = Operation(size) { it.y }
    val drawContext2D = DrawContext2D(glfwWindow.glContext, size)

    internal var lastUpdateTime = System.currentTimeMillis()

    init {
        drawContext2D.viewportSize <- size
        events.resized.connect { sizeProperty.value = it.toVec2() }
    }
}

private fun Size.toVec2() = vec2(width.toFloat(), height.toFloat())

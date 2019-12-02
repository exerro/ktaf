package ktaf.graphics

import geometry.vec2
import ktaf.data.Value
import ktaf.data.property.mutableProperty
import lwjglkt.gl.GLContext
import lwjglkt.gl.GLCurrentContext

open class DrawContext(
        protected val glContext: GLContext,
        private val screenSize: Value<vec2>
) {
    val viewportPosition = mutableProperty(vec2(0f))
    val viewportSize = mutableProperty(screenSize.value)
    lateinit var currentContext: GLCurrentContext

    open fun begin() {
        val x = viewportPosition.value.x.toInt()
        val y = (screenSize.value.y - viewportPosition.value.y - viewportSize.value.y).toInt()
        val w = viewportSize.value.x.toInt()
        val h = viewportSize.value.y.toInt()
        currentContext = glContext.waitToMakeCurrent()
        currentContext.gl.viewport(x, y, w, h)
    }

    open fun end() {
        currentContext.free()
    }
}

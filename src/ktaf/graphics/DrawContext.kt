package ktaf.graphics

import geometry.vec2
import ktaf.data.Value
import ktaf.data.property.mutableProperty
import lwjglkt.gl.GLContext

open class DrawContext(
        protected val glContext: GLContext,
        private val screenSize: Value<vec2>
) {
    val viewportPosition = mutableProperty(vec2(0f))
    val viewportSize = mutableProperty(vec2(1f))

    open fun begin() {
        val x = viewportPosition.value.x.toInt()
        val y = (screenSize.value.y - viewportPosition.value.y - viewportSize.value.y).toInt()
        val w = viewportSize.value.x.toInt()
        val h = viewportSize.value.y.toInt()

        glContext.makeCurrent()
        glContext.gl.viewport(x, y, w, h)
    }

    open fun end() {
        glContext.unmakeCurrent()
    }
}

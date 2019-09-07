package ktaf.graphics

import geometry.*
import ktaf.core.RGBA
import ktaf.core.rgba

internal data class DrawCtxState(
        private val transformation: mat4 = mat4_identity,
        private val colour: vec4 = rgba(1f)
) {
    fun translate(offset: vec2) = copy(
            transformation=transformation * mat4_translate(offset.vec3(0f))
    )

    fun rotate(angle: Float) = copy(
            transformation=transformation * mat3_rotate(angle, -vec3_z).mat4()
    )

    fun scale(scale: vec2) = copy(
            transformation=transformation * mat3_scale(scale.vec3(1f)).mat4()
    )

    fun setColour(colour: RGBA) = copy(
            colour=colour
    )

    fun transformation() = transformation
    fun colour() = colour
}

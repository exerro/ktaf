package ktaf.graphics

import geometry.*
import ktaf.core.RGBA
import ktaf.core.rgba


data class DrawCtxState internal constructor(
        private val transformation: mat4 = mat4_identity,
        private val colour: vec4 = rgba(1f),
        private val shader: FragmentShader2D? = null
) {
    internal fun translate(offset: vec2) = copy(
            transformation=transformation * mat4_translate(offset.vec3(0f))
    )

    internal fun rotate(angle: Float) = copy(
            transformation=transformation * mat3_rotate(angle, vec3_z).mat4()
    )

    internal fun scale(scale: vec2) = copy(
            transformation=transformation * mat3_scale(scale.vec3(1f)).mat4()
    )

    internal fun setColour(colour: RGBA) = copy(
            colour=colour
    )

    internal fun setShader(shader: FragmentShader2D) = copy(
            shader=shader
    )

    internal fun transformation() = transformation
    internal fun colour() = colour
    internal fun shader() = shader
}

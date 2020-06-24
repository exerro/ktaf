package ktaf.util

import geometry.*
import lwjglkt.gl.*
import lwjglkt.gl.enum.GLBufferType
import lwjglkt.util.createBuffer

fun GLCurrentContext.vec3vbo(data: List<vec3>)
        = createBuffer(data.flatMap(vec3::unpack).toFloatArray())

fun GLCurrentContext.vec2vbo(data: List<vec2>): GLVBO {
    return createVBO(GLBufferType.GL_ARRAY_BUFFER) {
        data(data.flatMap(vec2::unpack).toFloatArray())
    }
}

val GLTexture2.size: vec2
    get() = vec2(width.toFloat(), height.toFloat())

fun GLShaderProgram.uniform(uniform: String, value: Boolean) = uniform1i(uniformLocation(uniform), if (value) 1 else 0)
fun GLShaderProgram.uniform(uniform: String, value: Int    ) = uniform1i(uniformLocation(uniform), value)
fun GLShaderProgram.uniform(uniform: String, value: Float  ) = uniform1f(uniformLocation(uniform), value)

fun GLShaderProgram.uniform(uniform: String, value: vec2) = uniform2f(uniformLocation(uniform), value.x, value.y)
fun GLShaderProgram.uniform(uniform: String, value: vec3) = uniform3f(uniformLocation(uniform), value.x, value.y, value.z)
fun GLShaderProgram.uniform(uniform: String, value: vec4) = uniform4f(uniformLocation(uniform), value.x, value.y, value.z, value.w)
fun GLShaderProgram.uniform(uniform: String, value: mat3) = uniformMatrix3ft(uniformLocation(uniform), value.elements)
fun GLShaderProgram.uniform(uniform: String, value: mat4) = uniformMatrix4ft(uniformLocation(uniform), value.elements)

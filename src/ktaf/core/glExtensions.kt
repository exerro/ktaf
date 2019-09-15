package ktaf.core

import geometry.*
import lwjglkt.*
import lwjglkt.gl.GLContext
import lwjglkt.gl.bindIn
import lwjglkt.gl.createVBO

const val VERTEX_POSITION_ATTRIBUTE = 0
const val VERTEX_UV_ATTRIBUTE = 1
const val VERTEX_NORMAL_ATTRIBUTE = 2
const val VERTEX_COLOUR_ATTRIBUTE = 3

fun GLContext.elementBuffer(elements: List<Int>): GLVBO {
    return createVBO(GLBufferType.GL_ELEMENT_ARRAY_BUFFER) {
        data(elements.toIntArray())
    }
}

fun GLContext.vec3vbo(data: List<vec3>): GLVBO {
    return createVBO(GLBufferType.GL_ARRAY_BUFFER) {
        data(data.flatMap(vec3::unpack).toFloatArray())
    }
}

fun GLContext.colourBuffer(size: Int): GLVBO {
    return createVBO(GLBufferType.GL_ARRAY_BUFFER) {
        data(FloatArray(size * 3) { 1f })
    }
}

fun GLContext.vec2vbo(data: List<vec2>): GLVBO {
    return createVBO(GLBufferType.GL_ARRAY_BUFFER) {
        data(data.flatMap(vec2::unpack).toFloatArray())
    }
}

fun GLVAO.bindElementBuffer(buffer: GLVBO) = bindIn {
    buffer.bind()
    attach(buffer)
    // buffer.unbind()
}

fun GLVAO.bindPositionBuffer(
        buffer: GLVBO,
        usage: GLBufferUsage = GLBufferUsage.GL_STATIC_DRAW
) = bindIn {
    buffer.bindIn {
        vertexAttributePointer(
                VERTEX_POSITION_ATTRIBUTE, GLNumComponents.THREE,
                GLAttribPointerType.GL_FLOAT, false, 0, 0)
    }
    enableVertexAttributeArray(VERTEX_POSITION_ATTRIBUTE)
    attach(buffer)
}

fun GLVAO.bindNormalBuffer(
        buffer: GLVBO,
        usage: GLBufferUsage = GLBufferUsage.GL_STATIC_DRAW
) = bindIn {
    buffer.bindIn {
        vertexAttributePointer(
                VERTEX_NORMAL_ATTRIBUTE, GLNumComponents.THREE,
                GLAttribPointerType.GL_FLOAT, false, 0, 0)
    }
    enableVertexAttributeArray(VERTEX_NORMAL_ATTRIBUTE)
    attach(buffer)
}

fun GLVAO.bindColourBuffer(
        buffer: GLVBO,
        usage: GLBufferUsage = GLBufferUsage.GL_STATIC_DRAW
) = bindIn {
    buffer.bindIn {
        vertexAttributePointer(
                VERTEX_COLOUR_ATTRIBUTE, GLNumComponents.THREE,
                GLAttribPointerType.GL_FLOAT, false, 0, 0)
    }
    enableVertexAttributeArray(VERTEX_COLOUR_ATTRIBUTE)
    attach(buffer)
}

fun GLVAO.bindUVBuffer(
        buffer: GLVBO,
        usage: GLBufferUsage = GLBufferUsage.GL_STATIC_DRAW
) = bindIn {
    buffer.bindIn {
        vertexAttributePointer(
                VERTEX_UV_ATTRIBUTE, GLNumComponents.TWO,
                GLAttribPointerType.GL_FLOAT, false, 0, 0)
    }
    enableVertexAttributeArray(VERTEX_UV_ATTRIBUTE)
    attach(buffer)
}

val GLTexture2.size: vec2
    get() = vec2(width.toFloat(), height.toFloat())

fun GLShaderProgram.uniform(uniform: String, value: Boolean) { uniform1i(uniformLocation(uniform), if (value) 1 else 0) }
fun GLShaderProgram.uniform(uniform: String, value: Int    ) { uniform1i(uniformLocation(uniform), value) }
fun GLShaderProgram.uniform(uniform: String, value: Float  ) { uniform1f(uniformLocation(uniform), value) }
fun GLShaderProgram.uniform(uniform: String, value: vec2) { uniform2f(uniformLocation(uniform), value.x, value.y) }
fun GLShaderProgram.uniform(uniform: String, value: vec3) { uniform3f(uniformLocation(uniform), value.x, value.y, value.z) }
fun GLShaderProgram.uniform(uniform: String, value: vec4) { uniform4f(uniformLocation(uniform), value.x, value.y, value.z, value.w) }
fun GLShaderProgram.uniform(uniform: String, value: mat3) { uniformMatrix3ft(uniformLocation(uniform), value.elements) }
fun GLShaderProgram.uniform(uniform: String, value: mat4) { uniformMatrix4ft(uniformLocation(uniform), value.elements) }

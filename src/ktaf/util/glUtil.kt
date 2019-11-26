package ktaf.util

import geometry.vec2
import geometry.vec3
import lwjglkt.gl.GLCurrentContext
import lwjglkt.gl.GLVAO
import lwjglkt.gl.createVAO
import lwjglkt.util.*

fun createElementGLVAO(context: GLCurrentContext, elements: List<Int>, vertices: List<vec3>, normals: List<vec3>, uvs: List<vec2>, colours: List<vec3>): GLVAO {
    val elementBuffer = context.createElementBuffer(elements.toIntArray())
    val positionBuffer = context.vec3vbo(vertices)
    val normalBuffer = context.vec3vbo(normals)
    val colourBuffer = context.vec3vbo(colours)
    val uvBuffer = context.vec2vbo(uvs)

    return context.createVAO {
        bindElementBuffer(elementBuffer)
        bindPositionBuffer(positionBuffer)
        bindNormalBuffer(normalBuffer)
        bindColourBuffer(colourBuffer)
        bindUVBuffer(uvBuffer)
    }
}

fun createElementGLVAO(context: GLCurrentContext, elements: List<Int>, vertices: List<vec3>, normals: List<vec3>, uvs: List<vec2>, colours: Boolean = true): GLVAO {
    val elementBuffer = context.createElementBuffer(elements.toIntArray())
    val positionBuffer = context.vec3vbo(vertices)
    val normalBuffer = context.vec3vbo(normals)
    val uvBuffer = context.vec2vbo(uvs)
    val colourBuffer = if (colours) context.createColourBuffer(vertices.size) else null

    return context.createVAO {
        bindElementBuffer(elementBuffer)
        bindPositionBuffer(positionBuffer)
        bindNormalBuffer(normalBuffer)
        bindUVBuffer(uvBuffer)
        colourBuffer?.let { bindColourBuffer(it) }
    }
}

fun createElementGLVAO(context: GLCurrentContext, elements: List<Int>, vertices: List<vec3>, normals: List<vec3>, colours: Boolean = true): GLVAO {
    val elementBuffer = context.createElementBuffer(elements.toIntArray())
    val positionBuffer = context.vec3vbo(vertices)
    val normalBuffer = context.vec3vbo(normals)
    val colourBuffer = if (colours) context.createColourBuffer(vertices.size) else null

    return context.createVAO {
        bindElementBuffer(elementBuffer)
        bindPositionBuffer(positionBuffer)
        bindNormalBuffer(normalBuffer)
        colourBuffer?.let { bindColourBuffer(it) }
    }
}

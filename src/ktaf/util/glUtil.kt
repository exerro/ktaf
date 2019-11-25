package ktaf.util

import geometry.vec2
import geometry.vec3
import ktaf.core_old.elementBuffer
import ktaf.core_old.vec2vbo
import ktaf.core_old.vec3vbo
import lwjglkt.gl.GLContext
import lwjglkt.gl.GLVAO
import lwjglkt.gl.createVAO
import lwjglkt.util.*

fun createElementGLVAO(context: GLContext, elements: List<Int>, vertices: List<vec3>, normals: List<vec3>, uvs: List<vec2>, colours: List<vec3>): GLVAO {
    val elementBuffer = context.elementBuffer(elements)
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

fun createElementGLVAO(context: GLContext, elements: List<Int>, vertices: List<vec3>, normals: List<vec3>, uvs: List<vec2>, colours: Boolean = true): GLVAO {
    val elementBuffer = context.elementBuffer(elements)
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

fun createElementGLVAO(context: GLContext, elements: List<Int>, vertices: List<vec3>, normals: List<vec3>, colours: Boolean = true): GLVAO {
    val elementBuffer = context.elementBuffer(elements)
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

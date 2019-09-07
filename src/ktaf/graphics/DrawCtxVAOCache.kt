package ktaf.graphics

import geometry.unpack
import geometry.vec2
import geometry.vec3
import geometry.vec3_z
import ktaf.core.*
import lwjglkt.*

internal class DrawCtxVAOCache {
    val rectangleVAO by lazy {
        createVAO {
            genElementBuffer(listOf(0, 1, 2, 3, 2, 0))
            genVertexPositionBuffer(listOf(
                    vec3(0f, 1f, 0f),
                    vec3(0f, 0f, 0f),
                    vec3(1f, 0f, 0f),
                    vec3(1f, 1f, 0f)
            ))
            genVertexNormalBuffer(List(4) { vec3(0f, 0f, 1f) })
            genVertexUVBuffer(listOf(
                    vec2(0f, 1f),
                    vec2(0f, 0f),
                    vec2(1f, 0f),
                    vec2(1f, 1f)
            ))
            genVertexColourBuffer(4)
        }
    }

    val quadVAO by lazy {
        createVAO {
            genElementBuffer(listOf(0, 1, 2, 3, 2, 0))
            bindBufferToAttribute(this, quadVAOPositions, VERTEX_POSITION_ATTRIBUTE)
            bindBufferToAttribute(this, quadVAONormals, VERTEX_NORMAL_ATTRIBUTE)
            genVertexUVBuffer(listOf(
                    vec2(0f, 1f),
                    vec2(0f, 0f),
                    vec2(1f, 0f),
                    vec2(1f, 1f)
            ))
            genVertexColourBuffer(4)
        }
    }

    val quadVAOPositions by lazy {
        createVectorVBO(listOf(
                vec3(0f, 0f, 0f),
                vec3(0f, 0f, 0f),
                vec3(0f, 0f, 0f),
                vec3(0f, 0f, 0f)
        ))
    }

    val quadVAONormals by lazy {
        createVectorVBO(List(4) { vec3(0f, 0f, 1f) })
    }

    val triangleVAO by lazy {
        createVAO {
            genElementBuffer(listOf(0, 1, 2))
            bindBufferToAttribute(this, triangleVAOPositions, VERTEX_POSITION_ATTRIBUTE)
            bindBufferToAttribute(this, triangleVAONormals, VERTEX_NORMAL_ATTRIBUTE)
            genVertexUVBuffer(listOf(
                    vec2(0f, 1f),
                    vec2(0f, 0f),
                    vec2(1f, 0f)
            ))
            genVertexColourBuffer(3)
        }
    }

    val triangleVAOPositions by lazy {
        createVectorVBO(listOf(
                vec3(0f, 1f, 0f),
                vec3(0f, 0f, 0f),
                vec3(1f, 0f, 0f)
        ))
    }

    val triangleVAONormals by lazy {
        createVectorVBO(List(3) { vec3(0f, 0f, 1f) })
    }

    fun flatNormals(size: Int, buffer: GLVBO) {
        val normals = List(size) { vec3_z }.flatMap { it.unpack().toList() }
        buffer.subData(0, normals.toFloatArray())
    }

    private fun createVectorVBO(data: List<vec3>) = createVBO(GLBufferType.GL_ARRAY_BUFFER) {
        data(data.flatMap { it.unpack().toList() } .toFloatArray(), GLBufferUsage.GL_STATIC_DRAW)
    }

    private fun bindBufferToAttribute(
            vao: GLVAO,
            vbo: GLVBO,
            attribute: Int
    ): GLVBO = vao.bindIn {
        vbo.bindIn {
            vertexAttributePointer(attribute, GLNumComponents.THREE, GLAttribPointerType.GL_FLOAT, false, 0, 0)
        }
        enableVertexAttributeArray(attribute)
        attach(vbo)
    }
}

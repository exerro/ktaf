package ktaf.graphics

import geometry.*
import ktaf.core.*
import lwjglkt.*
import kotlin.math.PI
import kotlin.math.max

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

    fun circleVAO(numPoints: Int): GLVAO {
        if (circleCache.size >= MAX_CACHE_SIZE) {
            for ((k, _) in circleCache) {
                circleCache.remove(k)
                break
            }
        }

        return circleCache.computeIfAbsent(numPoints) { createVAO {
            val startPoint = listOf(vec3(0f))
            val angles = (0 until numPoints).map { i -> i * PI.toFloat() * 2 / numPoints }
            val points = angles.map { mat3_rotate(it, -vec3_z) * vec3_x }
            genVertexPositionBuffer((startPoint + points))
            genVertexNormalBuffer(List(numPoints + 1) { vec3(0f, 0f, 1f) })
            genVertexColourBuffer(numPoints * 3)
            genElementBuffer((1..numPoints).flatMap { i -> listOf(0, i, i % numPoints + 1) })
        } }
    }

    fun calculateCirclePointCount(radius: Float)
            = max(radius.toInt(), 3)

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

    private val circleCache = LinkedHashMap<Int, GLVAO>()
}

private const val MAX_CACHE_SIZE = 15

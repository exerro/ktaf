package ktaf.graphics

import geometry.*
import ktaf.core.*
import ktaf.util.createElementGLVAO
import lwjglkt.*
import lwjglkt.gl.GLContext
import lwjglkt.gl.bindIn
import lwjglkt.gl.createVAO
import lwjglkt.gl.createVBO
import kotlin.math.PI
import kotlin.math.max

internal class DrawCtxVAOCache(
        val glContext: GLContext
) {
    val DEFAULT_FONT by lazy {
        FNTFont.load(glContext, FNTFont::class.java.getResourceAsStream("/ktaf/font/open-sans/OpenSans-Regular.fnt"))
    }

    val rectangleVAO by lazy {
        createElementGLVAO(
                glContext,
                listOf(0, 1, 2, 3, 2, 0),
                listOf(
                        vec3(0f, 1f, 0f),
                        vec3(0f, 0f, 0f),
                        vec3(1f, 0f, 0f),
                        vec3(1f, 1f, 0f)
                ),
                List(4) { vec3(0f, 0f, 1f) },
                listOf(
                        vec2(0f, 1f),
                        vec2(0f, 0f),
                        vec2(1f, 0f),
                        vec2(1f, 1f)
                ),
                true
        )
    }

    val quadVAO by lazy {
        val elementBuffer = glContext.elementBuffer(listOf(0, 1, 2, 3, 2, 0))
        val uvBuffer = glContext.vec2vbo(listOf(
                vec2(0f, 1f),
                vec2(0f, 0f),
                vec2(1f, 0f),
                vec2(1f, 1f)
        ))
        val colourBuffer = glContext.colourBuffer(4)

        glContext.createVAO {
            bindElementBuffer(elementBuffer)
            bindPositionBuffer(quadVAOPositions)
            bindNormalBuffer(quadVAONormals)
            bindUVBuffer(uvBuffer)
            bindColourBuffer(colourBuffer)
        }
    }

    val quadVAOPositions by lazy {
        glContext.vec3vbo(listOf(
                vec3(0f, 0f, 0f),
                vec3(0f, 0f, 0f),
                vec3(0f, 0f, 0f),
                vec3(0f, 0f, 0f)
        ))
    }

    val quadVAONormals by lazy {
        glContext.vec3vbo(List(4) { vec3(0f, 0f, 1f) })
    }

    val triangleVAO by lazy {
        val elementBuffer = glContext.elementBuffer(listOf(0, 1, 2))
        val uvBuffer = glContext.vec2vbo(listOf(
                vec2(0f, 1f),
                vec2(0f, 0f),
                vec2(1f, 0f)
        ))
        val colourBuffer = glContext.colourBuffer(3)

        glContext.createVAO {
            bindElementBuffer(elementBuffer)
            bindPositionBuffer(triangleVAOPositions)
            bindNormalBuffer(triangleVAONormals)
            bindUVBuffer(uvBuffer)
            bindColourBuffer(colourBuffer)
        }
    }

    val triangleVAOPositions by lazy {
        glContext.vec3vbo(listOf(
                vec3(0f, 1f, 0f),
                vec3(0f, 0f, 0f),
                vec3(1f, 0f, 0f)
        ))
    }

    val triangleVAONormals by lazy {
        glContext.vec3vbo(List(3) { vec3(0f, 0f, 1f) })
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

        return circleCache.computeIfAbsent(numPoints) {
            val startPoint = listOf(vec3(0f))
            val angles = (0 until numPoints).map { i -> i * PI.toFloat() * 2 / numPoints }
            val points = angles.map { mat3_rotate(it, -vec3_z) * vec3_x }

            createElementGLVAO(
                    glContext,
                    (1..numPoints).flatMap { i -> listOf(0, i, i % numPoints + 1) },
                    startPoint + points,
                    List(numPoints + 1) { vec3(0f, 0f, 1f) },
                    true
            )
        }
    }

    fun calculateCirclePointCount(radius: Float)
            = max(radius.toInt(), 3)

    @Deprecated("abc", replaceWith = ReplaceWith("glContext.vec3vbo(data)"))
    private fun createVectorVBO(data: List<vec3>) = glContext.createVBO(GLBufferType.GL_ARRAY_BUFFER) {
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

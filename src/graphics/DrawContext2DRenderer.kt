package graphics

import GLTexture2
import GLVAO
import GLDrawMode
import bindIn
import core.*
import createVAO
import postFragmentShaderState
import rasterState
import stopUsing
import use
import useIn
import util.createElementGLVAO
import kotlin.math.max

class DrawContext2DRenderer(val context: DrawContext2D) { init {
    rasterState {
        defaults()
    }

    postFragmentShaderState {
        defaults()
        blendFunction(GLBLendFunction.GL_SRC_ALPHA, GLBLendFunction.GL_ONE_MINUS_SRC_ALPHA)
    }

    GL.enable(GLOption.GL_BLEND)
} }

fun DrawContext2DRenderer.vao(vao: GLVAO, vertexCount: Int, transform: mat4 = mat4_identity, textured: Boolean = false, mode: GLDrawMode = GLDrawMode.GL_TRIANGLES) {
    context.shader.uniform("transform", context.transform * transform)
    context.shader.uniform("colour", context.colour)
    context.shader.uniform("useTexture", textured)
    vao.bindIn {
        GL.drawElements(mode, vertexCount, 0)
    }
}

fun DrawContext2DRenderer.rectangle(position: vec2, size: vec2) {
    if (context.fill) {
        vao(rectangleVAO, 6,
                mat4_translate(position.vec3(0f)) *
                        mat3_scale(size.vec3(1f)).mat4())
    }
    else {
        vao(rectangleVAO, 6,
                mat4_translate(position.vec3(0f)) *
                        mat3_scale(vec3(size.x, context.lineWidth, 1f)).mat4())

        vao(rectangleVAO, 6,
                mat4_translate(position.vec3(0f)) *
                        mat3_scale(vec3(context.lineWidth, size.y, 1f)).mat4())

        vao(rectangleVAO, 6,
                mat4_translate(vec3(position.x, position.y + size.y - context.lineWidth, 0f)) *
                        mat3_scale(vec3(size.x, context.lineWidth, 1f)).mat4())

        vao(rectangleVAO, 6,
                mat4_translate(vec3(position.x + size.x - context.lineWidth, position.y, 0f)) *
                        mat3_scale(vec3(context.lineWidth, size.y, 1f)).mat4())
    }
}

fun DrawContext2DRenderer.circle(position: vec2, radius: Float) {
    if (context.fill) {
        val points = calculateCirclePointCount(radius)
        vao(circleVAO(points), 3 * (points + 1),
                mat4_translate(position.vec3(0f)) *
                        mat3_scale(radius).mat4())
    }
    else {
        TODO("outline graphics.circle rendering not yet implemented")
    }
}

fun DrawContext2DRenderer.image(image: GLTexture2, position: vec2 = vec2(0f), scale: vec2 = vec2(1f)) {
    image.useIn(0) {
        vao(rectangleVAO, 6,
                mat4_translate(position.vec3(0f)) *
                        mat3_scale(scale.vec3(1f) * vec3(image.width.toFloat(), image.height.toFloat(), 1f)).mat4(),
                true
        )
    }
}

fun DrawContext2DRenderer.write(text: String, font: Font, position: vec2 = vec2(0f)) {
    var x = position.x
    val y = position.y + (font.lineHeight - font.baseline) * font.scale

    (text.zip(text.drop(1)) + listOf(text.last() to null)).forEach { (char, next) ->
        val offset = font.getCharOffset(char)
        font.getTexture(char)?.use(0)
        vao(font.getVAO(char), font.getVAOVertexCount(char),
                mat4_translate(vec3(x + offset.x * font.scale, y + offset.y * font.scale, 0f)) *
                        mat3_scale(vec3(font.scale)).mat4(),
                font.getTexture(char) != null)
        font.getTexture(char)?.stopUsing()
        x += font.getCharAdvance(char) * font.scale
        next ?.let { x += font.getKerning(char, next) * font.scale }
    }
}

private val circleCache = LinkedHashMap<Int, GLVAO>()
private const val MAX_CACHE_SIZE = 15

private fun calculateCirclePointCount(radius: Float)
        = max(radius.toInt(), 3)

private fun circleVAO(numPoints: Int): GLVAO {
    if (circleCache.size >= MAX_CACHE_SIZE) {
        for ((k, _) in circleCache) {
            circleCache.remove(k)
            break
        }
    }

    return circleCache.computeIfAbsent(numPoints) { createVAO {
        genVertexPositionBuffer((listOf(vec3(0f)) + (0 until numPoints).map { i -> mat3_rotate(i / numPoints.toFloat() * Math.PI.toFloat() * 2, vec3(0f, 0f, -1f)) * vec3(1f, 0f, 0f) }))
        genVertexNormalBuffer(List(numPoints + 1) { vec3(0f, 0f, 1f) })
        genVertexColourBuffer(numPoints * 3)
        genElementBuffer((1..numPoints).flatMap { i -> listOf(0, i, i % numPoints + 1) })
    } }
}

private var rectangleVAO: GLVAO = createVAO {
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

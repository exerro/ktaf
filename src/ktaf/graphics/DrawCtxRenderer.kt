package ktaf.graphics

import geometry.*
import ktaf.core.RGBA
import ktaf.core.rgba
import ktaf.core.uniform
import lwjglkt.*

class DrawCtxRenderer internal constructor(
        private val ctx: DrawCtx,
        val shader: GLShaderProgram,
        private val vaoCache: DrawCtxVAOCache
) {
    fun vao(vao: GLVAO, vertexCount: Int, colour: RGBA = rgba(1f), transform: mat4 = mat4_identity, textured: Boolean = false, mode: GLDrawMode = GLDrawMode.GL_TRIANGLES) {
        shader.uniform("transformation", ctx.transformation * transform)
        shader.uniform("colour", ctx.colour * colour)
        shader.uniform("useTexture", textured)

        vao.bindIn {
            GL.drawElements(mode, vertexCount, 0)
        }
    }

    fun rectangle(position: vec2, size: vec2, mode: GLDrawMode = GLDrawMode.GL_TRIANGLES) {
        vao(vaoCache.rectangleVAO, 6,
                transform=mat4_identity *
                        mat4_translate(position.vec3(0f)) *
                        mat3_scale(size.vec3(0f)).mat4(),
                mode=mode)
    }

    fun line(a: vec2, b: vec2, size: Float = 1f) {
        val hs = size / 2
        val ab = (b - a).normalise()
        val ba = -ab
        val a1 = a + ba.rotate45CW()  * hs
        val a2 = a + ba.rotate45CCW() * hs
        val b1 = b + ab.rotate45CW()  * hs
        val b2 = b + ab.rotate45CCW() * hs

        vaoCache.flatNormals(4, vaoCache.quadVAONormals)
        vaoCache.quadVAOPositions.subData(0, floatArrayOf(
                a1.x, a1.y, 0f,
                a2.x, a2.y, 0f,
                b1.x, b1.y, 0f,
                b2.x, b2.y, 0f
        ))

        vao(vaoCache.quadVAO, 6)
    }

    fun circle(centre: vec2, radius: Float, mode: GLDrawMode = GLDrawMode.GL_TRIANGLES) {
        val points = vaoCache.calculateCirclePointCount(radius)
        vao(vaoCache.circleVAO(points), 3 * (points + 1),
                transform=mat4_translate(centre.vec3(0f)) * mat3_scale(radius).mat4())
    }

    fun write(text: String, position: vec2 = vec2(0f), font: Font = Font.DEFAULT_FONT) {
        if (text == "") return

        var x = position.x
        val y = position.y + (font.lineHeight - font.baseline) * font.scale

        (text.zip(text.drop(1)) + listOf(text.last() to null)).forEach { (char, next) ->
            val offset = font.getCharOffset(char)
            val translation = vec3(x + offset.x * font.scale, y + offset.y * font.scale, 0f)
            font.getTexture(char)?.use(0)
            vao(font.getVAO(char), font.getVAOVertexCount(char),
                    transform=mat4_translate(translation) * mat3_scale(vec3(font.scale)).mat4(),
                    textured=font.getTexture(char) != null)
            font.getTexture(char)?.stopUsing()
            x += font.getCharAdvance(char) * font.scale
            next ?.let { x += font.getKerning(char, next) * font.scale }
        }
    }

}

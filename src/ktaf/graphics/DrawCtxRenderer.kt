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
}

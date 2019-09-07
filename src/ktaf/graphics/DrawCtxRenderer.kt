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
}

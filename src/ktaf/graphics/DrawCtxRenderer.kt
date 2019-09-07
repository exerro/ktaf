package ktaf.graphics

import geometry.mat4
import geometry.mat4_identity
import geometry.times
import ktaf.core.RGBA
import ktaf.core.uniform
import lwjglkt.*

class DrawCtxRenderer(
        private val ctx: DrawCtx,
        val shader: GLShaderProgram
) {
    fun vao(vao: GLVAO, vertexCount: Int, colour: RGBA, transform: mat4 = mat4_identity, textured: Boolean = false, mode: GLDrawMode = GLDrawMode.GL_TRIANGLES) {
        shader.uniform("transformation", ctx.transformation * transform)
        shader.uniform("colour", ctx.colour * colour)
        shader.uniform("useTexture", textured)

        vao.bindIn {
            GL.drawElements(mode, vertexCount, 0)
        }
    }
}

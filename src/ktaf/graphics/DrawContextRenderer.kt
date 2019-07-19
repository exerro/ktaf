package ktaf.graphics

import ktaf.core.RGBA
import ktaf.core.mat4
import ktaf.core.mat4_identity
import ktaf.core.uniform
import lwjglkt.*

abstract class DrawContextRenderer<Context>(
        val context: Context,
        val shader: GLShaderProgram
) {
    open fun begin() {}
    open fun finish() {}

    open fun vao(vao: GLVAO, vertexCount: Int, transform: mat4 = mat4_identity, textured: Boolean = false, colour: RGBA, mode: GLDrawMode = GLDrawMode.GL_TRIANGLES) {
        shader.uniform("modelTransform", transform)
        shader.uniform("useTexture", textured)
        shader.uniform("colour", colour)

        vao.bindIn {
            GL.drawElements(mode, vertexCount, 0)
        }
    }
}

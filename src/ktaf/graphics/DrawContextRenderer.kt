package ktaf.graphics

import geometry.*
import ktaf.core.*
import lwjglkt.*

abstract class DrawContextRenderer<Context>(
        val context: Context,
        val shader: GLShaderProgram,
        val getTransformation: () -> mat4
) {
    open fun begin() {}
    open fun finish() {}

    open fun vao(vao: GLVAO, vertexCount: Int, transform: mat4 = mat4_identity, textured: Boolean = false, colour: RGBA, mode: GLDrawMode = GLDrawMode.GL_TRIANGLES) {
        shader.uniform("transform", getTransformation() * transform)
        shader.uniform("useTexture", textured)
        shader.uniform("colour", colour)

        vao.bindIn {
            GL.drawElements(mode, vertexCount, 0)
        }
    }
}

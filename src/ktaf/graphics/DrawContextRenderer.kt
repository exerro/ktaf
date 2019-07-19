package ktaf.graphics

import ktaf.core.mat4
import ktaf.core.mat4_identity
import lwjglkt.*

class DrawContextRenderer<Context>(
        val context: Context,
        val shader: GLShaderProgram
) {
    fun vao(vao: GLVAO, vertexCount: Int, transform: mat4 = mat4_identity, textured: Boolean = false, mode: GLDrawMode = GLDrawMode.GL_TRIANGLES) {
        vao.bindIn {
            GL.drawElements(mode, vertexCount, 0)
        }
    }
}

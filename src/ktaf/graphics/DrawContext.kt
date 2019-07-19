package ktaf.graphics

import ktaf.core.mat4
import lwjglkt.*

abstract class DrawContext<Renderer: DrawContextRenderer<*>>(val target: RenderTarget) {
    protected open val TRANSFORM_UNIFORM = "transform"
    protected abstract fun setState(fn: () -> Unit)
    protected abstract fun getTransformation(): mat4
    protected abstract fun getShader(): GLShaderProgram
    protected abstract fun setConstantUniforms(shader: GLShaderProgram)
    protected abstract fun createRenderer(shader: GLShaderProgram): Renderer

    fun draw(fn: Renderer.() -> Any?) {
        target.draw {
            setState {
                getShader().useIn {
                    setConstantUniforms(this)
                    uniformMatrix4f(uniformLocation(TRANSFORM_UNIFORM), getTransformation().elements)
                    fn(createRenderer(this))
                }
            }
        }
    }
}

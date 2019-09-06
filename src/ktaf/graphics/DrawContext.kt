package ktaf.graphics

import geometry.mat4
import ktaf.core.uniform
import lwjglkt.*

abstract class DrawContext<Renderer: DrawContextRenderer<*>>(val target: RenderTarget) {
    protected abstract fun setRenderState(fn: () -> Unit)
    abstract fun getTransformation(): mat4
    protected abstract fun getShader(): GLShaderProgram
    protected abstract fun setConstantUniforms(shader: GLShaderProgram)
    protected abstract fun createRenderer(shader: GLShaderProgram): Renderer

    open fun draw(fn: Renderer.() -> Any?) {
        target.draw {
            setRenderState {
                getShader().useIn {
                    val renderer = createRenderer(this)

                    setConstantUniforms(this)
                    uniform(TRANSFORM_UNIFORM, getTransformation())
                    renderer.begin()
                    fn(renderer)
                    renderer.finish()
                }
            }
        }
    }

    companion object {
        val TRANSFORM_UNIFORM = "transform"
    }
}

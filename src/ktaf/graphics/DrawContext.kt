package ktaf.graphics

import ktaf.core.mat4
import ktaf.core.uniform
import lwjglkt.*

abstract class DrawContext<Renderer: DrawContextRenderer<*>>(val target: RenderTarget) {
    protected open val TRANSFORM_UNIFORM = "transform"
    protected abstract fun setRenderState(fn: () -> Unit)
    protected abstract fun getTransformation(): mat4
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
}

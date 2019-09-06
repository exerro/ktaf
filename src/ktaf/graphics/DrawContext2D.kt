package ktaf.graphics

import ktaf.core.*
import ktaf.typeclass.minus
import ktaf.typeclass.plus
import ktaf.util.*
import lwjglkt.*

class DrawContext2D(target: RenderTarget): DrawContext<DrawContext2DRenderer>(target) {
    var fill: Boolean
        get() = stateManager.activeState.fill
        set(fill) { stateManager.activeState.fill = fill }

    var colour: RGBA
        get() = stateManager.activeState.colour
        set(colour) { stateManager.activeState.colour = colour }

    var lineWidth: Float
        get() = stateManager.activeState.lineWidth
        set(width) { stateManager.activeState.lineWidth = width }

    var scissor: AABB?
        get() = stateManager.fold(null as AABB?) { a, b -> b.scissor?.let { a?.intersection(it) } ?: b.scissor ?: a }
        set(scissor) { stateManager.activeState.scissor = scissor?.intersection(AABB(vec2(0f), target.size)) }

    val transform: mat4
        get() = (scissor?.let { it.max - it.min } ?: target.size) .let { viewSize ->
            mat4_identity *
                    mat3_scale(vec3(1f, -1f, 1f)).mat4() *
                    mat4_translate(vec3(-1f, -1f, 0f)) *
                    mat3_scale(vec3(2 / viewSize.x, 2 / viewSize.y, 1f)).mat4() *
                    mat4_translate(-vec3(scissor?.min?.x ?: 0f, scissor?.min?.y ?: 0f, 0f)) *
                    stateManager.fold(mat4_identity) { a, b -> a * b.transform }
        }

    fun <T> push(fn: DrawContext2D.() -> T): T {
        push()
        val result = fn(this)
        pop()
        return result
    }

    fun push() { stateManager.push() }
    fun pop() { stateManager.pop() }

    fun translate(translation: vec2) {
        stateManager.activeState.transform *= mat4_translate(translation.vec3(0f))
    }

    fun rotate(theta: Float) {
        stateManager.activeState.transform *= mat3_rotate(theta, vec3(0f, 0f, -1f)).mat4()
    }

    fun rotateAbout(position: vec2, theta: Float) {
        translate(position)
        rotate(theta)
        translate(-position)
    }

    fun scale(scale: vec2) {
        stateManager.activeState.transform *= mat3_scale(scale.vec3(1f)).mat4()
    }

    fun scale(scale: Float) {
        stateManager.activeState.transform *= mat3_scale(vec3(scale, scale, 1f)).mat4()
    }

    override fun getTransformation(): mat4 {
        val viewSize = scissor?.let { it.max - it.min } ?: target.size

        return mat4_identity *
                mat3_scale(vec3(1f, -1f, 1f)).mat4() *
                mat4_translate(vec3(-1f, -1f, 0f)) *
                mat3_scale(vec3(2 / viewSize.x, 2 / viewSize.y, 1f)).mat4() *
                mat4_translate(-vec3(scissor?.min?.x ?: 0f, scissor?.min?.y ?: 0f, 0f)) *
                stateManager.fold(mat4_identity) { a, b -> a * b.transform }
    }

    override fun setRenderState(fn: () -> Unit) {
        val sOffset = target.offset + vec2(scissor?.min?.x ?: 0f, target.size.y - (scissor?.max?.y
                ?: target.size.y))
        val sSize = scissor?.max?.minus(scissor?.min ?: vec2(0f)) ?: target.size
        GL.viewport(sOffset.x.toInt(), sOffset.y.toInt(), sSize.x.toInt(), sSize.y.toInt())
        fn()
    }

    override fun getShader(): GLShaderProgram = shaderProgram2D
    override fun setConstantUniforms(shader: GLShaderProgram) {}
    override fun createRenderer(shader: GLShaderProgram): DrawContext2DRenderer
            = DrawContext2DRenderer(this, shader)

    private val stateManager = DrawStateManager()

    companion object {
        internal val shaderProgram2D: GLShaderProgram = createGLShaderProgram {
            val fragment = shader(GLShaderType.GL_VERTEX_SHADER, "#version 400 core\n" +
                    "\n" +
                    "// model attributes\n" +
                    "layout (location=0) in vec3 vertex;\n" +
                    "layout (location=1) in vec2 vertex_uv;\n" +
                    "layout (location=3) in vec3 vertex_colour;\n" +
                    "\n" +
                    "out vec3 fragment_colour;\n" +
                    "out vec2 fragment_uv;\n" +
                    "\n" +
                    "uniform mat4 $TRANSFORM_UNIFORM;\n" +
                    "\n" +
                    "void main(void) {\n" +
                    "    gl_Position = $TRANSFORM_UNIFORM * vec4(vertex, 1);\n" +
                    "    fragment_colour = vertex_colour;\n" +
                    "    fragment_uv = vertex_uv;\n" +
                    "}")

            val vertex = shader(GLShaderType.GL_FRAGMENT_SHADER, "#version 400 core\n" +
                    "\n" +
                    "in vec3 fragment_colour;\n" +
                    "in vec2 fragment_uv;\n" +
                    "\n" +
                    "uniform sampler2D textureSampler;\n" +
                    "uniform vec4 colour = vec4(1, 1, 1, 1);\n" +
                    "uniform bool useTexture = false;\n" +
                    "\n" +
                    "void main(void) {\n" +
                    "    gl_FragColor = colour * vec4(fragment_colour, 1.0);\n" +
                    "    if (useTexture) gl_FragColor *= texture(textureSampler, fragment_uv);\n" +
                    "}")

            link()
            validate()
            detach(fragment)
            detach(vertex)
        }
    }
}

private class DrawStateManager {
    val activeState: DrawState
        get() = states.lastOrNull() ?: state

    fun push() {
        states.add(DrawState(activeState))
    }

    fun pop() {
        if (states.isNotEmpty()) states.removeAt(states.size - 1)
    }

    fun <T> fold(default: T, fn: (T, DrawState) -> T): T
            = states.fold(fn(default, state), fn)

    private val state = DrawState()
    private val states: MutableList<DrawState> = mutableListOf()
}

private class DrawState(parent: DrawState? = null) {
    var fill: Boolean = parent?.fill ?: true
    var transform: mat4 = mat4_identity
    var colour: vec4 = parent?.colour ?: vec4(1f)
    var lineWidth: Float = parent?.lineWidth ?: 1f
    var scissor: AABB? = null
}

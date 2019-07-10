package ktaf.graphics

import ktaf.core.*
import ktaf.typeclass.minus
import ktaf.typeclass.plus
import ktaf.util.*
import lwjglkt.*

fun <T> DrawContext2D.draw(fn: DrawContext2D.() -> T)
        = fn(this)

fun <T> DrawContext2D.push(fn: DrawContext2D.() -> T): T {
    push()
    val result = fn()
    pop()
    return result
}

class DrawContext2D(val viewport: GLViewport) {
    var fill: Boolean
        get() = activeState.fill
        set(fill) { activeState.fill = fill }

    var colour: RGBA
        get() = activeState.colour
        set(colour) { activeState.colour = colour }

    var lineWidth: Float
        get() = activeState.lineWidth
        set(width) { activeState.lineWidth = width }

    var scissor: AABB?
        get() = states.fold(state.scissor) { a, b -> b.scissor?.let { a?.intersection(it) } }
        set(scissor) { activeState.scissor = scissor?.intersection(AABB(vec2(0f), viewport.size)) }

    val shader get() = shaderProgram2D

    val transform: mat4
        get() = (scissor?.let { it.max - it.min } ?: viewport.size) .let { viewSize ->
            mat4_identity *
                    mat3_scale(vec3(1f, -1f, 1f)).mat4() *
                    mat4_translate(vec3(-1f, -1f, 0f)) *
                    mat3_scale(vec3(2 / viewSize.x, 2 / viewSize.y, 1f)).mat4() *
                    mat4_translate(-vec3(scissor?.min?.x ?: 0f, scissor?.min?.y ?: 0f, 0f)) *
                    states.fold(state.transform) { a, b -> a * b.transform }
        }

    fun push() {
        states.add(DrawState(activeState))
    }

    fun pop() {
        if (states.isNotEmpty()) states.removeAt(states.size - 1)
    }

    fun translate(translation: vec2) {
        activeState.transform *= mat4_translate(translation.vec3(0f))
    }

    fun rotate(theta: Float) {
        activeState.transform *= mat3_rotate(theta, vec3(0f, 0f, -1f)).mat4()
    }

    fun rotateAbout(position: vec2, theta: Float) {
        translate(position)
        rotate(theta)
        translate(-position)
    }

    fun scale(scale: vec2) {
        activeState.transform *= mat3_scale(scale.vec3(1f)).mat4()
    }

    fun scale(scale: Float) {
        activeState.transform *= mat3_scale(vec3(scale, scale, 1f)).mat4()
    }

    fun draw(fn: DrawContext2DRenderer.() -> Unit) {
        val sOffset = viewport.offset + vec2(scissor?.min?.x ?: 0f, viewport.size.y - (scissor?.max?.y
                ?: viewport.size.y))
        val sSize = scissor?.max?.minus(scissor?.min ?: vec2(0f)) ?: viewport.size
        GLViewport(sOffset.x.toInt(), sOffset.y.toInt(), sSize.x.toInt(), sSize.y.toInt()).setGLViewport()
        val renderer = DrawContext2DRenderer(this)
        shaderProgram2D.useIn { fn(renderer) }
    }

    private val state = DrawState()

    private val states: MutableList<DrawState> = mutableListOf()

    private val activeState: DrawState
        get() = if (states.isNotEmpty()) states.last() else state

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
                    "uniform mat4 transform;\n" +
                    "\n" +
                    "void main(void) {\n" +
                    "    gl_Position = transform * vec4(vertex, 1);\n" +
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

private class DrawState(parent: DrawState? = null) {
    var fill: Boolean = parent?.fill ?: true
    var transform: mat4 = mat4_identity
    var colour: vec4 = parent?.colour ?: vec4(1f)
    var lineWidth: Float = parent?.lineWidth ?: 1f
    var scissor: AABB? = null
}

package ktaf.graphics

import geometry.*
import ktaf.core.*
import lwjglkt.*
import kotlin.reflect.KMutableProperty0

open class DrawCtx {
    fun push() { stateStack.push(DrawCtxState()) }
    fun pop() { stateStack.pop() }
    fun push(fn: () -> Unit) { push(); fn(); pop() }

    fun pushShaders(vararg shaders: FragmentShader) {
        shaderStack.add(shaders.toList())
    }

    fun popShaders() { if (shaderStack.isNotEmpty())
        shaderStack.removeAt(shaderStack.size - 1)
    }

    fun translate(offset: vec2) { stateStack.push(stateStack.pop().translate(offset)) }
    fun rotate(angle: Float) { stateStack.push(stateStack.pop().rotate(angle)) }
    fun scale(scale: vec2) { stateStack.push(stateStack.pop().scale(scale)) }
    fun scale(scale: Float) { scale(vec2_one * scale) }
    fun colour(colour: RGBA) { stateStack.push(stateStack.pop().setColour(colour)) }

    fun rotateAbout(angle: Float, centre: vec2) {
        translate(centre)
        rotate(angle)
        translate(-centre)
    }

    fun projection(projection: Projection) { this.projection = projection }

    fun viewport(x: Int, y: Int, width: Int, height: Int) {
        viewportX = x
        viewportY = y
        viewportWidth = width
        viewportHeight = height
    }

    fun draw(fn: DrawCtxRenderer.() -> Unit) {
        val shader = shader()
        val projectionMatrix = projection.matrix(viewportWidth, viewportHeight)
        val renderer = DrawCtxRenderer(this, shader, vaoCache)

        GL.viewport(viewportX, viewportY, viewportWidth, viewportHeight)

        shader.useIn {
            shader.uniform("projection", projectionMatrix)
            fn(renderer)
        }
    }

    private fun <T: Any> compoundProperty(
            prop: KMutableProperty0<T>,
            getter: (DrawCtxState) -> T?, join: (T, T) -> T
    ) {
        val base = prop.get()
        stateStack.pushed.connect { updateProperty(prop, base, getter, join) }
        stateStack.popped.connect { updateProperty(prop, base, getter, join) }
    }

    private fun <T: Any> updateProperty(
            prop: KMutableProperty0<T>,
            base: T, getter: (DrawCtxState) -> T?, join: (T, T) -> T
    ) = prop.set(stateStack.fold(base, getter, join))

    /** Create the shader object to use when drawing.
     *  Will reuse the last compiled shader if possible.
     *  Must be called while in an OpenGL context. */
    private fun shader(): GLShaderProgram {
        val shaderList = shaderStack.lastOrNull() ?: listOf()

        if (!::lastShaderList.isInitialized || lastShaderList != shaderList) {
            if (::compiledShader.isInitialized) compiledShader.free()

            compiledShader = createGLShaderProgram {
                val vertex = vertexShader()
                val fragment = createGLShader(GLShaderType.GL_FRAGMENT_SHADER) {
                    source(shaderList.compile())
                    compile()
                }

                attach(vertex)
                attach(fragment)
                link()
                validate()
                detach(vertex)
                detach(fragment)
            }

            lastShaderList = shaderList
        }

        return compiledShader
    }

    /** Create the default vertex shader.
     *  Must be called while in an OpenGL context. */
    private fun vertexShader(): GLShader = createGLShader(GLShaderType.GL_VERTEX_SHADER) {
        source(VERTEX_SHADER_SOURCE)
        compile()
    }

    internal var transformation: mat4 = mat4_identity
        private set
    internal var colour: RGBA = rgba(1f)
        private set

    private val vaoCache = DrawCtxVAOCache()
    private var stateStack = StateStack<DrawCtxState>()
    private var shaderStack: MutableList<List<FragmentShader>> = mutableListOf()
    private var projection: Projection = Projection.identity()
    private var viewportX = 0
    private var viewportY = 0
    private var viewportWidth = 100
    private var viewportHeight = 100
    private lateinit var lastShaderList: List<FragmentShader>
    private lateinit var compiledShader: GLShaderProgram // TODO: this should cache better

    init {
        compoundProperty<mat4>(::transformation, DrawCtxState::transformation, mat4::times)
        compoundProperty<RGBA>(::colour, DrawCtxState::colour) { _, c -> c }

        push()
    }
}

private const val VERTEX_SHADER_SOURCE = """#version 440 core
layout (location=$VERTEX_POSITION_ATTRIBUTE) in vec3 vertex_position;
layout (location=$VERTEX_NORMAL_ATTRIBUTE) in vec3 vertex_normal;
layout (location=$VERTEX_COLOUR_ATTRIBUTE) in vec4 vertex_colour;
layout (location=$VERTEX_UV_ATTRIBUTE) in vec2 vertex_uv;

out vec3 fragment_position;
out vec3 fragment_normal;
out vec4 fragment_colour;
out vec2 fragment_uv;

uniform mat4 projection;
uniform mat4 transformation;
uniform vec4 colour;

void main(void) {
    fragment_position = (transformation * vec4(vertex_position, 1)).xyz;
    fragment_normal = normalize((transpose(inverse(transformation)) * vec4(vertex_normal, 0)).xyz); 
    fragment_colour = colour * vertex_colour;
    fragment_uv = vertex_uv;
    
    gl_Position = projection * transformation * vec4(vertex_position, 1); 
}
"""

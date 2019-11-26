package ktaf.graphics

import geometry.*
import ktaf.util.size
import ktaf.util.uniform
import ktaf.data.Value
import ktaf.data.property.mutableProperty
import lwjglkt.gl.*
import lwjglkt.gl.enum.*
import lwjglkt.util.*

class DrawContext2D(
        glContext: GLContext,
        screenSize: Value<vec2>
): DrawContext(glContext, screenSize) {
    val DEFAULT_FONT: Font = FNTFont.load(glContext,
            FNTFont.preloadResource("font/open-sans/OpenSans-Regular.fnt"))
            .scaleTo(16f)

    val colour = mutableProperty(rgba(1f))

    fun vao(count: Int, vao: GLVAO,
            transform: mat4 = mat4_identity,
            texture: GLTexture2? = null
    ) {
        shader.uniform("u_viewportSize", viewportSize.value)
        shader.uniform("u_colour", colour.value)
        shader.uniform("u_transform", transform)
        shader.uniform("u_enableTexture", texture != null)
        shader.uniform("u_texture", 0)

        texture?.use(0)

        shader.useIn {
            vao.bindIn {
                currentContext.gl.drawElements(count)
            }
        }

        texture?.stopUsing()
    }

    fun image(texture: GLTexture2, position: vec2, scale: vec2 = vec2_one) {
        val (x0, y0) = position
        val (x1, y1) = position + texture.size * scale

        quadBuffer.subData(floatArrayOf(x0, y0, 0f, x0, y1, 0f, x1, y1, 0f, x1, y0, 0f))

        vao(6, quadVAO, texture = texture)
    }

    // TODO: flip Y axis (for some reason?)
    fun texture(texture: GLTexture2, position: vec2, scale: vec2 = vec2_one) {
        val (x0, y0) = position
        val (x1, y1) = position + texture.size * scale

        quadBuffer.subData(floatArrayOf(x0, y0, 0f, x0, y1, 0f, x1, y1, 0f, x1, y0, 0f))

        vao(6, quadVAO, texture = texture)
    }

    fun triangle(a: vec2, b: vec2, c: vec2) {
        shader.uniform("u_viewportSize", viewportSize.value)
        shader.uniform("u_colour", colour.value)
        quadBuffer.subData(floatArrayOf(a.x, a.y, 0f, b.x, b.y, 0f, c.x, c.y, 0f))

        shader.useIn {
            triVAO.bindIn {
                currentContext.gl.drawArrays(3)
            }
        }
    }

    fun rectangle(position: vec2, size: vec2) {
        val (x0, y0) = position
        val (x1, y1) = position + size

        quadBuffer.subData(floatArrayOf(x0, y0, 0f, x0, y1, 0f, x1, y1, 0f, x1, y0, 0f))
        vao(6, quadVAO)
    }

    fun line(a: vec2, b: vec2, size: Float = 2f) {
        val hs = size / 2
        val ab = (b - a).normalise()
        val ba = -ab
        val a1 = a + ba.rotate45CW()  * hs
        val a2 = a + ba.rotate45CCW() * hs
        val b1 = b + ab.rotate45CW()  * hs
        val b2 = b + ab.rotate45CCW() * hs

        quadBuffer.subData(0, floatArrayOf(
                a1.x, a1.y, 0f,
                a2.x, a2.y, 0f,
                b1.x, b1.y, 0f,
                b2.x, b2.y, 0f
        ))

        vao(6, quadVAO)
    }

    fun point(position: vec2, size: Float = 2f) {
        shader.uniform("u_viewportSize", viewportSize.value)
        shader.uniform("u_colour", colour.value)
        pointBuffer.subData(floatArrayOf(position.x, position.y, 0f))

        currentContext.gl.rasterState {
            pointSize(size)
        }

        shader.useIn {
            pointVAO.bindIn {
                currentContext.gl.drawArrays(GLDrawMode.GL_POINTS, 1)
            }
        }
    }

    fun write(text: String, position: vec2 = vec2_zero, font: Font = DEFAULT_FONT) {
        if (text == "") return

        var x = position.x
        val y = position.y + (font.lineHeight - font.baseline) * font.scale

        (text.zip(text.drop(1)) + listOf(text.last() to null)).forEach { (char, next) ->
            val offset = font.getCharOffset(char)
            val translation = vec3(x + offset.x * font.scale, y + offset.y * font.scale, 0f)
            vao(font.getVAOVertexCount(char), font.getVAO(char),
                    transform=mat4_translate(translation) * mat3_scale(vec3(font.scale)).mat4(),
                    texture=font.getTexture(char))
            x += font.getCharAdvance(char) * font.scale
            next ?.let { x += font.getKerning(char, next) * font.scale }
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    override fun begin() {
        super.begin()

        currentContext.gl.enable(GLOption.GL_BLEND)

        currentContext.gl.rasterState {
            defaults()
            cullFace(GLFace.GL_FRONT_AND_BACK)
        }

        currentContext.gl.postFragmentShaderState {
            defaults()
            blendFunction(
                    GLBlendFunction.GL_SRC_ALPHA,
                    GLBlendFunction.GL_ONE_MINUS_SRC_ALPHA,
                    GLBlendFunction.GL_ONE,
                    GLBlendFunction.GL_ONE
            )
            blendEquation(
                    GLBlendEquation.GL_FUNC_ADD,
                    GLBlendEquation.GL_FUNC_ADD
            )
            depthFunction(GLDepthFunction.GL_ALWAYS)
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    private val shader: GLShaderProgram
    private val quadBuffer: GLVBO
    private val triBuffer: GLVBO
    private val pointBuffer: GLVBO
    private val quadVAO: GLVAO
    private val triVAO: GLVAO
    private val pointVAO: GLVAO

    ////////////////////////////////////////////////////////////////////////////

    init {
        val current = glContext.makeCurrent()

        shader = current.createShaderProgram(
                current.createShader(GLShaderType.GL_VERTEX_SHADER, VERTEX_SHADER_CODE),
                current.createShader(GLShaderType.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE)
        )

        quadBuffer = current.createVertexBuffer(
                floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f))

        triBuffer = current.createVertexBuffer(
                floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f))

        pointBuffer = current.createVertexBuffer(
                floatArrayOf(0f, 0f, 0f))

        quadVAO = current.createVAO {
            val uvs = current.createUVBuffer(floatArrayOf(0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f))
            val colours = current.createColourBuffer(4)
            val elements = current.createElementBuffer(intArrayOf(0, 1, 2, 0, 2, 3))

            bindPositionBuffer(quadBuffer)
            bindUVBuffer(uvs)
            bindColourBuffer(colours)
            bindElementBuffer(elements)
        }

        triVAO = current.createVAO {
            val uvs = current.createUVBuffer(floatArrayOf(0f, 0f, 0f, 1f, 1f, 1f))
            val colours = current.createColourBuffer(3)

            bindPositionBuffer(quadBuffer)
            bindUVBuffer(uvs)
            bindColourBuffer(colours)
        }

        pointVAO = current.createVAO {
            val colours = current.createColourBuffer(1)

            bindPositionBuffer(quadBuffer)
            bindColourBuffer(colours)
        }

        current.free()
    }
}

private const val VERTEX_SHADER_CODE = """
#version 440 core

uniform vec2 u_viewportSize;
uniform vec4 u_colour;
uniform mat4 u_transform;

layout (location=${DefaultBufferAttributes.VERTEX_POSITION_ATTRIBUTE}) in vec3 v_pos; 
layout (location=${DefaultBufferAttributes.VERTEX_COLOUR_ATTRIBUTE}) in vec3 v_colour; 
layout (location=${DefaultBufferAttributes.VERTEX_UV_ATTRIBUTE}) in vec2 v_uv;
 
out vec4 f_colour;
out vec2 f_uv;

void main(void) {
    gl_Position = u_transform * vec4(v_pos, 1) / vec4(u_viewportSize, 1, 1) * vec4(2, -2, 0, 1) + vec4(-1, 1, 0, 0);
    f_colour = u_colour * vec4(v_colour, 1); 
    f_uv = v_uv;
}
"""

private const val FRAGMENT_SHADER_CODE = """
#version 440 core

uniform bool u_enableTexture;
uniform sampler2D u_texture;

in vec4 f_colour;
in vec2 f_uv;

// TODO: textures

out vec4 fragment_colour;

void main(void) {
    fragment_colour = f_colour;
    
    if (u_enableTexture)
        fragment_colour *= texture(u_texture, f_uv);
}
"""

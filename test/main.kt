import kotlin.math.sin

fun main() = application("Hello world") {
    println("Hello world")

    val vao = computeOnMainThread { createVAO {
        genVertexPositionBuffer(listOf(vec3(0f, 0f, 0f), vec3(0f, 1f, 0f), vec3(1f, 0f, 0f)))
        genVertexColourBuffer(listOf(RGB(0f, 1f, 0f), RGB(1f, 0f, 0f), RGB(0f, 0f, 1f)))
    } }

    val program = computeOnMainThread { createGLShaderProgram {
        val vertex = shader(GLShaderType.GL_VERTEX_SHADER, "#version 460 core\n" +
                "" +
                "layout (location=0) in vec3 position;" +
                "layout (location=3) in vec3 colour;" +
                "" +
                "uniform vec3 offset;" +
                "" +
                "out vec3 fragment_colour;" +
                "" +
                "void main(void) {" +
                "    fragment_colour = colour;" +
                "    gl_Position = vec4(offset + position, 1);" +
                "}")
        val fragment = shader(GLShaderType.GL_FRAGMENT_SHADER, "#version 460 core\n" +
                "" +
                "in vec3 fragment_colour;" +
                "" +
                "uniform vec3 colour = vec3(1, 0, 1);" +
                "" +
                "void main(void) {" +
                "    gl_FragColor = vec4(colour * fragment_colour, 1);" +
                "}")
        link()
        validate()
        detach(vertex)
        detach(fragment)
    } }

    mainThread { program.uniform3f(program.uniformLocation("colour"), 1f, 1f, 0.5f) }

    val tris = IntArray(300).map {
        Pair(Math.random().toFloat() + 0.2f, Math.random().toFloat() * 0.3f + 0.2f)
    }

    draw {
        program.useIn {
            vao.bindIn {
                tris.map { (movement, flashing) ->
                    if (timer(flashing, 0f)) {
                        program.uniform3f(program.uniformLocation("offset"), pos(movement), pos(movement, 0.5f), 0f)
                        GLDraw.drawArrays(3)
                    }
                }
            }
        }
    }

    update {
        println(fps)
    }
}

val s = System.currentTimeMillis()

fun pos(period: Float = 1f, offset: Float = 0f, amplitude: Float = 1f): Float {
    return sin((System.currentTimeMillis() - s) / 1000f / period - offset) * amplitude
}

fun timer(period: Float, offset: Float): Boolean {
    return ((System.currentTimeMillis() - s) / 1000f - offset) % period < period / 2
}

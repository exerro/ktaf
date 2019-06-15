
fun main() = application("Hello world") {
    println("Hello world")

    val vao = createVAO {
        genVertexPositionBuffer(floatArrayOf(0f, 0f, 0f, 0f, 1f, 0f, 1f, 0f, 0f))
        genVertexColourBuffer(floatArrayOf(0f, 1f, 0f, 1f, 0f, 0f, 0f, 0f, 1f))
    }

    val program = createGLShaderProgram {
        val vertex = shader(GLShaderType.GL_VERTEX_SHADER, "#version 460 core\n" +
                "" +
                "layout (location=0) in vec3 position;" +
                "layout (location=3) in vec3 colour;" +
                "" +
                "out vec3 fragment_colour;" +
                "" +
                "void main(void) {" +
                "    fragment_colour = colour;" +
                "    gl_Position = vec4(position, 1);" +
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
    }

    program.uniform3f(program.uniformLocation("colour"), 1f, 1f, 0.5f)

    System.gc()

    rasterState {
        polygonMode = GLPolygonMode.GL_LINE
    }

    draw {
        program.useIn {
            vao.bindIn {
                GLDraw.drawArrays(3)
            }
        }
    }

    update {
        println(fps)
    }
}

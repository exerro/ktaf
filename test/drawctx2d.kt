import geometry.*
import ktaf.core.*
import ktaf.graphics.*
import lwjglkt.createVAO
import kotlin.math.sin

fun main() = application {
    display("Window") {
        val ctx = DrawCtx()

        val s1 = FragmentShader2D.create("""
            ktaf_fragment_colour = ktaf_colour * vec4(1, 1, 0, 1);
        """.trimIndent())

        val s2 = FragmentShader2D.create("""
            ktaf_fragment_colour = ktaf_colour + vec4(0, factor, 0, 1);
        """.trimIndent(), ShaderUniform("factor", "float"))

        glfwWindow.framebufferResized.connect {
            val (w, h) = glfwWindow.framebufferSize
            ctx.viewport(0, 0, w, h)
        }

        val (w, h) = glfwWindow.size
        ctx.viewport(0, 0, w, h)
        ctx.pushShaders(s1, s2)

        val vao = createVAO {
            genElementBuffer(listOf(0, 1, 2, 3, 2, 0))
            genVertexPositionBuffer(listOf(
                    vec3(0f, 1f, 0f),
                    vec3(0f, 0f, 0f),
                    vec3(1f, 0f, 0f),
                    vec3(1f, 1f, 0f)
            ))
            genVertexNormalBuffer(List(4) { vec3(0f, 0f, 1f) })
            genVertexUVBuffer(listOf(
                    vec2(0f, 1f),
                    vec2(0f, 0f),
                    vec2(1f, 0f),
                    vec2(1f, 1f)
            ))
            genVertexColourBuffer(4)
        }

//        ctx.translate(vec2_one * -0.5f)
        ctx.projection(Projection.screen())

        draw.connect {
            ctx.draw {
                shader.uniform("factor", sin(time) / 2 + 0.5f)
                vao(vao, 6, rgba(1f, 0f, 1f, 1f),
                        transform = mat4_translate(vec3(100f, 100f, 0f)) *
                mat3_scale(30f).mat4())
            }
        }
    }
}

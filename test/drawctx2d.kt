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

//        ctx.translate(vec2_one * -0.5f)
        ctx.projection(Projection.screen())

        update.connect { dt ->
            ctx.rotateAbout(dt, vec2(60f))
        }

        draw.connect {
            ctx.draw {
                shader.uniform("factor", sin(time) / 2 + 0.5f)
                rectangle(vec2_one * 50f, vec2_one * 30f)
            }
        }
    }
}

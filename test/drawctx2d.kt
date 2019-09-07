import geometry.times
import geometry.vec2
import geometry.vec2_one
import ktaf.core.application
import ktaf.core.uniform
import ktaf.graphics.DrawCtx
import ktaf.graphics.FragmentShader
import ktaf.graphics.Projection
import lwjglkt.GL
import lwjglkt.GLOption
import kotlin.math.max
import kotlin.math.sin

fun main() = application {
    display("Window") {
        val ctx = DrawCtx()
        var pos = vec2(100f, 200f)
        var size = 2f

        val circleShader = FragmentShader.create("""
            float distance = length(ktaf_position - vec3(400, 400, 0));
            ktaf_fragment_colour = (ktaf_colour + 100) / (distance + 100);
        """.trimIndent())

        glfwWindow.framebufferResized.connect { (w, h) ->
            ctx.viewport(0, 0, w, h)
        }

        onMouseMove.connect { event ->
            pos = event.position
        }

        onMouseScroll.connect { event ->
            size += event.direction.y
            size = max(1f, size)
        }

        ctx.viewport(0, 0, glfwWindow.size.width, glfwWindow.size.height)
        ctx.projection(Projection.screen())

        draw.connect {
            ctx.draw {
                GL.enable(GLOption.GL_BLEND)
                shader.uniform("factor", sin(time) / 2 + 0.5f)
                rectangle(vec2_one * 50f, vec2_one * 30f)
                write("Hello world", vec2(300f, 100f))
                line(vec2(10f, 100f), pos, size)
            }

            ctx.pushShaders(circleShader)
            ctx.draw {
                circle(vec2(400f), 100f)
            }
            ctx.popShaders()
        }
    }
}

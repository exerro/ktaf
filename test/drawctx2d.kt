import geometry.times
import geometry.vec2
import geometry.vec2_one
import ktaf.core.Colour
import ktaf.core.application
import ktaf.core.uniform
import ktaf.graphics.FragmentShader
import ktaf.graphics.Projection
import lwjglkt.gl.GLOption
import kotlin.math.max
import kotlin.math.sin

fun main() = application {
    display("Window") {
        val ctx = context2D
        var pos = vec2(100f, 200f)
        var size = 2f

        val circleShader = FragmentShader.create("""
            float distance = length(ktaf_position - vec3(320, 340, 0));
            ktaf_fragment_colour = ktaf_colour * 10000 / (distance * distance + 10000);
        """.trimIndent())

        val fontShader = FragmentShader.create("""
            ktaf_fragment_colour = vec4(vec3(ktaf_uv, 0), 1);
        """.trimIndent())

        onMouseMove.connect { event ->
            pos = event.position
        }

        onMouseScroll.connect { event ->
            size += event.direction.y
            size = max(1f, size)
        }

        ctx.projection(Projection.screen())

        draw.connect {
            ctx.draw {
                ctx.glContext.enable(GLOption.GL_BLEND)
                shader.uniform("factor", sin(time) / 2 + 0.5f)
                ctx.colour(Colour.blue)
                rectangle(vec2_one * 50f, vec2_one * 30f)
                ctx.colour(Colour.green)
                line(vec2(10f, 100f), pos, size)
            }

//            ctx.pushShaders(fontShader)
            ctx.draw {
                ctx.colour(Colour.red)
                write("Hello world", vec2(300f, 100f))
            }
//            ctx.popShaders()

            ctx.pushShaders(circleShader)
            ctx.draw {
                ctx.colour(Colour.purple)
                circle(vec2(400f), 100f)
            }
            ctx.popShaders()
        }
    }
}

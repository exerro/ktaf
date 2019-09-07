import geometry.vec2
import ktaf.core.application
import ktaf.core.rgba

fun main() = application {
    display("Display") {
        val context = context2D

        draw.connect {
            context.colour(rgba(1f, 0f, 1f))
            context.draw {
                rectangle(vec2(100f), vec2(100f))
            }
        }
    }
}

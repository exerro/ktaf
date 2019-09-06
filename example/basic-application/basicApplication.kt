import geometry.vec2
import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.graphics.rectangle

fun main() = application {
    display("Display") {
        val context = DrawContext2D(screen)

        draw.connect {
            context.colour = rgba(1f, 0f, 1f)
            context.draw {
                rectangle(vec2(100f), vec2(100f))
            }
        }
    }
}

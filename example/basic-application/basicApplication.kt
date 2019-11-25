import geometry.vec2
import ktaf.core.application
import ktaf.graphics.rgba
import ktaf.property.const

fun main() = application {
    display("Display", 1080, 720) { window ->
        val context = window.drawContext2D

        window.draw.connect {
            context.begin()
            context.colour <- const(rgba(1f, 0f, 1f))
            context.rectangle(vec2(100f), vec2(100f))
            context.end()
        }
    }
}

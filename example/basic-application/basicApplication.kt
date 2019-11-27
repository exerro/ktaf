import geometry.vec2
import ktaf.core.application
import ktaf.graphics.rgba
import ktaf.data.property.const
import ktaf.util.compareTo

fun main() = application {
    window("Display", 1080, 720) { window ->
        val context = window.drawContext2D

        window.draw.subscribe(window) {
            context.begin()
            context.colour <- const(rgba(1f, 0f, 1f))
            context.rectangle(vec2(100f), vec2(100f))
            context.end()
        }
    }
}

import geometry.vec2
import ktaf.core.application

fun main() = application {
    window("Hello", 1080, 720) { window ->
        window.draw.subscribe(window) {
            window.drawContext2D.begin()
            window.drawContext2D.rectangle(vec2(0f), vec2(100f))
            window.drawContext2D.end()
        }
    }

    window("World", 1080, 720) { window ->
        window.draw.subscribe(window) {
            window.drawContext2D.begin()
            window.drawContext2D.rectangle(vec2(0f), vec2(100f))
            window.drawContext2D.end()
        }

        window.events.mousePressed.subscribe(window) { event ->
            window("!", 240, 240) { w ->
                w.draw.subscribe(window) {
                    w.drawContext2D.begin()
                    w.drawContext2D.rectangle(vec2(0f), vec2(100f))
                    w.drawContext2D.end()
                }
            }
        }
    }
}

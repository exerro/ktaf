import geometry.vec2
import ktaf.core.application

fun main() = application {
    display("Hello", 1080, 720) { window ->
        window.draw.connect {
            window.drawContext2D.begin()
            window.drawContext2D.rectangle(vec2(0f), vec2(100f))
            window.drawContext2D.end()
        }
    }

    display("World", 1080, 720) { window ->
        window.draw.connect {
            window.drawContext2D.begin()
            window.drawContext2D.rectangle(vec2(0f), vec2(100f))
            window.drawContext2D.end()
        }

        window.events.setMouseButtonCallback { _, _, action, _ ->
            display("!", 240, 240) { w ->
                w.draw.connect {
                    w.drawContext2D.begin()
                    w.drawContext2D.rectangle(vec2(0f), vec2(100f))
                    w.drawContext2D.end()
                }
            }
        }
    }
}

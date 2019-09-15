import geometry.vec2
import ktaf.core.application

fun main() = application {
    display("Hello") {
        draw.connect {
            context2D.draw {
                rectangle(vec2(0f), vec2(100f))
            }
        }
    }

    display("World") {
        draw.connect {
            context2D.draw {
                rectangle(vec2(0f), vec2(100f))
            }
        }

        onMousePress.connect {
            display("!") {
                draw.connect {
                    context2D.draw {
                        rectangle(vec2(0f), vec2(100f))
                    }
                }
            }
        }
    }
}

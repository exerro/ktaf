package ui

import graphics.DrawContext2D
import RGB
import graphics.rectangle
import vec2

class UIButton: UINode() {
    internal val clickEventHandlers: EventHandlerList<UIMouseClickEvent> = mutableListOf()
    var colour by property(RGB(1f))

    init {
        onMousePress { event -> event.ifNotHandled {
            if (event.within(this)) {
                event.handledBy(this)
            }
        } }

        onMouseClick { event -> event.ifNotHandled {
            if (event.within(this)) {
                event.handledBy(this)
                clickEventHandlers.forEach { it(event) }
            }
        } }
    }

    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        context.draw {
            context.colour = colour
            rectangle(position, size)
        }
        super.draw(context, position, size)
    }
}

fun UIButton.onClick(fn: UIButton.(UIMouseClickEvent) -> Unit) {
    clickEventHandlers.add { fn(this, it) }
}

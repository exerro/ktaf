package ktaf.ui.elements

import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.graphics.circle
import ktaf.typeclass.minus
import ktaf.typeclass.plus
import ktaf.ui.DEFAULT_STATE
import ktaf.ui.UIAnimatedProperty
import ktaf.ui.UIProperty
import ktaf.ui.layout.Border
import ktaf.ui.layout.size
import ktaf.ui.layout.tl
import ktaf.ui.node.UINode
import ktaf.ui.node.push
import ktaf.ui.node.remove
import ktaf.ui.typeclass.Clickable
import ktaf.util.Animation
import lwjglkt.glfw.GLFWCursor
import kotlin.math.min

class UIRadioButton: UINode(), Clickable {
    val group = KTAFValue<RadioButtonGroup?>(null)
    val colour = UIAnimatedProperty(rgba(0f), this, "colour", animationDuration = Animation.QUICK)
    val hoverColour = UIProperty(rgba(0f, 0.9f))
    val checkColour = UIProperty(rgba(0f, 0.9f))
    val checked = KTAFValue(false)

    private val effectiveCheckColour = UIAnimatedProperty(rgba(0f), this, "effectiveCheckColour", animationDuration = Animation.QUICK)
    private val hovering = KTAFValue(false)

    override fun cursor(): GLFWCursor? = GLFWCursor.POINTER

    override fun click(event: Event) { checked(true) }

    override fun computeContentWidth(width: Float?): Float = DEFAULT_SIZE
    override fun computeContentHeight(width: Float, height: Float?): Float = width

    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        context.draw {
            context.fill = true

            context.colour = colour.get()
            circle(position + size / 2f, min(size.x, size.y) / 2)

            context.colour = effectiveCheckColour.get()
            circle(position + padding.get().tl + (size - padding.get().size) / 2f, (size - padding.get().size).let { min(it.x, it.y) / 2 })
        }
    }

    init {
        propertyState(colour)
        propertyState(checkColour)
        propertyState(hoverColour)

        onKeyPress { click(it); }
        onMouseClick { click(it); }
        onMouseEnter { hovering(it.target) }
        onMouseExit { hovering(false) }

        checked.connect { if (it) state.push(CHECKED) else state.remove(CHECKED) }

        checked.connect { if (it) {
            effectiveCheckColour(checkColour.get())
        } else {
            effectiveCheckColour(if (hovering.get()) hoverColour.get() else hoverColour.get().rgb().rgba(0f))
        } }

        hovering.connect { if (!checked.get())
            effectiveCheckColour(if (it) hoverColour.get() else hoverColour.get().rgb().rgba(0f))
        }

        checkColour.connect { if (checked.get()) effectiveCheckColour(it) }
        hoverColour.connect { if (!checked.get()) effectiveCheckColour(if (hovering.get()) hoverColour.get() else hoverColour.get().rgb().rgba(0f)) }

        checkColour.setSetter {
            this[DEFAULT_STATE](it)
            this[HOVER](it.lighten())
        }

        group.connectComparator { old, new ->
            old?.buttons?.remove(this)
            new?.buttons?.add(this)
        }

        checked.connect { checked -> if (checked) group.get() ?.buttons?.filter { it != this } ?.forEach { it.checked(false) } }

        checkColour(Colour.blue)
        hoverColour(rgba(0.73f))
        colour(rgba(0.8f))
        padding(Border(3f))
    }

    companion object {
        const val CHECKED = "checked"
        const val DEFAULT_SIZE = 18f
    }
}

class RadioButtonGroup {
    val buttons = KTAFList<UIRadioButton>()

    init {
        buttons.connectAdded { it.group(this) }
        buttons.connectRemoved { it.group(null) }
    }
}

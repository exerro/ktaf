package ktaf.ui.elements

import ktaf.core.rgba
import ktaf.core.vec2
import ktaf.graphics.DrawContext2D
import ktaf.graphics.Font
import ktaf.graphics.rectangle
import ktaf.typeclass.minus
import ktaf.typeclass.plus
import ktaf.ui.UIAnimatedProperty
import ktaf.ui.UIProperty
import ktaf.ui.layout.height
import ktaf.ui.layout.size
import ktaf.ui.layout.tl
import ktaf.ui.layout.width
import ktaf.ui.node.UINode
import ktaf.ui.node.drawText
import ktaf.ui.node.textHeight
import ktaf.ui.node.textWidth
import ktaf.util.Animation
import kotlin.math.min

abstract class UITextRenderer: UINode() {
    val colour = UIAnimatedProperty(rgba(0f), this, "colour", animationDuration = Animation.QUICK)
    val textColour = UIAnimatedProperty(rgba(0f, 0.9f), this, "textColour", animationDuration = Animation.QUICK)
    val text = UIProperty("")
    val font = UIProperty(Font.DEFAULT_FONT.scaleTo(16f))
    val alignment = UIAnimatedProperty(vec2(0f), this, "alignment")
    val wrap = UIProperty(true)

    override fun computeContentWidth(width: Float?): Float {
        return textWidth(text.get(), font.get(), wrap.get()).let { w -> width ?.let { min(w, it) } ?: w } + padding.get().width
    }

    override fun computeContentHeight(width: Float, height: Float?): Float {
        return textHeight(text.get(), font.get(), wrap.get(), width) + padding.get().height
    }

    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        context.draw {
            context.colour = colour.get()
            context.fill = true
            rectangle(position, size)
        }

        drawText(context, position + padding.get().tl, size - padding.get().size,
                text = text.get(),
                font = font.get(),
                wrap = wrap.get(),
                alignment = alignment.get(),
                colour = textColour.get()
        )
    }

    init {
        propertyState(colour)
        propertyState(textColour)
        propertyState(text)
        propertyState(font)
        propertyState(alignment)
        propertyState(wrap)

        colour(rgba(1f, 0f))
        textColour(rgba(1f))
        text("")
        font(Font.DEFAULT_FONT.scaleTo(16f))
        alignment(vec2(0f))
        wrap(true)
    }
}

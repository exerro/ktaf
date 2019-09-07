package ktaf.ui.elements

import geometry.minus
import geometry.plus
import geometry.vec2
import ktaf.core.rgba
import ktaf.graphics.DrawCtx
import ktaf.graphics.Font
import ktaf.ui.UIProperty
import ktaf.ui.layout.height
import ktaf.ui.layout.size
import ktaf.ui.layout.tl
import ktaf.ui.layout.width
import ktaf.ui.node.UINode
import ktaf.ui.node.drawText
import ktaf.ui.node.textHeight
import ktaf.ui.node.textWidth
import kotlin.math.min

abstract class UITextRenderer: UINode() {
    val colour = UIProperty(rgba(0f))
    val textColour = UIProperty(rgba(0f, 0.9f))
    val text = UIProperty("")
    val font = UIProperty(Font.DEFAULT_FONT.scaleTo(16f))
    val alignment = UIProperty(vec2(0f))
    val wrap = UIProperty(true)

    override fun computeContentWidth(width: Float?): Float {
        return textWidth(text.get(), font.get(), wrap.get()).let { w -> width ?.let { min(w, it) } ?: w } + padding.get().width
    }

    override fun computeContentHeight(width: Float, height: Float?): Float {
        return textHeight(text.get(), font.get(), wrap.get(), width) + padding.get().height
    }

    override fun draw(context: DrawCtx, position: vec2, size: vec2) {
        context.draw {
            context.colour(colour.get())
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

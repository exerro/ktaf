package ktaf.ui.elements

import ktaf.core.KTAFValue
import ktaf.core.rgba
import ktaf.core.vec2
import ktaf.graphics.DrawContext2D
import ktaf.graphics.Font

class UIText: UITextBase() {
    val font = KTAFValue(Font.DEFAULT_FONT.scaleTo(24f))
    val textColour = KTAFValue(rgba(0f))

    override fun generateSegments(line: String): List<UITextSegment> {
        return listOf(UITextSegment(font.get(), textColour.get(), setOf(), 0, 0, line.length))
    }

    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        super.draw(context, position, size)
    }

    init {
        init()
    }

}

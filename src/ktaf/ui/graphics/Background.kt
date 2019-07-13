package ktaf.ui.graphics

import ktaf.core.vec2
import ktaf.graphics.DrawContext2D

abstract class Background {
    abstract fun draw(context: DrawContext2D, position: vec2, size: vec2)

    open fun computeWidth(): Float? = null
    open fun computeHeight(width: Float): Float? = null
}

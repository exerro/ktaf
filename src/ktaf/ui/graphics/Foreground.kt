package ktaf.ui.graphics

import ktaf.core.vec2
import ktaf.graphics.DrawContext2D

abstract class Foreground {
    abstract fun draw(context: DrawContext2D, position: vec2, size: vec2)
    open fun getHeight(width: Float): Float? = null
}

package ktaf.gui.core

import geometry.vec2
import ktaf.data.property.AnimatedProperty
import ktaf.data.animation.Animation
import ktaf.data.property.MutableProperty

/** Padding within a node. */
data class Padding(
        /** Padding above the node's content. */
        val top: Float = 0f,
        /** Padding to the right of the node's content. */
        val right: Float = 0f,
        /** Padding below the node's content. */
        val bottom: Float = 0f,
        /** Padding to the left of the node's content. */
        val left: Float = 0f
) {
    constructor(all: Float): this(all, all)
    constructor(vertical: Float, horizontal: Float)
            : this(vertical, horizontal, vertical, horizontal)

    /** Sum of the horizontal components. */
    val width get() = left + right
    /** Sum of the vertical components. */
    val height get() = top + bottom
    /** Vector of the top and left components. */
    val topLeft get() = vec2(left, top)
    /** Vector of the sum of the components. */
    val size get() = vec2(width, height)
}

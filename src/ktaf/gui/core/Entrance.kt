package ktaf.gui.core

import geometry.vec2
import geometry.vec2_zero
import ktaf.core.Window

/** Object representing how a node should animate in when positioned for the
 *  first time. */
class Entrance(
        internal val position: (vec2, vec2) -> vec2,
        internal val size: (vec2, vec2) -> vec2
) {
    companion object {
        /** No entrance, nodes snap directly to their initial position. */
        val none = Entrance({ p, _ -> p }, { _, s -> s })
        /** Nodes grow from the centre of their initial position, expanding to
         *  the target position/size. */
        val grow = Entrance({ p, s -> (p + p + s).div(2f) }, { _, _ -> vec2_zero })

        /** Nodes move in from the left of the screen. */
        fun fromLeft()
                = Entrance({ p, s -> vec2(-s.x, p.y) }, { _, s -> s })

        /** Nodes move in from the right of the screen. */
        fun fromRight(window: Window)
                = Entrance({ p, s -> vec2(window.width.value, p.y) }, { _, s -> s })

        /** Nodes move in from the top of the screen. */
        fun fromTop()
                = Entrance({ p, s -> vec2(p.x, -s.y) }, { _, s -> s })

        /** Nodes move in from the bottom of the screen. */
        fun fromBottom(window: Window)
                = Entrance({ p, s -> vec2(p.x, window.height.value) }, { _, s -> s })
    }
}

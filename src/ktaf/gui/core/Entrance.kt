package ktaf.gui.core

import geometry.vec2
import geometry.vec2_zero

/** Object representing how a node should animate in when positioned for the
 *  first time. */
class Entrance internal constructor(
        internal val position: (vec2, vec2) -> Pair<vec2, vec2>,
        internal val size: (vec2, vec2) -> Pair<vec2, vec2>
) {
    companion object {
        /** No entrance, nodes snap directly to their initial position. */
        val NONE = Entrance({ p, _ -> p to p }, { _, s -> s to s })
        /** Nodes grow from the centre of their initial position, expanding to
         *  the target position/size. */
        val GROW = Entrance({ p, s -> (p + p + s).div(2f) to p }, { _, s -> vec2_zero to s })
    }
}

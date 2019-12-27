package ktaf.gui.core

import geometry.vec2
import geometry.vec2_zero
import ktaf.core.Window

class Exit(
        internal val position: ((vec2, vec2) -> vec2)?,
        internal val size: ((vec2, vec2) -> vec2)?
) {
    companion object {
        val none = Exit(null, null)
        val shrink = Exit({ p, s -> p + s / 2f }, { _, _ -> vec2_zero })

        fun toLeft()
                = Exit({ p, s -> vec2(-s.x, p.y) }, { _, s -> s })

        fun toRight(window: Window)
                = Exit({ p, _ -> vec2(window.width.value, p.y) }, { _, s -> s })

        fun toTop()
                = Exit({ p, s -> vec2(p.x, -s.y) }, { _, s -> s })

        fun toBottom(window: Window)
                = Exit({ p, s -> vec2(p.x, window.height.value) }, { _, s -> s })
    }
}

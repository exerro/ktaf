package ktaf.gui.experimental

import geometry.vec2
import geometry.vec2_zero
import ktaf.gui.core.Padding

class Positioning {
    var width: Float? = null
    var height: Float? = null
    var padding: Padding = Padding(0f)
    var expand: Boolean = false
    var contentWidth: Float? = null
    var contentHeight: Float? = null

    var calculatedPosition: vec2 = vec2_zero
    var calculatedWidth: Float = 0f
    var calculatedHeight: Float = 0f
}

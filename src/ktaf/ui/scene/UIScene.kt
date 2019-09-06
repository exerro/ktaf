package ktaf.ui.scene

import geometry.*
import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.ui.UIFocusEvent
import ktaf.ui.UIUnFocusEvent
import ktaf.ui.node.UIContainer
import ktaf.ui.node.UINode
import ktaf.ui.node.handleEvent
import ktaf.util.Animations

class UIScene(val display: Display, val context: DrawContext2D) {
    val root = KTAFValue<UIContainer?>(null)
    val focussedNode = KTAFValue<UINode?>(null)
    val animations = Animations()

    init {
        focussedNode.connectComparator { old, new ->
            old?.handleEvent(UIUnFocusEvent(new))
            new?.handleEvent(UIFocusEvent(old))
            old?.focused?.set(false)
            new?.focused?.set(true)
        }

        root.connectComparator { old, new ->
            old?.scene?.set(null)
            new?.scene?.set(this)
        }
    }

    internal var focussedNodeHover: UINode? = null
    internal var firstRelativeMouseLocation = vec2(0f)
    internal var lastRelativeMouseLocation = vec2(0f)
}

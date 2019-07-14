package ktaf.ui

import ktaf.core.*
import ktaf.ui.node.UINode

class UIMouseEnterEvent(val target: Boolean, position: vec2): MouseEvent(position)
class UIMouseExitEvent(val target: Boolean, position: vec2): MouseEvent(position)

class UIFocusEvent(val from: UINode?): Event()
class UIUnFocusEvent(val to: UINode?): Event()

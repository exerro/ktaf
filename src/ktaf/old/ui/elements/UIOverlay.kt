package ktaf.ui.elements

import ktaf.ui.layout.AreaLayout
import ktaf.ui.layout.LayoutValue
import ktaf.ui.layout.pc
import ktaf.ui.node.UIContainer
import ktaf.ui.node.UINode

open class UIOverlay<Node: UINode>(val primary: Node): UIContainer() {

    fun <N: UINode> createOverlay(node: N, overlayName: String = node.toString(),
                                  top: LayoutValue = 0.pc(), right: LayoutValue = 100.pc(),
                                  bottom: LayoutValue = 100.pc(), left: LayoutValue = 0.pc()): N {
        when (val l = layout.get()) { is AreaLayout -> {
            l.areas { area(overlayName) {
                this.top(top)
                this.bottom(bottom)
                this.left(left)
                this.right(right)
            } }
            l.elem(node, overlayName)
        } }

        children.add(node)

        return node
    }

    fun <N: UINode> createOverlay(node: N, overlayName: String = node.toString(),
                                  top: LayoutValue = 0.pc(), right: LayoutValue = 100.pc(),
                                  bottom: LayoutValue = 100.pc(), left: LayoutValue = 0.pc(),
                                  fn: N.() -> Unit)
            = fn(createOverlay(node, overlayName, top, right, bottom, left))

    init {
        layout(AreaLayout()) {
            areas {
                area("main") {
                    top(0.pc())
                    left(0.pc())
                    bottom(100.pc())
                    right(100.pc())
                }
            }

            elem(primary, "main")
        }

        children.add(primary)
    }
}

package ktaf.ui.elements

import ktaf.core.GLFWKey
import ktaf.core.GLFWKeyModifier
import ktaf.core.KTAFValue
import ktaf.core.vec2
import ktaf.typeclass.minus
import ktaf.ui.layout.ViewLayout
import ktaf.ui.layout.tl
import ktaf.ui.node.UINode

class UIView: UINode() {
    val viewLayout = KTAFValue(ViewLayout())
    val active = KTAFValue(null as UINode?)

    fun show(node: UINode) {
        active(node)
        viewLayout.get().location(node) ?.let { (x, y) -> viewLayout.get().location(x, y) }
    }

    fun vertical() {
        val added = children.connectAdded { viewLayout.get().location(it, 0, children.indexOf(it)) }
        val removed = children.connectRemoved { children.map { viewLayout.get().location(it, 0, children.indexOf(it)) } }

        children.map { viewLayout.get().location(it, 0, children.indexOf(it)) }
        functions.forEach { children.disconnectChanged(it) }
        functions = listOf(added, removed)
    }

    fun horizontal() {
        val added = children.connectAdded { viewLayout.get().location(it, children.indexOf(it), 0) }
        val removed = children.connectRemoved { children.map { viewLayout.get().location(it, children.indexOf(it), 0) } }

        children.map { viewLayout.get().location(it, children.indexOf(it), 0) }
        functions.forEach { children.disconnectChanged(it) }
        functions = listOf(added, removed)
    }

    override fun getMouseHandler(position: vec2): UINode?
            = active.get() ?.let { it.getMouseHandler(position - padding.get().tl - it.computedPosition.get()) }
            ?: this.takeIf { position.x >= 0 && position.y >= 0 && position.x < computedWidth.get() && position.y < computedHeight.get() }

    override fun getKeyboardHandler(key: GLFWKey, modifiers: Set<GLFWKeyModifier>): UINode?
            = active.get() ?.getKeyboardHandler(key, modifiers) ?: this.takeIf { handlesKey(key, modifiers) }

    override fun getInputHandler(): UINode?
            = active.get() ?.getInputHandler() ?: this.takeIf { handlesInput() }

    init {
        layout(viewLayout.get())
        viewLayout.connect { layout.set(it) }
        layout.connect { when (it) { is ViewLayout -> viewLayout.set(it) } }
        children.connectAdded { if (children.size == 1) show(it) }
        children.connectRemoved { if (active.get() == it) children.lastOrNull() ?.let { show(it) } }
    }

    private var functions = listOf<(UINode) -> Any?>()
}

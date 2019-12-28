package ktaf.gui.elements

import geometry.vec2
import ktaf.data.Value
import ktaf.graphics.DrawContext2D
import ktaf.gui.core.GUIBuilder
import ktaf.gui.core.GUIBuilderContext
import ktaf.gui.core.UIContainer
import ktaf.gui.core.UINode
import lwjglkt.glfw.CursorPosition
import lwjglkt.glfw.KeyEvent

fun <T> UIContainer.item(item: Value<T>, fn: GUIBuilderContext.(T) -> UINode)
        = addChild(Item(item) { fn(GUIBuilder, it) })

fun <T> GUIBuilderContext.item(item: Value<T>, fn: GUIBuilderContext.(T) -> UINode)
        = Item(item) { fn(GUIBuilder, it) }

//////////////////////////////////////////////////////////////////////////////////////////

class Item<T>(
        val item: Value<T>,
        private val fn: (T) -> UINode
): UINode() {
    override fun getDefaultWidth() = node.getDefaultWidth()
    override fun getDefaultHeight(width: Float) = node.getDefaultHeight(width)

    ////////////////////////////////////////////////////////////////////////////

    private var node: UINode

    init {
        node = fn(item.value)
        if (hasDrawContext()) node.setDrawContext(drawContext)

        item.onChangeEvent?.connect {
            node = fn(item.value)
            if (hasDrawContext()) node.setDrawContext(drawContext)
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    override fun getMouseHandler(position: CursorPosition): UINode?
            = node.getMouseHandler(position)

    override fun getKeyHandler(event: KeyEvent): UINode?
            = node.getKeyHandler(event)

    override fun getInputHandler(): UINode?
            = node.getInputHandler()

    override fun calculateWidth(availableWidth: Float) {
        val w = (width.value ?: availableWidth) - padding.value.width
        node.calculateWidth(w)
        super.calculateWidth(availableWidth)
    }

    override fun calculateHeight(availableHeight: Float?) {
        val h = height.value ?: availableHeight ?.let { it - padding.value.height }
        node.calculateHeight(h)
        super.calculateHeight(availableHeight)
    }

    override fun position(position: vec2) {
        super.position(position)
        node.position(calculatedPosition + padding.value.topLeft)
    }

    override fun setDrawContext(context: DrawContext2D) {
        super.setDrawContext(context)
        node.setDrawContext(context)
    }

    override fun draw() {
        node.draw()
    }

    override fun update(dt: Float) {
        super.update(dt)
        node.update(dt)
    }
}

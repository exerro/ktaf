package ktaf.gui.elements

import geometry.vec2
import ktaf.graphics.DrawContext2D
import ktaf.gui.core.Padding
import ktaf.gui.core.UINode
import ktaf.util.compareTo
import lwjglkt.glfw.CursorPosition
import lwjglkt.glfw.KeyEvent
import lwjglkt.glfw.MouseEvent
import lwjglkt.glfw.TextInputEvent

abstract class Proxy<N: UINode>(
        node: N
): UINode() {
    protected var node: N = node
        set(node) {
                field = node

                node.width <- width
                node.height <- height
                node.cursor <- cursor
                node.parented = true

                node.padding.value = Padding(0f)
                node.expand.value = true

                if (hasDrawContext()) node.setDrawContext(drawContext)
            }

    ////////////////////////////////////////////////////////////////////////////

    override fun entered() = node.entered()
    override fun exited() = node.exited()

    override fun handleMouseEvent(event: MouseEvent)
            = node.handleMouseEvent(event)

    override fun handleKeyEvent(event: KeyEvent)
            = node.handleKeyEvent(event)

    override fun handleInput(event: TextInputEvent)
            = node.handleInput(event)

    override fun getDefaultWidth()
            = node.getDefaultWidth()?.let { it + padding.value.width }

    override fun getDefaultHeight(width: Float)
            = node.getDefaultHeight(width)?.let { it + padding.value.height }

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
        val h = (height.value ?: availableHeight) ?.let { it - padding.value.height }
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

    ////////////////////////////////////////////////////////////////////////////

    init {
        this.node = node
    }
}

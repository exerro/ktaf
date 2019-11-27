package ktaf.gui.core

import geometry.vec2_zero
import ktaf.core.Window
import ktaf.graphics.DrawContext2D
import ktaf.data.property.vec2AnimatedProperty
import ktaf.util.compareTo
import lwjglkt.glfw.*

class UIScene<Root: UINode>(
        root: Root,
        val window: Window
): Positioner {
    var root: Root = root
        private set

    fun update(dt: Float) {
        val mouseTarget = root.getMouseHandler(mousePosition)

        if (mouseTarget != hoveringNode) {
            hoveringNode?.exited()
            mouseTarget?.entered()
            hoveringNode = mouseTarget
        }

        window.glfwWindow.setCursor(mouseTarget?.cursor?.value ?: GLFWCursor.DEFAULT)

        calculateChildrenWidths(drawContext.viewportSize.value.x)
        calculateChildrenHeights(drawContext.viewportSize.value.y)
        positionChildren()
        root.update(dt)
    }

    fun draw() {
        drawContext.begin()
        root.draw()
        drawContext.end()
    }

    fun mousePressed(event: MousePressEvent) {
        if (currentNode == null) {
            currentNode = root.getMouseHandler(event.position)
            pressedButton = event.button.takeIf { currentNode != null }
        }

        currentNode?.handleMouseEvent(event)
    }

    fun mouseReleased(event: MouseReleaseEvent) {
        currentNode?.handleMouseEvent(event)
        mousePosition = event.position

        if (pressedButton == event.button) {
            currentNode = null
            pressedButton = null
        }
    }

    fun mouseClicked(event: MouseClickEvent) {
        currentNode?.handleMouseEvent(event)
    }

    fun attach() {
        // this is important to stop the scene from being garbage collected
        // if the scene is garbage collected then the callbacks below will be
        // removed and everything will disappear
        val scene = this

        window.draw.subscribe(this) { scene.draw() }
        window.update.subscribe(this) { scene.update(it) }
        window.events.mousePressed.subscribe(this) { scene.mousePressed(it) }
        window.events.mouseReleased.subscribe(this) { scene.mouseReleased(it) }
        window.events.mouseClicked.subscribe(this) { scene.mouseClicked(it) }
        window.events.mouseMoved.subscribe(this) { scene.mousePosition = it.position }

        mousePosition = window.glfwWindow.cursorPosition
    }

    fun detach() {
        window.draw.unsubscribe(this)
        window.update.unsubscribe(this)
        window.events.mousePressed.unsubscribe(this)
        window.events.mouseReleased.unsubscribe(this)
        window.events.mouseClicked.unsubscribe(this)
        window.events.mouseMoved.unsubscribe(this)
    }

    ////////////////////////////////////////////////////////////////////////////

    override fun calculateChildrenWidths(availableWidth: Float) {
        root.calculateWidth(availableWidth)
    }

    override fun calculateChildrenHeights(availableHeight: Float?) {
        root.calculateHeight(availableHeight)
    }

    override fun positionChildren() {
        root.position(vec2_zero)
    }

    ////////////////////////////////////////////////////////////////////////////

    private var currentNode: UINode? = null
    private var pressedButton: GLFWMouseButton? = null
    private var hoveringNode: UINode? = null
    private var mousePosition: CursorPosition = CursorPosition(0f, 0f)
    private val drawContext = window.drawContext2D

    init {
        root.setDrawContext(drawContext)
    }
}

open class SceneBuilderContext<R: UINode>(
        private val window: Window
) : GUIBuilderContext {
    lateinit var root: R

    internal fun create(): UIScene<R> {
        if (!::root.isInitialized) error("No root provided to scene")
        return UIScene(root, window)
    }
}

fun <R: UINode> scene(window: Window, fn: SceneBuilderContext<R>.() -> Unit): UIScene<R>
        = SceneBuilderContext<R>(window).also(fn).create()

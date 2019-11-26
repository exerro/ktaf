package ktaf.gui.core

import geometry.vec2_zero
import ktaf.core.Window
import ktaf.graphics.DrawContext2D
import ktaf.data.property.vec2AnimatedProperty
import ktaf.util.compareTo

class UIScene<Root: UINode>(
        root: Root,
        val drawContext: DrawContext2D
): Positioner {
    var root: Root = root
        private set

    fun update(dt: Float) {
        calculateChildrenWidths(drawContext.viewportSize.value.x)
        calculateChildrenHeights(drawContext.viewportSize.value.y)
        positionChildren()
        root.update(dt)
    }

    fun draw() {
        drawContext.begin()
        root.draw(drawContext)
        drawContext.end()
    }

    fun attach(window: Window) {
        window.draw.connect(this::draw)
        window.update.connect(this::update)
    }

    override fun calculateChildrenWidths(availableWidth: Float) {
        root.calculateWidth(availableWidth)
    }

    override fun calculateChildrenHeights(availableHeight: Float?) {
        root.calculateHeight(availableHeight)
    }

    override fun positionChildren() {
        root.position(vec2_zero)
    }
}

open class SceneBuilderContext<R: UINode>(
        private val drawContext: DrawContext2D
) : GUIBuilderContext {
    lateinit var root: R

    internal fun create(): UIScene<R> {
        if (!::root.isInitialized) error("No root provided to scene")
        return UIScene(root, drawContext)
    }
}

fun <R: UINode> scene(drawContext: DrawContext2D, fn: SceneBuilderContext<R>.() -> Unit): UIScene<R>
        = SceneBuilderContext<R>(drawContext).also(fn).create()

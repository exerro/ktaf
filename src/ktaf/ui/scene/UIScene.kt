package ktaf.ui.scene

import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.typeclass.Animateable
import ktaf.typeclass.minus
import ktaf.typeclass.transitionTo
import ktaf.ui.*
import ktaf.ui.layout.computeWidthInternal
import ktaf.ui.layout.positionChildrenInternal
import ktaf.ui.node.UINode
import ktaf.ui.node.absolutePosition
import ktaf.util.Animation
import ktaf.util.Easing
import ktaf.util.EasingFunction
import lwjglkt.GLFWCursor
import lwjglkt.GLFWDisplay
import lwjglkt.setCursor
import kotlin.reflect.KProperty0

class UIScene(val display: GLFWDisplay, val context: DrawContext2D) {
    internal var focussedNodeHover: UINode? = null
    internal var firstRelativeMouseLocation = vec2(0f)
    internal var lastRelativeMouseLocation = vec2(0f)
    internal var mouseModifiers = setOf<GLFWMouseModifier>()

    internal val animations: MutableMap<Any, MutableMap<String, Animation<*>>> = mutableMapOf()
    val root = KTAFMutableValue<UINode?>(null)
    val focussedNode = KTAFMutableValue<UINode?>(null)

    init {
        focussedNode.connectComparator { old, new ->
            old?.handleEvent(UIUnFocusEvent(new))
            new?.handleEvent(UIFocusEvent(old))
        }

        root.connectComparator { old, new ->
            old?.scene?.set(null)
            new?.scene?.set(this)
        }
    }
}

fun UIScene.update(dt: Float) {
    for ((_, m) in animations) for ((_, animation) in m)
        animation.update(dt)

    animations.map { (node, m) -> node to m.filterValues { !it.finished() } }

    root.get()?.let {
        it.computeWidthInternal(context.viewport.width().toFloat())
        it.positionChildrenInternal(context.viewport.height().toFloat())
        it.update(dt)
        it.computedXCachedSetter = 0f
        it.computedYCachedSetter = 0f
    }
}

fun UIScene.draw() {
    display.setCursor(focussedNodeHover?.cursor ?: GLFWCursor.DEFAULT)

    root.get()?.let {
        it.draw(context, it.margin.get().tl,
                vec2(
                        context.viewport.width().toFloat() - it.margin.get().width,
                        context.viewport.height().toFloat() - it.margin.get().height
                )
        )
    }
}

fun UIScene.mousePressed(button: GLFWMouseButton, position: vec2, modifiers: Set<GLFWMouseModifier>) {
    root.get() ?.let { root ->
        root.getMouseHandler(position - root.computedPosition.get()) ?.let { target ->
            target.handleEvent(UIMousePressEvent(position - target.absolutePosition(), button, modifiers))
            focussedNode(target)
            firstRelativeMouseLocation = position - target.absolutePosition()
        }

        lastRelativeMouseLocation = firstRelativeMouseLocation
        mouseModifiers = modifiers
    }
}

fun UIScene.mouseReleased(button: GLFWMouseButton, position: vec2, modifiers: Set<GLFWMouseModifier>) {
    focussedNode.get() ?.let { node ->
        node.handleEvent(UIMouseReleaseEvent(position - node.absolutePosition(), button, modifiers))
        node.handleEvent(UIMouseClickEvent(position - node.absolutePosition(), button, modifiers)) // TODO: maybe do a bounds check?
    }
}

fun UIScene.mouseMoved(position: vec2, last: vec2) {
    val previousFocussedNode = focussedNodeHover

    root.get() ?.let { root ->
        val currentFocussedNode = root.getMouseHandler(position - root.computedPosition.get())

        currentFocussedNode?.handleEvent(UIMouseMoveEvent(
                position - currentFocussedNode.absolutePosition(),
                last - currentFocussedNode.absolutePosition()
        ))

        if (previousFocussedNode != currentFocussedNode) {
            focussedNodeHover = currentFocussedNode
            previousFocussedNode?.handleEvent(UIMouseExitEvent(position - previousFocussedNode.absolutePosition()))
            currentFocussedNode?.handleEvent(UIMouseEnterEvent(position - currentFocussedNode.absolutePosition()))
        }
    }
}

fun UIScene.mouseDragged(position: vec2) {
    focussedNode.get() ?.let { node ->
        node.handleEvent(UIMouseDragEvent(position, lastRelativeMouseLocation, firstRelativeMouseLocation, mouseModifiers))
        lastRelativeMouseLocation = position - node.absolutePosition()
    }
}

fun UIScene.attachCallbacks(app: Application) {
    val scene = this

    app.update { dt -> scene.update(dt) }
    app.draw { scene.draw() }

    app.onMousePressed { button, x, y, modifiers ->
        scene.mousePressed(button, vec2(x.toFloat(), y.toFloat()), modifiers)
    }

    app.onMouseReleased { button, x, y, modifiers ->
        scene.mouseReleased(button, vec2(x.toFloat(), y.toFloat()), modifiers)
    }

    app.onMouseMoved { x, y, lx, ly ->
        scene.mouseMoved(vec2(x.toFloat(), y.toFloat()), vec2(lx.toFloat(), ly.toFloat()))
    }

    app.onMouseDragged { x, y, _, _, _, _, _ ->
        scene.mouseDragged(vec2(x.toFloat(), y.toFloat()))
    }
}

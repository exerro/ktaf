package ktaf.ui

import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.typeclass.Animateable
import ktaf.typeclass.minus
import ktaf.typeclass.transitionTo
import ktaf.ui.layout.computeWidthInternal
import ktaf.ui.layout.positionChildrenInternal
import ktaf.util.Animation
import ktaf.util.Easing
import ktaf.util.EasingFunction
import lwjglkt.GLFWCursor
import lwjglkt.GLFWDisplay
import lwjglkt.setCursor
import kotlin.reflect.KProperty0

class UIScene(val display: GLFWDisplay, val context: DrawContext2D) {
    internal val focussedNodeHover = KTAFMutableValue<UINode?>(null)
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

fun <T: Animateable<T>> UIScene.animate(owner: Any, property: KProperty0<KTAFMutableValue<T>>, to: T, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH)
        = animate(owner, property.name, property.get(), to, duration, easing)

fun <T: Animateable<T>> UIScene.animate(owner: Any, property: String, value: KTAFMutableValue<T>, to: T, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH) {
    animations.computeIfAbsent(owner) { mutableMapOf() } [property] = Animation(
            value.get(),
            to,
            duration,
            easing,
            { a, b, t -> a.transitionTo(b, t) },
            value::setValue
    )
}

fun <T: Animateable<T>> UIScene.animateNullable(owner: Any, property: KProperty0<KTAFMutableValue<T?>>, to: T?, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH)
        = animateNullable(owner, property.name, property.get(), to, duration, easing)

fun <T: Animateable<T>> UIScene.animateNullable(owner: Any, property: String, value: KTAFMutableValue<T?>, to: T?, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH) {
    val prev = value.get()

    if (prev == null || to == null) {
        value.setValue(to)
        return
    }

    animations.computeIfAbsent(owner) { mutableMapOf() } [property] = Animation(
            prev,
            to,
            duration,
            easing,
            { a, b, t -> a.transitionTo(b, t) },
            value::setValue
    )
}

fun UIScene.animate(owner: Any, property: KProperty0<KTAFMutableValue<Float>>, to: Float, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH)
        = animate(owner, property.name, property.get(), to, duration, easing)

fun UIScene.animate(owner: Any, property: String, value: KTAFMutableValue<Float>, to: Float, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH) {
    animations.computeIfAbsent(owner) { mutableMapOf() } [property] = Animation(
            value.get(),
            to,
            duration,
            easing,
            { a, b, t -> a + (b - a) * t },
            value::setValue
    )
}

fun UIScene.animateNullable(owner: Any, property: KProperty0<KTAFMutableValue<Float?>>, to: Float?, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH)
        = animateNullable(owner, property.name, property.get(), to, duration, easing)

fun UIScene.animateNullable(owner: Any, property: String, value: KTAFMutableValue<Float?>, to: Float?, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH) {
    val prev = value.get()

    if (prev == null || to == null) {
        value.setValue(to)
        return
    }

    animations.computeIfAbsent(owner) { mutableMapOf() } [property] = Animation(
            prev,
            to,
            duration,
            easing,
            { a, b, t -> a + (b - a) * t },
            value::setValue
    )
}

fun <N: UINode, T> UIScene.cancelAnimation(node: N, property: String) {
    animations.computeIfAbsent(node) { mutableMapOf() } .remove(property)
}

fun <N: UINode> UIScene.setRoot(root: N, init: N.() -> Unit = {}): N {
    init(root)
    this.root.set(root)
    return root
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
    display.setCursor(focussedNodeHover.get()?.cursor ?: GLFWCursor.DEFAULT)

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
            focussedNode.set(target)
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
    val previousFocussedNode = focussedNodeHover.get()

    root.get() ?.let { root ->
        val currentFocussedNode = root.getMouseHandler(position - root.computedPosition.get())

        currentFocussedNode?.handleEvent(UIMouseMoveEvent(
                position - currentFocussedNode.absolutePosition(),
                last - currentFocussedNode.absolutePosition()
        ))

        if (previousFocussedNode != currentFocussedNode) {
            focussedNodeHover.set(currentFocussedNode)
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

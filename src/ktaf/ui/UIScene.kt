package ktaf.ui

import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.ui.layout.computeWidthInternal
import ktaf.ui.layout.positionChildrenInternal
import lwjglkt.GLFWCursor
import lwjglkt.GLFWDisplay
import lwjglkt.setCursor
import ktaf.util.Animation

class UIScene(val display: GLFWDisplay, val context: DrawContext2D) {
    internal var focussedNode: UINode? = null
    internal var firstRelativeMouseLocation = vec2(0f)
    internal var lastRelativeMouseLocation = vec2(0f)
    internal var mouseModifiers = setOf<GLFWMouseModifier>()
    internal val animations: MutableMap<UINode, MutableMap<UIProperty<UINode, *>, Animation<*>>> = mutableMapOf()
    internal val animations2: MutableMap<UINode, MutableMap<String, Animation<*>>> = mutableMapOf()
    internal val rootsInternal = mutableListOf<UINode>()

    val roots get() = rootsInternal.toList()
}

fun <N: UINode, T> UIScene.animate2(node: N, property: String, animation: Animation<T>) {
    animations2.computeIfAbsent(node) { mutableMapOf() } [property] = animation
}

fun <N: UINode, T> UIScene.animateNullable2(node: N, property: String, animation: Animation<T>) {
    animations2.computeIfAbsent(node) { mutableMapOf() } [property] = animation
}

fun <N: UINode, T> UIScene.cancelAnimation2(node: N, property: String) {
    animations2.computeIfAbsent(node) { mutableMapOf() } .remove(property)
}

fun <N: UINode, T> UIScene.animate(node: N, property: UIProperty<N, T>, animation: Animation<T>) {
    animations.computeIfAbsent(node) { mutableMapOf() } [property] = animation
}

fun <N: UINode, T> UIScene.animateNullable(node: N, property: UIProperty<N, T?>, animation: Animation<T>) {
    animations.computeIfAbsent(node) { mutableMapOf() } [property] = animation
}

fun <N: UINode, T> UIScene.cancelAnimation(node: N, property: UIProperty<N, T>) {
    animations.computeIfAbsent(node) { mutableMapOf() } .remove(property)
}

fun UIScene.focusOn(node: UINode) {
    if (focussedNode == node) return
    val event = UIFocusEvent(null, focussedNode)
    focussedNode?.handleEvent(UIUnFocusEvent(null, node))
    focussedNode = node
    node.handleEvent(event)
}

fun UIScene.unfocus() {
    focussedNode?.handleEvent(UIUnFocusEvent(null, null))
    focussedNode = null
}

fun <N: UINode> UIScene.addRoot(root: N, init: N.() -> Unit = {}): N {
    init(root)
    rootsInternal.add(root)
    generateSequence(listOf(root as UINode)) { nodes ->
        nodes.flatMap { it.children } .takeIf { it.isNotEmpty() }
    } .flatten() .forEach { it.sceneInternal = this }
    return root
}

fun UIScene.removeRoot(root: UINode) {
    if (rootsInternal.contains(root)) {
        rootsInternal.remove(root)
        root.sceneInternal = null
    }
}

fun UIScene.update(dt: Float) {
    for ((_, m) in animations) for ((_, animation) in m)
        animation.update(dt)

    animations.map { (node, m) -> node to m.filterValues { !it.finished() } }

    for ((_, m) in animations2) for ((_, animation) in m)
        animation.update(dt)

    animations2.map { (node, m) -> node to m.filterValues { !it.finished() } }

    rootsInternal.forEach {
        it.computeWidthInternal(context.viewport.width().toFloat())
        it.positionChildrenInternal(context.viewport.height().toFloat())
        it.update(dt)
        it.computedXInternal = 0f
        it.computedYInternal = 0f
    }
}

fun UIScene.draw() {
    rootsInternal.forEach {
        it.draw(context, vec2(it.margin.left, it.margin.top),
                vec2(
                        context.viewport.width().toFloat() - it.margin.width,
                        context.viewport.height().toFloat() - it.margin.height
                )
        )
    }
}

fun UIScene.mousePressed(button: GLFWMouseButton, position: vec2, modifiers: Set<GLFWMouseModifier>) {
    val event = UIMousePressEvent(null, null, position, button, modifiers)
    rootsInternal.forEach { it.handleEvent(event.relativeTo(vec2(it.computedXInternal, it.computedYInternal))) }
    event.handler ?. let { node ->
        focusOn(node)
        firstRelativeMouseLocation = position - node.absolutePosition()
        lastRelativeMouseLocation = firstRelativeMouseLocation
        mouseModifiers = modifiers
    }
}

fun UIScene.mouseReleased(button: GLFWMouseButton, position: vec2, modifiers: Set<GLFWMouseModifier>) {
    focussedNode ?.let { node ->
        val click = UIMouseClickEvent(null, null, position - node.absolutePosition(), button, modifiers)
        node.handleEvent(UIMouseReleaseEvent(null, null, position - node.absolutePosition(), button, modifiers))
        if (click.within(node)) node.handleEvent(click)
    }
}

fun UIScene.mouseMoved(position: vec2, last: vec2) {
    val allChildren = generateSequence(rootsInternal.toList()) { nodes -> nodes.flatMap { n -> n.childrenInternal.reversed() } .takeIf { it.isNotEmpty() } } .flatten()
    val event = UIMouseMoveEvent(null, null, position, last)
    val enter = UIMouseEnterEvent(null, null, position)
    val exit = UIMouseExitEvent(null, null, position)
    rootsInternal.forEach { it.handleEvent(event.relativeTo(vec2(it.computedXInternal, it.computedYInternal))) }
    val (inside, outside) = allChildren.partition { event.relativeTo(it.absolutePosition()).within(it) }
    outside.filter { it.mouseInside } .forEach { it.handleEvent(exit.relativeTo(it.absolutePosition())); it.mouseInside = false }
    inside.filter { !it.mouseInside } .forEach { it.handleEvent(enter.relativeTo(it.absolutePosition())); it.mouseInside = true }
    inside.map { it.cursor } .firstOrNull { it != null } ?.let { display.setCursor(it) } ?: display.setCursor(GLFWCursor.DEFAULT)
}

fun UIScene.mouseDragged(position: vec2) {
    focussedNode ?.let { node ->
        node.handleEvent(UIMouseDragEvent(null, null, position, lastRelativeMouseLocation, firstRelativeMouseLocation, mouseModifiers))
        lastRelativeMouseLocation = position - node.absolutePosition()
    }
}

fun UIScene.attachCallbacks(app: Application) {
    val scene = this

    app.update { dt ->
        scene.update(dt)
    }

    app.draw {
        scene.draw()
    }

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

package ui

import core.*
import graphics.DrawContext2D

class UIScene(val context: DrawContext2D) {
    internal var focussedNode: UINode? = null
    internal var firstRelativeMouseLocation = vec2(0f)
    internal var lastRelativeMouseLocation = vec2(0f)
    internal var mouseModifiers = setOf<GLFWMouseModifier>()
    val roots = mutableListOf<UINode>()
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

fun UIScene.addRoot(root: UINode) {
    roots.add(root)
    root.scene = this
}

fun UIScene.update(dt: Float) {
    roots.forEach {
        it.computeWidthInternal(context.viewport.width().toFloat())
        it.positionChildrenInternal(context.viewport.height().toFloat())
        it.update(dt)
        it.computedX = 0f
        it.computedY = 0f
    }
}

fun UIScene.draw() {
    roots.forEach {
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
    roots.forEach { it.handleEvent(event.relativeTo(vec2(it.computedX, it.computedY))) }
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
    val allChildren = generateSequence(roots.toList()) { nodes -> nodes.flatMap { n -> n.childrenInternal } .takeIf { it.isNotEmpty() } } .flatten()
    val event = UIMouseMoveEvent(null, null, position, last)
    val enter = UIMouseEnterEvent(null, null, position)
    val exit = UIMouseExitEvent(null, null, position)
    roots.forEach { it.handleEvent(event.relativeTo(vec2(it.computedX, it.computedY))) }
    val (inside, outside) = allChildren.partition { event.relativeTo(it.absolutePosition()).within(it) }
    inside.filter { !it.mouseInside } .forEach { it.handleEvent(enter.relativeTo(it.absolutePosition())); it.mouseInside = true }
    outside.filter { it.mouseInside } .forEach { it.handleEvent(exit.relativeTo(it.absolutePosition())); it.mouseInside = false }
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

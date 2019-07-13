package ktaf.ui.scene

import ktaf.core.*
import ktaf.typeclass.minus
import ktaf.ui.*
import ktaf.ui.layout.*
import ktaf.ui.node.absolutePosition
import ktaf.ui.node.handleEvent
import ktaf.util.update
import lwjglkt.GLFWCursor
import lwjglkt.setCursor

fun UIScene.update(dt: Float) {
    animations.update(dt)

    root.get()?.let {
        it.layout.get().beginPositioning(it)
        it.computeWidth(context.viewport.width().toFloat())
        it.computeHeight(context.viewport.height().toFloat())
        it.computePositionForChildren()
        it.layout.get().finishPositioning(it)

        it.update(dt)

        it.computedXInternal = 0f
        it.computedYInternal = 0f
    }
}

fun UIScene.draw() {
    display.setCursor(focussedNodeHover?.cursor() ?: GLFWCursor.DEFAULT)

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
            val inside = generateSequence(currentFocussedNode?.parent?.get()) { it.parent.get() }
            val oldParents = generateSequence(previousFocussedNode?.parent?.get()) { it.parent.get() }

            focussedNodeHover = currentFocussedNode
            previousFocussedNode?.handleEvent(UIMouseExitEvent(true, position - previousFocussedNode.absolutePosition()))
            (oldParents - inside - previousFocussedNode).forEach { it?.handleEvent(UIMouseExitEvent(false, position - it.absolutePosition())) }
            currentFocussedNode?.handleEvent(UIMouseEnterEvent(true, position - currentFocussedNode.absolutePosition()))
            (inside - oldParents - currentFocussedNode).forEach { it?.handleEvent(UIMouseEnterEvent(false, position - it.absolutePosition())) }
        }
    }
}

fun UIScene.mouseDragged(position: vec2) {
    focussedNode.get() ?.let { node ->
        node.handleEvent(UIMouseDragEvent(position - node.absolutePosition(), lastRelativeMouseLocation, firstRelativeMouseLocation, mouseModifiers))
        lastRelativeMouseLocation = position - node.absolutePosition()
    }
}

fun UIScene.keyPressed(key: GLFWKey, modifiers: Set<GLFWKeyModifier>) {
    keyboardTarget(key, modifiers) ?.handleEvent(UIKeyPressEvent(key, modifiers))
}

fun UIScene.keyReleased(key: GLFWKey, modifiers: Set<GLFWKeyModifier>) {
    keyboardTarget(key, modifiers) ?.handleEvent(UIKeyReleaseEvent(key, modifiers))
}

fun UIScene.textInput(text: String) {
    inputTarget() ?.handleEvent(UITextInputEvent(text))
}

private fun UIScene.keyboardTarget(key: GLFWKey, modifiers: Set<GLFWKeyModifier>)
        = focussedNode.get() ?.takeIf { it.getKeyboardHandler(key, modifiers) == it } ?: root.get() ?.getKeyboardHandler(key, modifiers)

private fun UIScene.inputTarget()
        = focussedNode.get() ?.takeIf { it.getInputHandler() == it } ?: root.get() ?.getInputHandler()

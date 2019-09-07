package ktaf.ui.scene

import geometry.*
import ktaf.core.*
import ktaf.ui.*
import ktaf.ui.layout.*
import ktaf.ui.node.absolutePosition
import ktaf.ui.node.handleEvent
import ktaf.util.update
import lwjglkt.*
import lwjglkt.glfw.GLFWCursor

fun UIScene.update(dt: Float) {
    animations.update(dt)

    root.get()?.let {
        it.layout.get().beginPositioning(it)
        // TODO: replace with framebuffer size
        it.computeWidth(display.glfwWindow.size.width.toFloat())
        it.computeHeight(display.glfwWindow.size.height.toFloat())
        it.computePositionForChildren()
        it.layout.get().finishPositioning(it)

        it.update(dt)

        it.computedX = 0f
        it.computedY = 0f
    }
}

fun UIScene.draw() {
    display.setCursor(focussedNodeHover?.cursor() ?: GLFWCursor.DEFAULT)

    GL.enable(GLOption.GL_BLEND)

    rasterState {
        defaults()
    }

    postFragmentShaderState {
        defaults()
        blendFunction(GLBLendFunction.GL_SRC_ALPHA, GLBLendFunction.GL_ONE_MINUS_SRC_ALPHA)
    }

    root.get()?.let {
        it.draw(context, it.margin.get().tl,
                vec2(
                        // TODO: replace with framebuffer size
                        display.glfwWindow.size.width.toFloat() - it.margin.get().width,
                        display.glfwWindow.size.height.toFloat() - it.margin.get().height
                )
        )
    }
}

fun UIScene.mousePressed(event: MousePressEvent) {
    root.get() ?.let { root ->
        root.getMouseHandler(event.position - root.computedPosition.get()) ?.let { target ->
            target.handleEvent(MousePressEvent(event.position - target.absolutePosition(), event.button, event.modifiers))
            focussedNode(target)
            firstRelativeMouseLocation = event.position - target.absolutePosition()
        }

        lastRelativeMouseLocation = firstRelativeMouseLocation
    }
}

fun UIScene.mouseReleased(event: MouseReleaseEvent) {
    focussedNode.get() ?.let { node ->
        node.handleEvent(MouseReleaseEvent(event.position - node.absolutePosition(), event.button, event.modifiers))
        node.handleEvent(MouseClickEvent(event.position - node.absolutePosition(), event.button, event.modifiers)) // TODO: maybe do a bounds check?
    }
}

fun UIScene.mouseScrolled(event: MouseScrollEvent) {
    root.get() ?.let { root ->
        val parents = generateSequence(root.getMouseHandler(event.position - root.computedPosition.get())) { it.parent.get() }
        parents.firstOrNull { it.handlesScroll() } ?.handleEvent(event)
    }
}

fun UIScene.mouseMoved(event: MouseMoveEvent) {
    val previousFocussedNode = focussedNodeHover

    root.get() ?.let { root ->
        val currentFocussedNode = root.getMouseHandler(event.position - root.computedPosition.get())

        currentFocussedNode?.handleEvent(MouseMoveEvent(
                event.position - currentFocussedNode.absolutePosition(),
                event.lastPosition - currentFocussedNode.absolutePosition()
        ))

        if (previousFocussedNode != currentFocussedNode) {
            val inside = generateSequence(currentFocussedNode?.parent?.get()) { it.parent.get() }
            val oldParents = generateSequence(previousFocussedNode?.parent?.get()) { it.parent.get() }

            focussedNodeHover = currentFocussedNode
            previousFocussedNode?.handleEvent(UIMouseExitEvent(true, event.position - previousFocussedNode.absolutePosition()))
            (oldParents - inside - previousFocussedNode).forEach { it?.handleEvent(UIMouseExitEvent(false, event.position - it.absolutePosition())) }
            currentFocussedNode?.handleEvent(UIMouseEnterEvent(true, event.position - currentFocussedNode.absolutePosition()))
            (inside - oldParents - currentFocussedNode).forEach { it?.handleEvent(UIMouseEnterEvent(false, event.position - it.absolutePosition())) }
        }
    }
}

fun UIScene.mouseDragged(event: MouseDragEvent) {
    focussedNode.get() ?.let { node ->
        node.handleEvent(MouseDragEvent(event.position - node.absolutePosition(), lastRelativeMouseLocation, firstRelativeMouseLocation, event.buttons, event.modifiers))
        lastRelativeMouseLocation = event.position - node.absolutePosition()
    }
}

fun UIScene.keyPressed(event: KeyPressEvent) {
    keyboardTarget(event.key, event.modifiers) ?.handleEvent(event)
}

fun UIScene.keyReleased(event: KeyReleaseEvent) {
    keyboardTarget(event.key, event.modifiers) ?.handleEvent(event)
}

fun UIScene.textInput(event: TextInputEvent) {
    inputTarget() ?.handleEvent(event)
}

private fun UIScene.keyboardTarget(key: GLFWKey, modifiers: Set<GLFWKeyModifier>)
        = focussedNode.get() ?.takeIf { it.getKeyboardHandler(key, modifiers) == it } ?: root.get() ?.getKeyboardHandler(key, modifiers)

private fun UIScene.inputTarget()
        = focussedNode.get() ?.takeIf { it.getInputHandler() == it } ?: root.get() ?.getInputHandler()

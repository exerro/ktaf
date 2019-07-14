package ktaf.ui.node

import ktaf.core.vec2
import ktaf.typeclass.plus
import ktaf.ui.*
import ktaf.ui.layout.height
import ktaf.ui.layout.tl
import ktaf.ui.layout.width

fun UINode.absolutePosition(): vec2
        = (parent.get()?.absolutePosition() ?: vec2(0f)) + (parent.get()?.padding?.get()?.tl ?: vec2(0f)) + vec2(computedX.get(), computedY.get())

fun UINode.handleEvent(event: UIEvent) {
    when (event) {
        is UIKeyEvent -> onKeyEvent.trigger(event)
        is UIMouseEvent -> onMouseEvent.trigger(event)
    }

    when (event) {
        is UIMouseButtonEvent -> onMouseButtonEvent.trigger(event)
    }

    when (event) {
        is UIMouseEnterEvent -> onMouseEnter.trigger(event)
        is UIMouseExitEvent -> onMouseExit.trigger(event)
        is UIMouseMoveEvent -> onMouseMove.trigger(event)
        is UIMouseDragEvent -> onMouseDrag.trigger(event)
        is UIMousePressEvent -> onMousePress.trigger(event)
        is UIMouseReleaseEvent -> onMouseRelease.trigger(event)
        is UIMouseClickEvent -> onMouseClick.trigger(event)
        is UIKeyPressEvent -> onKeyPress.trigger(event)
        is UIKeyReleaseEvent -> onKeyRelease.trigger(event)
        is UITextInputEvent -> onTextInput.trigger(event)
        is UIFocusEvent -> onFocus.trigger(event)
        is UIUnFocusEvent -> onUnFocus.trigger(event)
    }
}

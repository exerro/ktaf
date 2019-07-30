package ktaf.ui.node

import ktaf.core.*
import ktaf.typeclass.plus
import ktaf.ui.UIFocusEvent
import ktaf.ui.UIMouseEnterEvent
import ktaf.ui.UIMouseExitEvent
import ktaf.ui.UIUnFocusEvent
import ktaf.ui.layout.tl

fun UINode.absolutePosition(): vec2
        = (parent.get()?.absolutePosition() ?: vec2(0f)) + (parent.get()?.padding?.get()?.tl ?: vec2(0f)) + vec2(currentComputedX.get(), currentComputedY.get())

fun UINode.handleEvent(event: Event) {
    when (event) {
        is KeyEvent -> onKeyEvent.trigger(event)
        is MouseEvent -> onMouseEvent.trigger(event)
    }

    when (event) {
        is MouseButtonEvent -> onMouseButtonEvent.trigger(event)
    }

    when (event) {
        is UIMouseEnterEvent -> onMouseEnter.trigger(event)
        is UIMouseExitEvent -> onMouseExit.trigger(event)
        is MouseScrollEvent -> onMouseScroll.trigger(event)
        is MouseMoveEvent -> onMouseMove.trigger(event)
        is MouseDragEvent -> onMouseDrag.trigger(event)
        is MousePressEvent -> onMousePress.trigger(event)
        is MouseReleaseEvent -> onMouseRelease.trigger(event)
        is MouseClickEvent -> onMouseClick.trigger(event)
        is KeyPressEvent -> onKeyPress.trigger(event)
        is KeyReleaseEvent -> onKeyRelease.trigger(event)
        is TextInputEvent -> onTextInput.trigger(event)
        is UIFocusEvent -> onFocus.trigger(event)
        is UIUnFocusEvent -> onUnFocus.trigger(event)
    }
}

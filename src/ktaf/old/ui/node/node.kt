package ktaf.ui.node

import geometry.plus
import geometry.vec2
import ktaf.core.*
import ktaf.ui.UIFocusEvent
import ktaf.ui.UIMouseEnterEvent
import ktaf.ui.UIMouseExitEvent
import ktaf.ui.UIUnFocusEvent
import ktaf.ui.layout.tl

fun UINode.absolutePosition(): vec2
        = (parent.get()?.absolutePosition() ?: vec2(0f)) + (parent.get()?.padding?.get()?.tl ?: vec2(0f)) + vec2(currentComputedX.get(), currentComputedY.get())

fun UINode.handleEvent(event: Event) {
    when (event) {
        is KeyEvent -> onKeyEvent.emit(event)
        is MouseEvent -> onMouseEvent.emit(event)
    }

    when (event) {
        is MouseButtonEvent -> onMouseButtonEvent.emit(event)
    }

    when (event) {
        is UIMouseEnterEvent -> onMouseEnter.emit(event)
        is UIMouseExitEvent -> onMouseExit.emit(event)
        is MouseScrollEvent -> onMouseScroll.emit(event)
        is MouseMoveEvent -> onMouseMove.emit(event)
        is MouseDragEvent -> onMouseDrag.emit(event)
        is MousePressEvent -> onMousePress.emit(event)
        is MouseReleaseEvent -> onMouseRelease.emit(event)
        is MouseClickEvent -> onMouseClick.emit(event)
        is KeyPressEvent -> onKeyPress.emit(event)
        is KeyReleaseEvent -> onKeyRelease.emit(event)
        is TextInputEvent -> onTextInput.emit(event)
        is UIFocusEvent -> onFocus.emit(event)
        is UIUnFocusEvent -> onUnFocus.emit(event)
    }
}

package ktaf.ui

fun <N: UINode> N.onFocus(fn: N.(UIFocusEvent) -> Unit) { focusEventHandlers.add { fn(this, it) } }
fun <N: UINode> N.onFocusLost(fn: N.(UIUnFocusEvent) -> Unit) { unFocusEventHandlers.add { fn(this, it) } }

fun <N: UINode> N.onKeyEvent(fn: N.(UIKeyEvent) -> Unit) { keyEventHandlers.add { fn(this, it) } }
fun <N: UINode> N.onKeyPress(fn: N.(UIKeyPressEvent) -> Unit) { keyPressEventHandlers.add { fn(this, it) } }
fun <N: UINode> N.onKeyRelease(fn: N.(UIKeyReleaseEvent) -> Unit) { keyReleaseEventHandlers.add { fn(this, it) } }

fun <N: UINode> N.onTextInput(fn: N.(UITextInputEvent) -> Unit) { textInputEventHandlers.add { fn(this, it) } }

fun <N: UINode> N.onMouseEvent(fn: N.(UIMouseEvent) -> Unit) { mouseEventHandlers.add { fn(this, it) } }
fun <N: UINode> N.onMouseEnter(fn: N.(UIMouseEnterEvent) -> Unit) { mouseEnterEventHandlers.add { fn(this, it) } }
fun <N: UINode> N.onMouseExit(fn: N.(UIMouseExitEvent) -> Unit) { mouseExitEventHandlers.add { fn(this, it) } }
fun <N: UINode> N.onMousePress(fn: N.(UIMousePressEvent) -> Unit) { mousePressEventHandlers.add { fn(this, it) } }
fun <N: UINode> N.onMouseRelease(fn: N.(UIMouseReleaseEvent) -> Unit) { mouseReleaseEventHandlers.add { fn(this, it) } }
fun <N: UINode> N.onMouseClick(fn: N.(UIMouseClickEvent) -> Unit) { mouseClickEventHandlers.add { fn(this, it) } }
fun <N: UINode> N.onMouseMove(fn: N.(UIMouseMoveEvent) -> Unit) { mouseMoveEventHandlers.add { fn(this, it) } }
fun <N: UINode> N.onMouseDrag(fn: N.(UIMouseDragEvent) -> Unit) { mouseDragEventHandlers.add { fn(this, it) } }

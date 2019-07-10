package ktaf.ui

import ktaf.core.*

typealias EventHandler<T> = (T) -> Unit
typealias EventHandlerList<T> = MutableList<EventHandler<T>>

sealed class UIEvent(var handler: UINode? = null) {
    open fun handledBy(handler: UINode) { this.handler = handler }
}

class UIFocusEvent(handler: UINode?,
        val from: UINode?
): UIEvent(handler)

class UIUnFocusEvent(handler: UINode?,
        val to: UINode?
): UIEvent(handler)

class UITextInputEvent(handler: UINode?,
        val input: String
): UIEvent(handler)

sealed class UIKeyEvent(handler: UINode?,
                        val key: GLFWKey,
                        val modifiers: Set<GLFWKeyModifier>
): UIEvent(handler)

class UIKeyPressEvent internal constructor(handler: UINode?, key: GLFWKey, modifiers: Set<GLFWKeyModifier>): UIKeyEvent(handler, key, modifiers)
class UIKeyReleaseEvent internal constructor(handler: UINode?, key: GLFWKey, modifiers: Set<GLFWKeyModifier>): UIKeyEvent(handler, key, modifiers)

sealed class UIMouseEvent(handler: UINode?,
        val parent: UIMouseEvent?,
        val position: vec2
): UIEvent(handler) {
    abstract fun relativeTo(origin: vec2): UIMouseEvent
    override fun handledBy(handler: UINode) {
        super.handledBy(handler)
        parent?.handledBy(handler)
    }
}

class UIMouseEnterEvent internal constructor(handler: UINode?, parent: UIMouseEvent?, position: vec2): UIMouseEvent(handler, parent, position) {
    override fun relativeTo(origin: vec2) = UIMouseEnterEvent(handler, this, position - origin)
}

class UIMouseExitEvent internal constructor(handler: UINode?, parent: UIMouseEvent?, position: vec2): UIMouseEvent(handler, parent, position) {
    override fun relativeTo(origin: vec2) = UIMouseExitEvent(handler, this, position - origin)
}

class UIMousePressEvent internal constructor(handler: UINode?, parent: UIMouseEvent?, position: vec2,
                                             val button: GLFWMouseButton,
                                             val modifiers: Set<GLFWMouseModifier>
): UIMouseEvent(handler, parent, position) {
    override fun relativeTo(origin: vec2) = UIMousePressEvent(handler, this, position - origin, button, modifiers)
}

class UIMouseReleaseEvent internal constructor(handler: UINode?, parent: UIMouseEvent?, position: vec2,
                                               val button: GLFWMouseButton,
                                               val modifiers: Set<GLFWMouseModifier>
): UIMouseEvent(handler, parent, position) {
    override fun relativeTo(origin: vec2) = UIMouseReleaseEvent(handler, this, position - origin, button, modifiers)
}

class UIMouseClickEvent internal constructor(handler: UINode?, parent: UIMouseEvent?, position: vec2,
                                             val button: GLFWMouseButton,
                                             val modifiers: Set<GLFWMouseModifier>
): UIMouseEvent(handler, parent, position) {
    override fun relativeTo(origin: vec2) = UIMouseClickEvent(handler, this, position - origin, button, modifiers)
}

class UIMouseMoveEvent internal constructor(handler: UINode?, parent: UIMouseEvent?, position: vec2,
                                            val lastPosition: vec2
): UIMouseEvent(handler, parent, position) {
    override fun relativeTo(origin: vec2) = UIMouseMoveEvent(handler, this, position - origin, lastPosition - origin)
}

class UIMouseDragEvent internal constructor(handler: UINode?, parent: UIMouseEvent?, position: vec2,
                                            val lastPosition: vec2,
                                            val firstPosition: vec2,
                                            val modifiers: Set<GLFWMouseModifier>
): UIMouseEvent(handler, parent, position) {
    override fun relativeTo(origin: vec2) = UIMouseDragEvent(handler, this, position - origin, lastPosition - origin, firstPosition - origin, modifiers)
}

fun UIEvent.handled() = handler != null

fun <E: UIEvent> E.ifNotHandled(fn: () -> Unit) {
    if (!handled()) fn()
}

fun UIMouseEvent.within(node: UINode): Boolean {
    return position.x >= 0 && position.y >= 0 && position.x < node.computedWidthInternal && position.y < node.computedHeightInternal
}

fun <E: UIMouseEvent> E.ifWithin(node: UINode, fn: () -> Unit) {
    if (within(node)) fn()
}

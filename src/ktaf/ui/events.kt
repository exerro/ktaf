package ktaf.ui

import ktaf.core.*
import ktaf.ui.node.UINode

typealias EventHandler<T> = (T) -> Unit

class EventHandlerList<T: UIEvent> {
    private val handlers: MutableList<EventHandler<T>> = mutableListOf()

    fun connect(handler: EventHandler<T>) { handlers.add(handler) }
    fun disconnect(handler: EventHandler<T>) { handlers.remove(handler) }
    fun trigger(event: T) { handlers.forEach { it(event) } }

    operator fun invoke(handler: EventHandler<T>) { handlers.add(handler) }
}

sealed class UIEvent
sealed class UIKeyEvent(val key: GLFWKey, val modifiers: Set<GLFWKeyModifier>): UIEvent()
sealed class UIMouseEvent(val position: vec2): UIEvent()
sealed class UIMouseButtonEvent(position: vec2, val button: GLFWMouseButton): UIMouseEvent(position)

class UIFocusEvent(val from: UINode?): UIEvent()
class UIUnFocusEvent(val to: UINode?): UIEvent()

class UITextInputEvent(val input: String): UIEvent()
class UIKeyPressEvent(key: GLFWKey, modifiers: Set<GLFWKeyModifier>): UIKeyEvent(key, modifiers)
class UIKeyReleaseEvent(key: GLFWKey, modifiers: Set<GLFWKeyModifier>): UIKeyEvent(key, modifiers)

class UIMouseEnterEvent(position: vec2): UIMouseEvent(position)
class UIMouseExitEvent(position: vec2): UIMouseEvent(position)
class UIMousePressEvent(position: vec2, button: GLFWMouseButton, val modifiers: Set<GLFWMouseModifier>): UIMouseButtonEvent(position, button)
class UIMouseReleaseEvent(position: vec2, button: GLFWMouseButton, val modifiers: Set<GLFWMouseModifier>): UIMouseButtonEvent(position, button)
class UIMouseClickEvent(position: vec2, button: GLFWMouseButton, val modifiers: Set<GLFWMouseModifier>): UIMouseButtonEvent(position, button)
class UIMouseMoveEvent(position: vec2, val lastPosition: vec2): UIMouseEvent(position)
class UIMouseDragEvent(position: vec2, val lastPosition: vec2, val firstPosition: vec2, val modifiers: Set<GLFWMouseModifier>): UIMouseEvent(position)

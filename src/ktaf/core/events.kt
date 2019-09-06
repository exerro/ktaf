package ktaf.core

import geometry.vec2

abstract class Event
abstract class KeyEvent(val key: GLFWKey, val modifiers: Set<GLFWKeyModifier>): Event()
abstract class MouseEvent(val position: vec2): Event()
abstract class MouseButtonEvent(position: vec2, val button: GLFWMouseButton): MouseEvent(position)

class TextInputEvent(val input: String): Event()
class KeyPressEvent(key: GLFWKey, modifiers: Set<GLFWKeyModifier>): KeyEvent(key, modifiers)
class KeyReleaseEvent(key: GLFWKey, modifiers: Set<GLFWKeyModifier>): KeyEvent(key, modifiers)

class MousePressEvent(position: vec2, button: GLFWMouseButton, val modifiers: Set<GLFWMouseModifier>): MouseButtonEvent(position, button)
class MouseReleaseEvent(position: vec2, button: GLFWMouseButton, val modifiers: Set<GLFWMouseModifier>): MouseButtonEvent(position, button)
class MouseClickEvent(position: vec2, button: GLFWMouseButton, val modifiers: Set<GLFWMouseModifier>): MouseButtonEvent(position, button)
class MouseScrollEvent(position: vec2, val direction: vec2, val modifiers: Set<GLFWMouseModifier>): MouseEvent(position)
class MouseMoveEvent(position: vec2, val lastPosition: vec2): MouseEvent(position)
class MouseDragEvent(position: vec2, val lastPosition: vec2, val firstPosition: vec2, val buttons: Set<GLFWMouseButton>, val modifiers: Set<GLFWMouseModifier>): MouseEvent(position)

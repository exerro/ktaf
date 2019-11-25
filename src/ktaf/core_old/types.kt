package ktaf.core_old

typealias TextInputCallback = (String) -> Unit
typealias MousePressedCallback = (GLFWMouseButton, Int, Int, Set<GLFWMouseModifier>) -> Unit
typealias MouseReleasedCallback = (GLFWMouseButton, Int, Int, Set<GLFWMouseModifier>) -> Unit
typealias MouseMovedCallback = (Int, Int, Int, Int) -> Unit
typealias MouseDraggedCallback = (Int, Int, Int, Int, Int, Int, Set<GLFWMouseButton>) -> Unit
typealias DrawCallback = () -> Unit
typealias UpdateCallback = (Float) -> Unit
typealias ResizeCallback = (Int, Int) -> Unit


typealias GLFWKey = Int
typealias GLFWMouseButton = Int

typealias KeyPressedCallback = (GLFWKey, Set<KeyModifier>) -> Unit
typealias KeyReleasedCallback = (GLFWKey, Set<KeyModifier>) -> Unit
typealias TextInputCallback = (String) -> Unit
typealias MousePressedCallback = (GLFWMouseButton, Int, Int, Set<MouseModifier>) -> Unit
typealias MouseReleasedCallback = (GLFWMouseButton, Int, Int, Set<MouseModifier>) -> Unit
typealias MouseMovedCallback = (Int, Int, Int, Int) -> Unit
typealias MouseDraggedCallback = (Int, Int, Int, Int, Int, Int, Set<GLFWMouseButton>) -> Unit
typealias DrawCallback = () -> Unit
typealias UpdateCallback = (Float) -> Unit

enum class KeyModifier {
    CTRL,
    SHIFT,
    ALT,
    SUPER
}

enum class MouseModifier {
    CTRL,
    SHIFT,
    ALT,
    SUPER
}

package ktaf.core

class DeferredEvaluator<T>(val evaluator: () -> T) {
    @Volatile private var value: T? = null

    fun hasValue() = value != null
    fun getValue() = value!!

    fun evaluate() {
        value = evaluator()
    }
}

enum class GLFWKeyModifier {
    CTRL,
    SHIFT,
    ALT,
    SUPER
}

enum class GLFWMouseModifier {
    CTRL,
    SHIFT,
    ALT,
    SUPER
}

typealias GLFWKey = Int
typealias GLFWMouseButton = Int

typealias KeyPressedCallback = (GLFWKey, Set<GLFWKeyModifier>) -> Unit
typealias KeyReleasedCallback = (GLFWKey, Set<GLFWKeyModifier>) -> Unit
typealias TextInputCallback = (String) -> Unit
typealias MousePressedCallback = (GLFWMouseButton, Int, Int, Set<GLFWMouseModifier>) -> Unit
typealias MouseReleasedCallback = (GLFWMouseButton, Int, Int, Set<GLFWMouseModifier>) -> Unit
typealias MouseMovedCallback = (Int, Int, Int, Int) -> Unit
typealias MouseDraggedCallback = (Int, Int, Int, Int, Int, Int, Set<GLFWMouseButton>) -> Unit
typealias DrawCallback = () -> Unit
typealias UpdateCallback = (Float) -> Unit
typealias ResizeCallback = (Int, Int) -> Unit

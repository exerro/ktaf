package ktaf.ui

import ktaf.core.GLFWKey
import ktaf.core.GLFWKeyModifier

data class Hotkey(
        val key: GLFWKey,
        val modifiers: Set<GLFWKeyModifier> = setOf()
) {
    constructor(key: GLFWKey, vararg modifiers: GLFWKeyModifier): this(key, modifiers.toSet())

    fun matches(key: GLFWKey, modifiers: Set<GLFWKeyModifier>): Boolean {
        return key == this.key && modifiers == this.modifiers
    }
}

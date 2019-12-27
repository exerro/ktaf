package ktaf.gui.core

import lwjglkt.glfw.CursorPosition
import lwjglkt.glfw.KeyEvent
import lwjglkt.glfw.MouseEvent
import lwjglkt.glfw.TextInputEvent

interface UINodeEvents {
    /** Called when the mouse enters the node. */
    fun entered() {}

    /** Called when the mouse exits the node. */
    fun exited() {}

    /** Handle a mouse event. */
    fun handleMouseEvent(event: MouseEvent) {}

    /** Handle a key event */
    fun handleKeyEvent(event: KeyEvent) {}

    /** Handle a text input event. */
    fun handleInput(event: TextInputEvent) {}

    /** Draw the node. */
    fun draw()
}
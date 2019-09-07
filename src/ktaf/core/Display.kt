package ktaf.core

import geometry.vec2
import ktaf.graphics.DrawCtx
import ktaf.graphics.Projection
import ktaf.graphics.RenderTarget
import lwjglkt.glfw.GLFWCursor
import lwjglkt.glfw.GLFWWindow
import observables.Signal
import observables.UnitSignal
import org.lwjgl.glfw.GLFW

class Display internal constructor(title: String, width: Int, height: Int) {
    var fps = 0
        internal set

    val glfwWindow = GLFWWindow(title, width, height)
    val screen = RenderTarget.Screen(width, height)

    val onKeyEvent = Signal<KeyEvent>()
    val onKeyPress = Signal<KeyPressEvent>()
    val onKeyRelease = Signal<KeyReleaseEvent>()
    val onTextInput = Signal<TextInputEvent>()
    val onMousePress = Signal<MousePressEvent>()
    val onMouseRelease = Signal<MouseReleaseEvent>()
    val onMouseScroll = Signal<MouseScrollEvent>()
    val onMouseMove = Signal<MouseMoveEvent>()
    val onMouseDrag = Signal<MouseDragEvent>()
    val resized = glfwWindow.resized

    val draw = UnitSignal()
    val update = Signal<Float>()
    val closed = UnitSignal()

    val context2D by lazy {
        val context = DrawCtx()

        // TODO: replace with framebufferSize
        context.viewport(0, 0, glfwWindow.size.width, glfwWindow.size.height)
        context.projection(Projection.screen())

        glfwWindow.framebufferResized.connect { (w, h) ->
            context.viewport(0, 0, w, h)
        }

        context
    }

    fun update() {
        val t = System.currentTimeMillis()
        val dt = (t - lastUpdate) / 1000f

        lastUpdate = t
        fps = (1/dt).toInt()
        update.emit(dt)
    }

    fun setCursor(cursor: GLFWCursor) {
        glfwWindow.setCursor(cursor)
    }

    internal val heldMouseButtons = mutableListOf<GLFWMouseButton>()
    internal var mouseModifiers = setOf<GLFWMouseModifier>()
    internal var lastMousePosition = vec2(0f)
    internal var firstMousePosition = vec2(0f)
    internal var lastUpdate = System.currentTimeMillis()

    init {
        // on window resize, update the GL viewport and call a resized callback, if set
        glfwWindow.resized.connect { size ->
            screen.screenWidth(size.width)
            screen.screenHeight(size.height)
            screen.maxX(RatioValue(size.width.toFloat(), 0f))
            screen.maxY(RatioValue(size.height.toFloat(), 0f))
        }

        glfwWindow.setKeyCallback { _, key, _, action, mods ->
            if (action == GLFW.GLFW_PRESS) {
                onKeyPress.emit(KeyPressEvent(key, keyModifiers(mods)))
                onKeyEvent.emit(KeyPressEvent(key, keyModifiers(mods)))
            }
            if (action == GLFW.GLFW_RELEASE) {
                onKeyRelease.emit(KeyReleaseEvent(key, keyModifiers(mods)))
                onKeyEvent.emit(KeyReleaseEvent(key, keyModifiers(mods)))
            }
        }

        glfwWindow.setTextCallback { _, codepoint ->
            onTextInput.emit(TextInputEvent(String(Character.toChars(codepoint))))
        }

        glfwWindow.setCursorPositionCallback { _, xr, yr ->
            val position = vec2(xr.toFloat(), yr.toFloat())
            if (heldMouseButtons.isEmpty()) onMouseMove.emit(MouseMoveEvent(position, lastMousePosition))
            else onMouseDrag.emit(MouseDragEvent(position, lastMousePosition, firstMousePosition, heldMouseButtons.toSet(), mouseModifiers))
        }

        glfwWindow.setMouseButtonCallback { _, button, action, mods ->
            val xt = DoubleArray(1)
            val yt = DoubleArray(1)
            GLFW.glfwGetCursorPos(glfwWindow.windowID, xt, yt)
            val position = vec2(xt[0].toFloat(), yt[0].toFloat())

            if (action == GLFW.GLFW_PRESS) {
                if (heldMouseButtons.isEmpty()) {
                    firstMousePosition = position
                }
                mouseModifiers = mouseModifiers(mods)
                heldMouseButtons.add(button)
                onMousePress.emit(MousePressEvent(position, button, mouseModifiers))
            }
            else if (action == GLFW.GLFW_RELEASE) {
                heldMouseButtons.remove(button)
                onMouseRelease.emit(MouseReleaseEvent(position, button, mouseModifiers(mods)))
            }
        }

        glfwWindow.setScrollCallback { _, xo, yo ->
            val xt = DoubleArray(1)
            val yt = DoubleArray(1)
            GLFW.glfwGetCursorPos(glfwWindow.windowID, xt, yt)
            val position = vec2(xt[0].toFloat(), yt[0].toFloat())
            // TODO: fix mouse modifiers!
            onMouseScroll.emit(MouseScrollEvent(position, vec2(xo.toFloat(), yo.toFloat()), setOf()))
        }
    }
}

fun Display.close() {
    glfwWindow.close()
}

private fun keyModifiers(mods: Int): Set<GLFWKeyModifier> = setOf(
        GLFWKeyModifier.CTRL.takeIf { mods and GLFW.GLFW_MOD_CONTROL != 0 },
        GLFWKeyModifier.ALT.takeIf { mods and GLFW.GLFW_MOD_ALT != 0 },
        GLFWKeyModifier.SHIFT.takeIf { mods and GLFW.GLFW_MOD_SHIFT != 0 },
        GLFWKeyModifier.SUPER.takeIf { mods and GLFW.GLFW_MOD_SUPER != 0 }
) .filterNotNull().toSet()

private fun mouseModifiers(mods: Int): Set<GLFWMouseModifier> = setOf(
        GLFWMouseModifier.CTRL.takeIf { mods and GLFW.GLFW_MOD_CONTROL != 0 },
        GLFWMouseModifier.ALT.takeIf { mods and GLFW.GLFW_MOD_ALT != 0 },
        GLFWMouseModifier.SHIFT.takeIf { mods and GLFW.GLFW_MOD_SHIFT != 0 },
        GLFWMouseModifier.SUPER.takeIf { mods and GLFW.GLFW_MOD_SUPER != 0 }
) .filterNotNull().toSet()

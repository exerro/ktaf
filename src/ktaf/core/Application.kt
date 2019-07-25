package ktaf.core

import ktaf.graphics.RenderTarget
import ktaf.ui.layout.px
import lwjglkt.*
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import kotlin.system.exitProcess

const val DISPLAY_FPS_READINGS = 10

class WindowResizeEvent(val size: vec2): Event()

class Application(val display: GLFWDisplay) {
    private val startTime = System.currentTimeMillis()
    internal var fpsInternal = 0

    var running = true
    val time get() = (System.currentTimeMillis() - startTime) / 1000f
    val fps get() = fpsInternal
    val screen = RenderTarget.Screen(display.width, display.height)

    val onResize = EventHandlerList<WindowResizeEvent>()
    val onUpdate = EventHandlerList<UpdateEvent>()

    val onKeyEvent = EventHandlerList<KeyEvent>()
    val onKeyPress = EventHandlerList<KeyPressEvent>()
    val onKeyRelease = EventHandlerList<KeyReleaseEvent>()
    val onTextInput = EventHandlerList<TextInputEvent>()
    val onMousePress = EventHandlerList<MousePressEvent>()
    val onMouseRelease = EventHandlerList<MouseReleaseEvent>()
    val onMouseScroll = EventHandlerList<MouseScrollEvent>()
    val onMouseMove = EventHandlerList<MouseMoveEvent>()
    val onMouseDrag = EventHandlerList<MouseDragEvent>()

    val onDraw = EventHandlerList<DrawEvent>()

    internal val mainThreadFunctions = mutableListOf<() -> Unit>()

    internal val heldMouseButtons = mutableListOf<GLFWMouseButton>()
    internal var mouseModifiers = setOf<GLFWMouseModifier>()
    internal var lastMousePosition = vec2(0f)
    internal var firstMousePosition = vec2(0f)
}

fun Application.thread(fn: () -> Unit) {
    Thread(fn).start()
}

fun Application.mainThread(fn: () -> Unit) {
    mainThreadFunctions.add(fn)
}

fun <T> Application.computeOnMainThread(fn: () -> T): T {
    val evaluator = DeferredEvaluator(fn)
    mainThread(evaluator::evaluate)
    while (true) { if (evaluator.hasValue()) return evaluator.getValue() }
}

fun application(name: String, load: (Application).() -> Unit) {
    val width = 1080
    val height = 720
    val app = setup(name, width, height)
    load(app)
    run(app)
    destroy(app)
    exitProcess(0)
}

private fun setup(title: String, width: Int, height: Int): Application {
    val display = GLFWDisplay(title, width, height)
    val app = Application(display)

    display.setup()

    // on window resize, update the GL viewport and call a resized callback, if set
    GLFW.glfwSetWindowSizeCallback(display.windowID) { _, w, h ->
        app.display.width = w
        app.display.height = h
        app.screen.screenWidth(w)
        app.screen.screenHeight(h)
        app.screen.maxX(RatioValue(w.toFloat(), 0f))
        app.screen.maxY(RatioValue(h.toFloat(), 0f))
        app.onResize.trigger(WindowResizeEvent(vec2(w.toFloat(), h.toFloat())))
    }

    GLFW.glfwSetKeyCallback(display.windowID) { _, key, _, action, mods ->
        if (action == GLFW.GLFW_PRESS) {
            app.onKeyPress.trigger(KeyPressEvent(key, keyModifiers(mods)))
            app.onKeyEvent.trigger(KeyPressEvent(key, keyModifiers(mods)))
        }
        if (action == GLFW.GLFW_RELEASE) {
            app.onKeyRelease.trigger(KeyReleaseEvent(key, keyModifiers(mods)))
            app.onKeyEvent.trigger(KeyReleaseEvent(key, keyModifiers(mods)))
        }
    }

    GLFW.glfwSetCharCallback(display.windowID) { _, codepoint ->
        app.onTextInput.trigger(TextInputEvent(String(Character.toChars(codepoint))))
    }

    GLFW.glfwSetCursorPosCallback(display.windowID) { _, xr, yr ->
        val position = vec2(xr.toFloat(), yr.toFloat())
        if (app.heldMouseButtons.isEmpty()) app.onMouseMove.trigger(MouseMoveEvent(position, app.lastMousePosition))
        else app.onMouseDrag.trigger(MouseDragEvent(position, app.lastMousePosition, app.firstMousePosition, app.heldMouseButtons.toSet(), app.mouseModifiers))
    }

    GLFW.glfwSetMouseButtonCallback(display.windowID) { _, button, action, mods ->
        val xt = DoubleArray(1)
        val yt = DoubleArray(1)
        GLFW.glfwGetCursorPos(display.windowID, xt, yt)
        val position = vec2(xt[0].toFloat(), yt[0].toFloat())

        if (action == GLFW.GLFW_PRESS) {
            if (app.heldMouseButtons.isEmpty()) {
                app.firstMousePosition = position
            }
            app.mouseModifiers = mouseModifiers(mods)
            app.heldMouseButtons.add(button)
            app.onMousePress.trigger(MousePressEvent(position, button, app.mouseModifiers))
        }
        else if (action == GLFW.GLFW_RELEASE) {
            app.heldMouseButtons.remove(button)
            app.onMouseRelease.trigger(MouseReleaseEvent(position, button, mouseModifiers(mods)))
        }
    }

    GLFW.glfwSetScrollCallback(display.windowID) { _, xo, yo ->
        val xt = DoubleArray(1)
        val yt = DoubleArray(1)
        GLFW.glfwGetCursorPos(display.windowID, xt, yt)
        val position = vec2(xt[0].toFloat(), yt[0].toFloat())
        // TODO: fix mouse modifiers!
        app.onMouseScroll.trigger(MouseScrollEvent(position, vec2(xo.toFloat(), yo.toFloat()), setOf()))
    }

    return app
}

private fun run(app: Application) {
    var lastUpdate = System.currentTimeMillis()
    var fpsReadings: List<Float> = ArrayList()

    app.running = true

    // run the rendering loop until the user has attempted to close the window or has pressed the ESCAPE key
    while (app.running && !GLFW.glfwWindowShouldClose(app.display.windowID)) {
        // set the clear colour to black and clear the framebuffer
        GL.clearColour(0.0f, 0.0f, 0.0f, 0.0f)
        GL.clear(GLClearBuffer.GL_COLOR_BUFFER_BIT, GLClearBuffer.GL_DEPTH_BUFFER_BIT)

        val t = System.currentTimeMillis()
        val dt = (t - lastUpdate).toFloat()
        lastUpdate = t

        // calculate the FPS based on past FPS readings
        fpsReadings = (listOf(1000/dt) + fpsReadings).take(DISPLAY_FPS_READINGS)
        app.fpsInternal = (fpsReadings.fold(dt) { acc, it -> acc + it } / (fpsReadings.size + 1)).toInt()

        // call the update callbacks
        app.onUpdate.trigger(UpdateEvent(dt / 1000f))

        // call the ktaf.graphics.draw callbacks
        checkGLError { GL.finish() }
        app.onDraw.trigger(DrawEvent)

        // run the main thread functions
        app.mainThreadFunctions.forEach { it() }
        app.mainThreadFunctions.clear()

        // swap the color buffers to present the content to the screen
        GLFW.glfwSwapBuffers(app.display.windowID)

        // poll for window events
        // the key callback above will only be invoked during this call
        GLFW.glfwPollEvents()

        freeUnreferencedGLObjects()
    }
}

private fun destroy(app: Application) {
    closeGL()
    Callbacks.glfwFreeCallbacks(app.display.windowID)
    GLFW.glfwDestroyWindow(app.display.windowID)
    GLFW.glfwTerminate()
    GLFW.glfwSetErrorCallback(null)?.free()
}

private fun keyModifiers(mods: Int): Set<GLFWKeyModifier> = setOf(
        GLFWKeyModifier.CTRL.takeIf { mods and GLFW.GLFW_MOD_CONTROL != 0 },
        GLFWKeyModifier.ALT.takeIf { mods and GLFW.GLFW_MOD_ALT != 0 },
        GLFWKeyModifier.SHIFT.takeIf { mods and GLFW.GLFW_MOD_SHIFT != 0 },
        GLFWKeyModifier.SUPER.takeIf { mods and GLFW.GLFW_MOD_SUPER != 0 }
) .filter { it != null } .map { it!! } .toSet()

private fun mouseModifiers(mods: Int): Set<GLFWMouseModifier> = setOf(
        GLFWMouseModifier.CTRL.takeIf { mods and GLFW.GLFW_MOD_CONTROL != 0 },
        GLFWMouseModifier.ALT.takeIf { mods and GLFW.GLFW_MOD_ALT != 0 },
        GLFWMouseModifier.SHIFT.takeIf { mods and GLFW.GLFW_MOD_SHIFT != 0 },
        GLFWMouseModifier.SUPER.takeIf { mods and GLFW.GLFW_MOD_SUPER != 0 }
) .filter { it != null } .map { it!! } .toSet()

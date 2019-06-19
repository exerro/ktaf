import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL46.*

const val DISPLAY_FPS_READINGS = 10

class Application(val display: GLFWDisplay) {
    var running = true
    var fps = 0

    val viewport
        get() = GLViewport({0}, {0}, {display.width}, {display.height})

    internal val onResizedCallbacks = mutableListOf<ResizeCallback>()
    internal val onKeyPressedCallbacks = mutableListOf<KeyPressedCallback>()
    internal val onKeyReleasedCallbacks = mutableListOf<KeyReleasedCallback>()
    internal val onTextInputCallbacks = mutableListOf<TextInputCallback>()
    internal val onMousePressedCallbacks = mutableListOf<MousePressedCallback>()
    internal val onMouseReleasedCallbacks = mutableListOf<MouseReleasedCallback>()
    internal val onMouseMovedCallbacks = mutableListOf<MouseMovedCallback>()
    internal val onMouseDraggedCallbacks = mutableListOf<MouseDraggedCallback>()
    internal val onDrawCallbacks = mutableListOf<DrawCallback>()
    internal val onUpdateCallbacks = mutableListOf<UpdateCallback>()

    internal val mainThreadFunctions = mutableListOf<() -> Unit>()

    internal val heldMouseButtons = mutableListOf<GLFWMouseButton>()
    internal var lastMouseX = 0
    internal var lastMouseY = 0
    internal var firstMouseX = 0
    internal var firstMouseY = 0
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

fun Application.draw(draw: (Application).() -> Unit)
        = onDrawCallbacks.add { draw(this) }
fun Application.update(update: (Application).(Float) -> Unit)
        = onUpdateCallbacks.add { update(this, it) }
fun Application.onMousePressed(handler: (Application).(GLFWMouseButton, Int, Int, Set<GLFWMouseModifier>) -> Unit)
        = onMousePressedCallbacks.add { a, b, c, d -> handler(this, a, b, c, d) }
fun Application.onMouseReleased(handler: (Application).(GLFWMouseButton, Int, Int, Set<GLFWMouseModifier>) -> Unit)
        = onMouseReleasedCallbacks.add { a, b, c, d -> handler(this, a, b, c, d) }
fun Application.onMouseMoved(handler: (Application).(Int, Int, Int, Int) -> Unit)
        = onMouseMovedCallbacks.add { a, b, c, d -> handler(this, a, b, c, d) }
fun Application.onMouseDragged(handler: (Application).(Int, Int, Int, Int, Int, Int, Set<GLFWMouseButton>) -> Unit)
        = onMouseDraggedCallbacks.add { a, b, c, d, e, f, g -> handler(this, a, b, c, d, e, f, g) }

fun application(name: String, load: (Application).() -> Unit) {
    val width = 1080
    val height = 720
    val app = setup(name, width, height)
    load(app)
    run(app)
    destroy(app)
    System.exit(0)
}

private fun setup(title: String, width: Int, height: Int): Application {
    val display = GLFWDisplay(title, width, height)
    val app = Application(display)

    display.setup()

    // on window resize, update the GL viewport and call a resized callback, if set
    GLFW.glfwSetWindowSizeCallback(display.windowID) { _, w, h ->
        app.display.width = w
        app.display.height = h
        app.onResizedCallbacks.map { it(w, h) }
    }

    GLFW.glfwSetKeyCallback(display.windowID) { _, key, _, action, mods ->
        if (action == GLFW.GLFW_PRESS) app.onKeyPressedCallbacks.map { it(key, keyModifiers(mods)) }
        if (action == GLFW.GLFW_RELEASE) app.onKeyReleasedCallbacks.map { it(key, keyModifiers(mods)) }
    }

    GLFW.glfwSetCharCallback(display.windowID) { _, codepoint ->
        app.onTextInputCallbacks.map { it(Character.toChars(codepoint).toString()) }
    }

    GLFW.glfwSetCursorPosCallback(display.windowID) { _, xr, yr ->
        val x = xr.toInt()
        val y = yr.toInt()
        if (app.heldMouseButtons.isEmpty()) app.onMouseMovedCallbacks.map { it(x, y, app.lastMouseX, app.lastMouseY) }
        else app.onMouseDraggedCallbacks.map { it(x, y, app.lastMouseX, app.lastMouseY, app.firstMouseX, app.firstMouseY, app.heldMouseButtons.toSet()) }
        app.lastMouseX = x
        app.lastMouseY = y
    }

    GLFW.glfwSetMouseButtonCallback(display.windowID) { _, button, action, mods ->
        val xt = DoubleArray(1)
        val yt = DoubleArray(1)
        GLFW.glfwGetCursorPos(display.windowID, xt, yt)
        val x = xt[0].toInt()
        val y = yt[0].toInt()

        if (action == GLFW.GLFW_PRESS) {
            if (app.heldMouseButtons.isEmpty()) {
                app.firstMouseX = x
                app.firstMouseY = y
            }
            app.heldMouseButtons.add(button)
            app.onMousePressedCallbacks.map { it(button, x, y, mouseModifiers(mods)) }
        } else if (action == GLFW.GLFW_RELEASE) {
            app.heldMouseButtons.remove(button)
            app.onMouseReleasedCallbacks.map { it(button, x, y, mouseModifiers(mods)) }
        }
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
        checkGLError {
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        }

        val t = System.currentTimeMillis()
        val dt = (t - lastUpdate).toFloat()
        lastUpdate = t

        // calculate the FPS based on past FPS readings
        fpsReadings = (listOf(1000/dt) + fpsReadings).take(DISPLAY_FPS_READINGS)
        app.fps = (fpsReadings.fold(dt) { acc, it -> acc + it } / (fpsReadings.size + 1)).toInt()

        // call the update callbacks
        app.onUpdateCallbacks.map { it(dt / 1000f) }

        // call the draw callbacks
        checkGLError { glFinish() }
        app.onDrawCallbacks.map { it() }

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

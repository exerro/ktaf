import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.MemoryUtil

const val DISPLAY_FPS_READINGS = 10

class Application(val windowID: Long, internal var windowWidth: Int, internal var windowHeight: Int) {
    var running = true
    var fps = 0

    var displayWidth
        get() = windowWidth
        set(w) {
            windowWidth = w
            GLFW.glfwSetWindowSize(windowID, windowWidth, windowHeight)
        }

    var displayHeight
        get() = windowHeight
        set(h) {
            windowHeight = h
            GLFW.glfwSetWindowSize(windowID, windowWidth, windowHeight)
        }

    internal val onResizedCallbacks = mutableListOf<(Int, Int) -> Unit>()
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

fun Application.draw(draw: (Application).() -> Unit) = onDrawCallbacks.add { draw(this) }
fun Application.update(update: (Application).(Float) -> Unit) = onUpdateCallbacks.add { update(this, it) }

fun application(name: String, load: (Application).() -> Unit) {
    val width = 1080
    val height = 720
    val app = setup(name, width, height)
    app.thread { load(app) }
    run(app)
    destroy(app)
}

private fun setup(title: String, width: Int, height: Int): Application {
    // set the GLFW error callback
    GLFWErrorCallback.createPrint(System.err).set()

    // initialise GLFW
    if (!GLFW.glfwInit()) throw IllegalStateException("Unable to initialize GLFW")

    GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE) // the window will stay hidden after creation
    GLFW.glfwWindowHint(GLFW.GLFW_FOCUS_ON_SHOW, GLFW.GLFW_TRUE) // the window will focus when shown
    GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE) // the window will be resizable
    GLFW.glfwWindowHint(GLFW.GLFW_CENTER_CURSOR, GLFW.GLFW_TRUE) // the window will have the cursor centred

    val windowID = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL) // create the window
    if (windowID == MemoryUtil.NULL) throw RuntimeException("Failed to create the GLFW window")

    val videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
    if (videoMode != null)
        GLFW.glfwSetWindowPos(
                windowID,
                (videoMode.width() - width) / 2,
                (videoMode.height() - height) / 2
        )

    GLFW.glfwMakeContextCurrent(windowID) // make the OpenGL context current
    GLFW.glfwSwapInterval(1) // enable/disable v-sync
    GLFW.glfwShowWindow(windowID) // make the window visible

    setupGL()

    val app = Application(windowID, width, height)

    // on window resize, update the GL viewport and call a resized callback, if set
    GLFW.glfwSetWindowSizeCallback(windowID) { _, w, h ->
        app.windowWidth = w
        app.windowHeight = h
        app.onResizedCallbacks.map { it(w, h) }
    }

    GLFW.glfwSetKeyCallback(windowID) { _, key, _, action, mods ->
        if (action == GLFW.GLFW_PRESS) app.onKeyPressedCallbacks.map { it(key, keyModifiers(mods)) }
        if (action == GLFW.GLFW_RELEASE) app.onKeyReleasedCallbacks.map { it(key, keyModifiers(mods)) }
    }

    GLFW.glfwSetCharCallback(windowID) { _, codepoint ->
        app.onTextInputCallbacks.map { it(Character.toChars(codepoint).toString()) }
    }

    GLFW.glfwSetCursorPosCallback(windowID) { _, xr, yr ->
        val x = (xr * app.windowWidth).toInt()
        val y = (yr * app.windowHeight).toInt()
        if (app.heldMouseButtons.isEmpty()) app.onMouseMovedCallbacks.map { it(x, y, app.lastMouseX, app.lastMouseY) }
        else app.onMouseDraggedCallbacks.map { it(x, y, app.lastMouseX, app.lastMouseY, app.firstMouseX, app.firstMouseY, app.heldMouseButtons.toSet()) }
        app.lastMouseX = x
        app.lastMouseY = y
    }

    GLFW.glfwSetMouseButtonCallback(windowID) { _, button, action, mods ->
        val x = 0 // TODO
        val y = 0 // TODO

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
    while (app.running && !GLFW.glfwWindowShouldClose(app.windowID)) {
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

        // call the update render callbacks
        app.onUpdateCallbacks.map { it(dt / 1000f) }
        app.onDrawCallbacks.map { it() }
        app.mainThreadFunctions.map { it() }
        app.mainThreadFunctions.clear()

        checkGLError { glFinish() }

        // swap the color buffers to present the content to the screen
        GLFW.glfwSwapBuffers(app.windowID)

        // poll for window events
        // the key callback above will only be invoked during this call
        GLFW.glfwPollEvents()

        checkGLError {}

        freeUnreferencedGLObjects()
    }
}

private fun destroy(app: Application) {
    Callbacks.glfwFreeCallbacks(app.windowID)
    GLFW.glfwDestroyWindow(app.windowID)
    GLFW.glfwTerminate()
    GLFW.glfwSetErrorCallback(null)?.free()
}

private fun keyModifiers(mods: Int): Set<KeyModifier> = setOf(
        KeyModifier.CTRL.takeIf { mods and GLFW.GLFW_MOD_CONTROL != 0 },
        KeyModifier.ALT.takeIf { mods and GLFW.GLFW_MOD_ALT != 0 },
        KeyModifier.SHIFT.takeIf { mods and GLFW.GLFW_MOD_SHIFT != 0 },
        KeyModifier.SUPER.takeIf { mods and GLFW.GLFW_MOD_SUPER != 0 }
) .filter { it != null } .map { it!! } .toSet()

private fun mouseModifiers(mods: Int): Set<MouseModifier> = setOf(
        MouseModifier.CTRL.takeIf { mods and GLFW.GLFW_MOD_CONTROL != 0 },
        MouseModifier.ALT.takeIf { mods and GLFW.GLFW_MOD_ALT != 0 },
        MouseModifier.SHIFT.takeIf { mods and GLFW.GLFW_MOD_SHIFT != 0 },
        MouseModifier.SUPER.takeIf { mods and GLFW.GLFW_MOD_SUPER != 0 }
) .filter { it != null } .map { it!! } .toSet()

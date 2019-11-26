package ktaf.core

import lwjglkt.LWJGLKTContext
import lwjglkt.gl.enum.GLClearBuffer
import lwjglkt.glfw.GLFWInitialisationHint
import lwjglkt.glfw.GLFWWindowBuilder
import lwjglkt.lwjglktInit
import kotlin.math.min

class Application internal constructor(
        private val ctx: LWJGLKTContext
) {
    var running = false
        private set

    fun display(title: String, width: Int, height: Int, fn: (Window) -> Unit) {
        synchronized(windowsToCreate) {
            windowsToCreate.add(GLFWWindowBuilder(ctx.glfw, title, width, height) to fn)
        }
    }

    fun stop() {
        running = false
    }

    ////////////////////////////////////////////////////////////////////////////

    internal fun run() {
        running = true

        while (running) {
            val wtc = synchronized(windowsToCreate) {
                val toCreate = windowsToCreate.map { it }
                windowsToCreate.clear()
                toCreate
            }

            wtc.forEach { (builder, fn) ->
                val window = Window(ctx.glfw.createWindow(builder))
                windows.add(window)
                fn(window)
            }

            val toClose = windows.filter { it.glfwWindow.shouldClose() }
            toClose.forEach { it.glfwWindow.destroy() }
            toClose.forEach { it.closed.emit() }
            windows.removeAll(toClose)
            if (windows.isEmpty()) stop()

            val t = System.currentTimeMillis()

            windows.forEach {
                it.glfwWindow.glContext.makeCurrent {
                    it.glfwWindow.glContext.gl.finish()
                    it.glfwWindow.glContext.gl.clearColour(0f, 0f, 0f, 0f)
                    it.glfwWindow.glContext.gl.clear(GLClearBuffer.GL_COLOR_BUFFER_BIT, GLClearBuffer.GL_DEPTH_BUFFER_BIT)
                }
                it.update.emit(min(0.1f, (t - it.lastUpdateTime) / 1000f))
                it.draw.emit()
                it.glfwWindow.swapBuffers()
                it.lastUpdateTime = t
            }

            ctx.glfw.pollEvents()
        }
    }

    private val windowsToCreate: MutableList<Pair<GLFWWindowBuilder, (Window) -> Unit>> = mutableListOf()
    private val windows: MutableList<Window> = mutableListOf()
}

fun application(vararg hints: GLFWInitialisationHint, fn: Application.() -> Unit) {
    val ctx = lwjglktInit(*hints)
    val app = Application(ctx)
    fn(app)
    app.run()
}

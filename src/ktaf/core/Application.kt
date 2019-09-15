package ktaf.core

import geometry.vec2
import lwjglkt.freeUnreferencedGLObjects
import lwjglkt.gl.GLClearBuffer
import lwjglkt.gl.makeCurrent
import lwjglkt.glfw.GLFWkt
import lwjglkt.glfw.init
import lwjglkt.glfw.pollEvents
import kotlin.system.exitProcess

class WindowResizeEvent(val size: vec2): Event()

class Application internal constructor() {
    val time get() = (System.currentTimeMillis() - startTime) / 1000f

    fun display(title: String, width: Int = 1080, height: Int = 720, fn: Display.() -> Unit) {
        synchronized(displaysToCreate) {
            displaysToCreate.add(DisplayConstructor(title, width, height, fn))
        }
    }

    internal fun run() {
        while (true) {
            // create any queued displays
            createNewDisplays()

            // take a copy of the current displays
            val frameDisplays = synchronized(displays) { displays.map { it } }

            // if all the displays have been closed, quit
            if (frameDisplays.isEmpty())
                break

            // update all the displays
            frameDisplays.forEach(Display::update)

            frameDisplays.forEach {
                // set the clear colour to black and clear the framebuffer, then call the draw callbacks
                it.glfwWindow.glContext.makeCurrent {
                    finish()
                    clearColour(0.0f, 0.0f, 0.0f, 0.0f)
                    clear(GLClearBuffer.GL_COLOR_BUFFER_BIT, GLClearBuffer.GL_DEPTH_BUFFER_BIT)
                    it.draw.emit()
                }

                // swap the color buffers to present the content to the screen
                it.glfwWindow.swapBuffers()
            }

            // poll for window events
            GLFWkt.pollEvents()

            // TODO: this will likely break with multiple contexts
            freeUnreferencedGLObjects()

            // remove displays that should close
            frameDisplays.forEach {
                if (it.glfwWindow.shouldClose()) {
                    synchronized(this.displays) { this.displays.remove(it) }
                    it.glfwWindow.destroy()
                }
            }
        }
    }

    private fun createNewDisplays() {
        val displays = synchronized(displaysToCreate) {
            displaysToCreate.map { it } .also { displaysToCreate.clear() }
        } .map(DisplayConstructor::create)

        synchronized(this.displays) {
            this.displays.addAll(displays)
        }
    }

    private val displaysToCreate: MutableList<DisplayConstructor> = mutableListOf()
    private val displays: MutableList<Display> = mutableListOf()
    private val startTime = System.currentTimeMillis()
}

private class DisplayConstructor(
        private val title: String,
        private val width: Int,
        private val height: Int,
        private val fn: Display.() -> Unit
) {
    fun create(): Display {
        val display = Display(title, width, height)
        display.glfwWindow.glContext.makeCurrent { fn(display) }
        return display
    }
}

fun application(load: (Application).() -> Unit) {
    GLFWkt.init()
    val app = Application()
    load(app)
    app.run()
    exitProcess(0)
}

package ktaf.ui.scene

import ktaf.core.*
import ktaf.graphics.DrawContext2D
import lwjglkt.GLFWDisplay

fun scene(display: GLFWDisplay, context: DrawContext2D, init: UIScene.() -> Unit = {}): UIScene {
    val root = UIScene(display, context)
    init(root)
    return root
}

fun UIScene.attachCallbacks(application: Application) {
    application.update(updater)
    application.draw(drawer)
    application.onMousePressed(mousePresser)
    application.onMouseReleased(mouseReleaser)
    application.onMouseMoved(mouseMover)
    application.onMouseDragged(mouseDragger)
    application.onKeyPressed(keyPresser)
    application.onKeyReleased(keyReleaser)
    application.onTextInput(textInputter)
}

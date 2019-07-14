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
    application.onUpdate.connect(this::update)
    application.onDraw.connect(this::draw)
    application.onMousePress.connect(this::mousePressed)
    application.onMouseRelease.connect(this::mouseReleased)
    application.onMouseScroll.connect(this::mouseScrolled)
    application.onMouseMove.connect(this::mouseMoved)
    application.onMouseDrag.connect(this::mouseDragged)
    application.onKeyPress.connect(this::keyPressed)
    application.onKeyRelease.connect(this::keyReleased)
    application.onTextInput.connect(this::textInput)
}

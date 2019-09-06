package ktaf.ui.scene

import ktaf.core.*
import ktaf.graphics.DrawContext2D

fun scene(display: Display, context: DrawContext2D, init: UIScene.() -> Unit = {}): UIScene {
    val root = UIScene(display, context)
    init(root)
    return root
}

fun UIScene.attachCallbacks(display: Display) {
    display.update.connect(this::update)
    display.draw.connect(this::draw)
    display.onMousePress.connect(this::mousePressed)
    display.onMouseRelease.connect(this::mouseReleased)
    display.onMouseScroll.connect(this::mouseScrolled)
    display.onMouseMove.connect(this::mouseMoved)
    display.onMouseDrag.connect(this::mouseDragged)
    display.onKeyPress.connect(this::keyPressed)
    display.onKeyRelease.connect(this::keyReleased)
    display.onTextInput.connect(this::textInput)
}

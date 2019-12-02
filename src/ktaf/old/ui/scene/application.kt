package ktaf.ui.scene

import ktaf.core.Display
import ktaf.graphics.DrawCtx

fun scene(display: Display, context: DrawCtx, init: UIScene.() -> Unit = {}): UIScene {
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

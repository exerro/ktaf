package ktaf.ui.scene

import ktaf.core.*

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

package ktaf.ui.scene

import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.ui.UIFocusEvent
import ktaf.ui.UIUnFocusEvent
import ktaf.ui.node.UINode
import ktaf.ui.node.handleEvent
import ktaf.util.Animations
import lwjglkt.GLFWDisplay

class UIScene(val display: GLFWDisplay, val context: DrawContext2D) {
    val root = KTAFValue<UINode?>(null)
    val focussedNode = KTAFValue<UINode?>(null)
    val animations = Animations()

    init {
        focussedNode.connectComparator { old, new ->
            old?.handleEvent(UIUnFocusEvent(new))
            new?.handleEvent(UIFocusEvent(old))
            old?.focussed?.set(false)
            new?.focussed?.set(true)
        }

        root.connectComparator { old, new ->
            old?.scene?.set(null)
            new?.scene?.set(this)
        }
    }

    internal var focussedNodeHover: UINode? = null
    internal var firstRelativeMouseLocation = vec2(0f)
    internal var lastRelativeMouseLocation = vec2(0f)
    internal var mouseModifiers = setOf<GLFWMouseModifier>()

    internal val updater by lazy { { _: Application, dt: Float -> update(dt) } }
    internal val drawer by lazy { { _: Application -> draw() } }
    internal val mousePresser by lazy {
        { _: Application, button: GLFWMouseButton, x: Int, y: Int, modifiers: Set<GLFWMouseModifier> -> mousePressed(button, vec2(x.toFloat(), y.toFloat()), modifiers) }
    }
    internal val mouseReleaser by lazy {
        { _: Application, button: GLFWMouseButton, x: Int, y: Int, modifiers: Set<GLFWMouseModifier> -> mouseReleased(button, vec2(x.toFloat(), y.toFloat()), modifiers) }
    }
    internal val mouseMover by lazy {
        { _: Application, x: Int, y: Int, lx: Int, ly: Int -> mouseMoved(vec2(x.toFloat(), y.toFloat()), vec2(lx.toFloat(), ly.toFloat())) }
    }
    internal val mouseDragger by lazy {
        { _: Application, x: Int, y: Int, _: Int, _: Int, _: Int, _: Int, _: Set<GLFWMouseButton> -> mouseDragged(vec2(x.toFloat(), y.toFloat())) }
    }
    internal val keyPresser by lazy {
        { _: Application, key: GLFWKey, modifiers: Set<GLFWKeyModifier> -> keyPressed(key, modifiers) }
    }
    internal val keyReleaser by lazy {
        { _: Application, key: GLFWKey, modifiers: Set<GLFWKeyModifier> -> keyReleased(key, modifiers) }
    }
    internal val textInputter by lazy {
        { _: Application, text: String -> textInput(text) }
    }
}

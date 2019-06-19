package ui

import core.minus
import graphics.DrawContext2D
import core.plus
import core.vec2
import GLFWCursor

abstract class UINode: UI_t {
    internal val focusEventHandlers: EventHandlerList<UIFocusEvent> = mutableListOf()
    internal val unFocusEventHandlers: EventHandlerList<UIUnFocusEvent> = mutableListOf()
    internal val mouseEventHandlers: EventHandlerList<UIMouseEvent> = mutableListOf()
    internal val mouseEnterEventHandlers: EventHandlerList<UIMouseEnterEvent> = mutableListOf()
    internal val mouseExitEventHandlers: EventHandlerList<UIMouseExitEvent> = mutableListOf()
    internal val mousePressEventHandlers: EventHandlerList<UIMousePressEvent> = mutableListOf()
    internal val mouseReleaseEventHandlers: EventHandlerList<UIMouseReleaseEvent> = mutableListOf()
    internal val mouseClickEventHandlers: EventHandlerList<UIMouseClickEvent> = mutableListOf()
    internal val mouseMoveEventHandlers: EventHandlerList<UIMouseMoveEvent> = mutableListOf()
    internal val mouseDragEventHandlers: EventHandlerList<UIMouseDragEvent> = mutableListOf()
    internal val keyEventHandlers: EventHandlerList<UIKeyEvent> = mutableListOf()
    internal val keyPressEventHandlers: EventHandlerList<UIKeyPressEvent> = mutableListOf()
    internal val keyReleaseEventHandlers: EventHandlerList<UIKeyReleaseEvent> = mutableListOf()
    internal val textInputEventHandlers: EventHandlerList<UITextInputEvent> = mutableListOf()
    internal var mouseInside = false
    internal var computedX: Float = 0f
    internal var computedY: Float = 0f
    internal var computedWidth: Float = 0f
    internal var computedHeight: Float = 0f
    internal val childrenInternal = mutableListOf<UINode>()
    internal val foregroundsInternal = mutableListOf<Foreground>()
    internal val backgroundsInternal = mutableListOf<Background>()

    open val cursor: GLFWCursor? = GLFWCursor.DEFAULT

    var margin by property(Border(0f))
    var padding by property(Border(0f))
    var layout by property(ListLayout() as UILayout)
    var width by property(null as Float?)
    var height by property(null as Float?)
    var parent by property(null as UINode?)
    var scene by property(null as UIScene?)
    val children get() = childrenInternal.toList()
    val foregrounds get() = foregroundsInternal.toList()
    val backgrounds get() = backgroundsInternal.toList()

    init {
        p(::parent) {
            attachChangeCallback { old, new ->
                old?.childrenInternal?.remove(this@UINode)
                new?.childrenInternal?.add(this@UINode)
                this@UINode.scene = new?.scene
            }
        }

        p(::scene) {
            attachChangeToCallback { new ->
                childrenInternal.forEach { it.scene = new }
            }
        }
    }

    open fun computeHeight(width: Float) = height

    open fun update(dt: Float) {}

    open fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        backgroundsInternal.reversed().forEach {
            it.draw(context, position, size)
        }

        childrenInternal.forEach {
            it.draw(context,
                    position + vec2(it.computedX, it.computedY),
                    vec2(it.computedWidth, it.computedHeight)
            )
        }

        foregroundsInternal.forEach {
            it.draw(context, position + vec2(padding.left, padding.top), size - padding.size)
        }
    }

    open fun handleEvent(event: UIEvent) {
        when (event) {
            is UIKeyEvent -> handleKeyEvent(event)
            is UIMouseEvent -> handleMouseEvent(event)
            is UITextInputEvent -> textInputEventHandlers.forEach { it(event) }
            is UIFocusEvent -> focusEventHandlers.forEach { it(event) }
            is UIUnFocusEvent -> unFocusEventHandlers.forEach { it(event) }
        }

        when (event) {
            is UITextInputEvent, is UIFocusEvent, is UIUnFocusEvent -> childrenInternal.forEach { it.handleEvent(event) }
            else -> { /* do nothing */ }
        }
    }

    open fun handleKeyEvent(event: UIKeyEvent) {
        keyEventHandlers.forEach { it(event) }

        when (event) {
            is UIKeyPressEvent -> keyPressEventHandlers.forEach { it(event) }
            is UIKeyReleaseEvent -> keyReleaseEventHandlers.forEach { it(event) }
        }

        childrenInternal.forEach { it.handleEvent(event) }
    }

    open fun handleMouseEvent(event: UIMouseEvent) {
        mouseEventHandlers.forEach { it(event) }

        when (event) {
            is UIMouseEnterEvent -> mouseEnterEventHandlers.forEach { it(event) }
            is UIMouseExitEvent -> mouseExitEventHandlers.forEach { it(event) }
            is UIMousePressEvent -> mousePressEventHandlers.forEach { it(event) }
            is UIMouseReleaseEvent -> mouseReleaseEventHandlers.forEach { it(event) }
            is UIMouseClickEvent -> mouseClickEventHandlers.forEach { it(event) }
            is UIMouseMoveEvent -> mouseMoveEventHandlers.forEach { it(event) }
            is UIMouseDragEvent -> mouseDragEventHandlers.forEach { it(event) }
        }

        childrenInternal.reversed().forEach {
            it.handleEvent(event.relativeTo(vec2(padding.left, padding.top) + vec2(it.computedX, it.computedY)))
        }
    }
}

fun UINode.absolutePosition(): vec2
        = (parent?.absolutePosition() ?: vec2(0f)) + (parent?.padding?.size ?: vec2(0f)) + vec2(computedX, computedY)

fun UINode.requestFocus() {
    scene?.focusOn(this)
}

fun UINode.unfocus() {
    if (isFocused()) scene?.unfocus()
}

fun UINode.isFocused()
        = scene?.focussedNode == this

fun <N: UINode> N.onFocus(fn: N.(UIFocusEvent) -> Unit) {
    focusEventHandlers.add { fn(this, it) }
}

fun <N: UINode> N.onFocusLost(fn: N.(UIUnFocusEvent) -> Unit) {
    unFocusEventHandlers.add { fn(this, it) }
}

fun <N: UINode> N.onKeyEvent(fn: N.(UIKeyEvent) -> Unit) {
    keyEventHandlers.add { fn(this, it) }
}

fun <N: UINode> N.onKeyPress(fn: N.(UIKeyPressEvent) -> Unit) {
    keyPressEventHandlers.add { fn(this, it) }
}

fun <N: UINode> N.onKeyRelease(fn: N.(UIKeyReleaseEvent) -> Unit) {
    keyReleaseEventHandlers.add { fn(this, it) }
}

fun <N: UINode> N.onTextInput(fn: N.(UITextInputEvent) -> Unit) {
    textInputEventHandlers.add { fn(this, it) }
}

fun <N: UINode> N.onMouseEvent(fn: N.(UIMouseEvent) -> Unit) {
    mouseEventHandlers.add { fn(this, it) }
}

fun <N: UINode> N.onMouseEnter(fn: N.(UIMouseEnterEvent) -> Unit) {
    mouseEnterEventHandlers.add { fn(this, it) }
}

fun <N: UINode> N.onMouseExit(fn: N.(UIMouseExitEvent) -> Unit) {
    mouseExitEventHandlers.add { fn(this, it) }
}

fun <N: UINode> N.onMousePress(fn: N.(UIMousePressEvent) -> Unit) {
    mousePressEventHandlers.add { fn(this, it) }
}

fun <N: UINode> N.onMouseRelease(fn: N.(UIMouseReleaseEvent) -> Unit) {
    mouseReleaseEventHandlers.add { fn(this, it) }
}

fun <N: UINode> N.onMouseClick(fn: N.(UIMouseClickEvent) -> Unit) {
    mouseClickEventHandlers.add { fn(this, it) }
}

fun <N: UINode> N.onMouseMove(fn: N.(UIMouseMoveEvent) -> Unit) {
    mouseMoveEventHandlers.add { fn(this, it) }
}

fun <N: UINode> N.onMouseDrag(fn: N.(UIMouseDragEvent) -> Unit) {
    mouseDragEventHandlers.add { fn(this, it) }
}

fun <C: UINode> UINode.addChild(child: C, init: C.() -> Unit = {}): C {
    child.parent = this
    init(child)
    return child
}

fun <C: UINode> UINode.removeChild(child: C): C {
    if (child.parent == this) child.parent = null
    return child
}

fun <B: Background> UINode.addBackground(background: B, init: B.() -> Unit = {}): B {
    init(background)
    backgroundsInternal.add(background)
    return background
}

fun <F: Foreground> UINode.addForeground(foreground: F, init: F.() -> Unit = {}): F {
    init(foreground)
    foregroundsInternal.add(foreground)
    return foreground
}

fun <B: Background> UINode.removeBackground(background: B): B {
    if (backgroundsInternal.contains(background)) backgroundsInternal.remove(background)
    return background
}

fun <F: Foreground> UINode.removeForeground(foreground: F): F {
    if (foregroundsInternal.contains(foreground)) foregroundsInternal.remove(foreground)
    return foreground
}

fun <B: Background> UINode.replaceBackground(old: Background, new: B): B {
    removeBackground(old)
    return addBackground(new)
}

fun <F: Foreground> UINode.replaceForeground(old: Foreground, new: F): F {
    removeForeground(old)
    return addForeground(new)
}

package ktaf.ui

import ktaf.core.minus
import ktaf.graphics.DrawContext2D
import ktaf.core.plus
import ktaf.core.vec2
import ktaf.ui.layout.ListLayout
import ktaf.ui.layout.UILayout
import ktaf.util.Animation
import ktaf.util.AnimationProperties
import ktaf.util.Easing
import lwjglkt.GLFWCursor
import kotlin.properties.Delegates

abstract class UINode: UI_t {
    // structure
    internal var sceneInternal: UIScene? = null
    internal val childrenInternal = mutableListOf<UINode>()
    val scene get() = sceneInternal
    val children get() = childrenInternal.toList()
    var parent by property(null as UINode?)

    // configuration
    internal val foregroundsInternal = mutableListOf<Foreground>()
    internal val backgroundsInternal = mutableListOf<Background>()
    internal open var fillAllocatedSize = true
    internal open val cursor: GLFWCursor? = GLFWCursor.DEFAULT

    var margin by property(Border(0f))
    var padding by property(Border(0f))
    var layout by property(ListLayout() as UILayout)
    var width by property(null as Float?)
    var height by property(null as Float?)

    // callbacks
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

    // state
    private val positionAnimationProperties = AnimationProperties(Animation.NORMAL, Easing.SMOOTH, Animation.Float)
    internal var mouseInside = false
    internal var computedXInternal: Float by Delegates.observable(0f) { _, old, new -> if (old != new) scene?.animate2(this, "computedXInternal", Animation(
            computedXAnimated, new, positionAnimationProperties.duration, positionAnimationProperties.easing, positionAnimationProperties.eval
    ) { computedXAnimated = it } ) }
    internal var computedYInternal: Float by Delegates.observable(0f) { _, old, new -> if (old != new) scene?.animate2(this, "computedYInternal", Animation(
            computedYAnimated, new, positionAnimationProperties.duration, positionAnimationProperties.easing, positionAnimationProperties.eval
    ) { computedYAnimated = it } ) }
    internal var computedWidthInternal: Float by Delegates.observable(0f) { _, old, new -> if (old != new) scene?.animate2(this, "computedWidthInternal", Animation(
            computedWidthAnimated, new, positionAnimationProperties.duration, positionAnimationProperties.easing, positionAnimationProperties.eval
    ) { computedWidthAnimated = it } ) }
    internal var computedHeightInternal: Float by Delegates.observable(0f) { _, old, new -> if (old != new) scene?.animate2(this, "computedHeightInternal", Animation(
            computedHeightAnimated, new, positionAnimationProperties.duration, positionAnimationProperties.easing, positionAnimationProperties.eval
    ) { computedHeightAnimated = it } ) }
    internal var computedXAnimated: Float = 0f
    internal var computedYAnimated: Float = 0f
    internal var computedWidthAnimated: Float = 0f
    internal var computedHeightAnimated: Float = 0f

    init {
        p(::parent) {
            attachChangeCallback { old, new ->
                old?.childrenInternal?.remove(this@UINode)
                new?.childrenInternal?.add(this@UINode)
                this@UINode.sceneInternal = new?.sceneInternal
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
                    position + padding.tl + vec2(it.computedXAnimated, it.computedYAnimated),
                    vec2(it.computedWidthAnimated, it.computedHeightAnimated)
            )
        }

        foregroundsInternal.forEach {
            it.draw(context, position + padding.tl, size - padding.size)
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
            it.handleEvent(event.relativeTo(padding.tl + vec2(it.computedXAnimated, it.computedYAnimated)))
        }
    }
}

fun UINode.fill() { fillAllocatedSize = true }
fun UINode.shrink() { fillAllocatedSize = false }

fun UINode.absolutePosition(): vec2
        = (parent?.absolutePosition() ?: vec2(0f)) + (parent?.padding?.tl ?: vec2(0f)) + vec2(computedXAnimated, computedYAnimated)

fun UINode.requestFocus() { sceneInternal?.focusOn(this) }
fun UINode.unfocus() { if (isFocused()) sceneInternal?.unfocus() }
fun UINode.isFocused() = sceneInternal?.focussedNode == this

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

fun <B: Background> UINode.removeBackground(background: B): B {
    if (backgroundsInternal.contains(background)) backgroundsInternal.remove(background)
    return background
}

fun <B: Background> UINode.replaceBackground(old: Background, new: B): B {
    removeBackground(old)
    return addBackground(new)
}

fun <F: Foreground> UINode.addForeground(foreground: F, init: F.() -> Unit = {}): F {
    init(foreground)
    foregroundsInternal.add(foreground)
    return foreground
}

fun <F: Foreground> UINode.removeForeground(foreground: F): F {
    if (foregroundsInternal.contains(foreground)) foregroundsInternal.remove(foreground)
    return foreground
}

fun <F: Foreground> UINode.replaceForeground(old: Foreground, new: F): F {
    removeForeground(old)
    return addForeground(new)
}

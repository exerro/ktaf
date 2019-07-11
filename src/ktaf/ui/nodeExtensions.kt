package ktaf.ui

import ktaf.core.vec2
import ktaf.typeclass.plus

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Positioning                                                                                                        //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

fun UINode.fill() { fillAllocatedSize = true }
fun UINode.shrink() { fillAllocatedSize = false }

fun UINode.absolutePosition(): vec2
        = (parent.get()?.absolutePosition() ?: vec2(0f)) + (parent.get()?.padding?.get()?.tl ?: vec2(0f)) + vec2(computedX.get(), computedY.get())

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Keyboard focus                                                                                                     //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

fun UINode.requestFocus() { scene.get()?.focussedNode?.set(this) }
fun UINode.unfocus() { if (isFocused()) scene.get()?.focussedNode?.set(null) }
fun UINode.isFocused() = scene.get()?.focussedNode?.get() == this

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Child control                                                                                                      //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

fun <C: UINode> UINode.addChild(child: C, init: C.() -> Unit = {}): C {
    child.parent.set(this)
    init(child)
    return child
}

fun <C: UINode> UINode.removeChild(child: C): C {
    if (child.parent == this) child.parent.set(null)
    return child
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Foreground/background control                                                                                      //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

package ktaf.gui.core

import geometry.plus
import geometry.times
import ktaf.data.property.AnimatedProperty
import ktaf.data.property.MutableProperty
import ktaf.data.property.mutableProperty
import ktaf.data.property.vec4AnimatedProperty
import ktaf.graphics.RGBA

fun colourProperty(initial: RGBA, fn: AnimatedProperty<RGBA>.() -> Unit = {})
        : AnimatedProperty<RGBA> = vec4AnimatedProperty(initial, fn)

fun paddingProperty(fn: MutableProperty<Padding>.() -> Unit = {})
        : MutableProperty<Padding> = MutableProperty(Padding(0f)).also(fn)

fun alignment1DProperty(initial: Alignment1D, fn: MutableProperty<Alignment1D>.() -> Unit = {})
        : MutableProperty<Alignment1D> = mutableProperty(initial).also(fn)

fun alignment2DProperty(initial: Alignment2D, fn: MutableProperty<Alignment2D>.() -> Unit = {})
        : MutableProperty<Alignment2D> = mutableProperty(initial).also(fn)

fun animatedAlignment2DProperty(initial: Alignment2D, fn: AnimatedProperty<Alignment2D>.() -> Unit = {})
        : AnimatedProperty<Alignment2D> = AnimatedProperty(initial) { a, b, t -> a * (1 - t) + b * t }

fun spacingProperty(initial: Spacing = Spacing.NONE, fn: MutableProperty<Spacing>.() -> Unit = {})
        : MutableProperty<Spacing> = mutableProperty(initial).also(fn)

package ktaf.typeclass

import geometry.*

interface Animateable<T>: Add<T, T>, Sub<T, T>, Mul<Float, T>

fun <T: Animateable<T>> T.transitionTo(other: T, t: Float)
        = this + (other - this) * t

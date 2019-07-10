package ktaf.typeclass

interface Animateable<T>: Add<T, T>, Sub<T, T>, Mul<Float, T>

fun <T: Animateable<T>> T.lerpTo(other: T, factor: Float)
        = this + (other - this) * factor

package ktaf.property

/** A float ranging between 0 and 1 inclusive. */
typealias AnimationParameter = Float

class Animation<T>(
        val begin: T,
        val end: T,
        val duration: Float,
        val easing: EasingFunction,
        val lerp: (T, T, AnimationParameter) -> T
) {
    var current: T = begin
        private set

    fun update(dt: Float) {
        clock += dt

        if (clock > duration) clock = duration

        current = lerp(begin, end, easing(clock / duration))
    }

    fun finished()
            = clock == duration

    private var clock = 0f
}

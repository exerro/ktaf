package ktaf.util

data class AnimationProperties<T>(
    var duration: Float,
    var easing: EasingFunction,
    var eval: AnimationEvaluator<T>
)

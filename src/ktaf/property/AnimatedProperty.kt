package ktaf.property

import geometry.*
import ktaf.core.debug

fun floatAnimatedProperty(value: Float, fn: AnimatedProperty<Float>.() -> Unit = {})
        = AnimatedProperty(value) { a, b, t -> a * (1 - t) + b * t } .also(fn)

fun vec3AnimatedProperty(value: vec2, fn: AnimatedProperty<vec2>.() -> Unit = {})
        = AnimatedProperty(value) { a, b, t -> a * (1 - t) + b * t } .also(fn)

fun vec3AnimatedProperty(value: vec3, fn: AnimatedProperty<vec3>.() -> Unit = {})
        = AnimatedProperty(value) { a, b, t -> a * (1 - t) + b * t } .also(fn)

fun vec4AnimatedProperty(value: vec4, fn: AnimatedProperty<vec4>.() -> Unit = {})
        = AnimatedProperty(value) { a, b, t -> a * (1 - t) + b * t } .also(fn)

//////////////////////////////////////////////////////////////////////////////////////////

class AnimatedProperty<T>(
        value: T,
        private val lerp: (T, T, AnimationParameter) -> T
): MutableProperty<T>(value) {
    var duration = 0.3f
    var easing = EasingFunctions.smooth

    fun setWithoutAnimation(value: T) {
        animation = null
        rawTargetValue = value
        rawValue = value
        onChangeEvent.emit(true)
    }

    fun update(dt: Float) {
        val a = animation

        if (a != null) {
            a.update(dt)
            if (a.finished()) animation = null
            rawValue = a.current
            onChangeEvent.emit(false)
        }
    }

    override val onChangeEvent = OnChangeEvent()

    override fun setValueInternal(value: T, animate: Boolean) {
        if (value != rawTargetValue) {
            debug("ktaf.experimental") {
                println("Value changed from $rawValue to $value on $this, " +
                        if (animate) "animating" else "setting")
            }

            rawTargetValue = value

            if (animate) {
                animation = Animation(rawValue, value, duration, easing, lerp)
            }
            else {
                animation = null
                rawValue = value
                onChangeEvent.emit(false)
            }
        }
    }

    override fun toString() = "AnimatedProperty($value, target=$rawTargetValue, duration=$duration)"

    private var animation: Animation<T>? = null
    private var rawTargetValue: T = rawValue
}

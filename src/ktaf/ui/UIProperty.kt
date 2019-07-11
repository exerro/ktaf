package ktaf.ui

import ktaf.core.KTAFMutableValue
import ktaf.typeclass.Animateable
import ktaf.util.Animation
import ktaf.util.Easing
import ktaf.util.EasingFunction

typealias UINodeState = String
const val DEFAULT_STATE: UINodeState = "default"

open class UIProperty<T>(value: T): KTAFMutableValue<T>(value) {
    private val stateValues: MutableMap<UINodeState, T> = mutableMapOf(DEFAULT_STATE to value)
    private var activeState = DEFAULT_STATE

    override fun set(value: T) {
        TODO()
    }

    fun setState(state: UINodeState) {
        if (activeState != state) {
            activeState = state
            stateValues[state]?.let(::updateValue)
        }
    }

    protected open fun updateValue(value: T) {
        super.set(value)
    }

    operator fun invoke(value: T) {
        this[DEFAULT_STATE](value)
    }

    operator fun get(state: UINodeState) = { value: T ->
        stateValues[state] = value
        if (activeState == state || state == DEFAULT_STATE && !stateValues.containsKey(activeState)) updateValue(value)
    }
}

class UIAnimatedProperty<T: Animateable<T>, N: UINode>(value: T,
                            private val node: N,
                            private val property: String,
                            var duration: Float = Animation.NORMAL,
                            var easing: EasingFunction = Easing.SMOOTH
): UIProperty<T>(value) {
    override fun updateValue(value: T) {
        val scene = node.scene.get()

        if (scene == null) {
            super.updateValue(value)
        }
        else {
            scene.animate(node, property, this, value, duration, easing)
        }
    }
}

fun KTAFMutableValue<UINodeState>.clear() = set(DEFAULT_STATE)

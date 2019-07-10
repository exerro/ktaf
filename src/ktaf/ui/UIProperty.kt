package ktaf.ui

import ktaf.KTAFMutableValue
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
        set(value, DEFAULT_STATE)
    }

    fun get(state: UINodeState): T = stateValues[state] ?: stateValues[DEFAULT_STATE]!!

    fun set(value: T, state: UINodeState) {
        stateValues[state] = value
        if (activeState == state || state == DEFAULT_STATE && !stateValues.containsKey(activeState)) updateValue(value)
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

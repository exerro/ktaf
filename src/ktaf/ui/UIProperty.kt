package ktaf.ui

import ktaf.core.KTAFValue
import ktaf.typeclass.Animateable
import ktaf.ui.node.UINode
import ktaf.util.Animation
import ktaf.util.Easing
import ktaf.util.EasingFunction
import ktaf.util.animate

typealias UINodeState = String
const val DEFAULT_STATE: UINodeState = "default"

open class UIProperty<T>(value: T,
        private val setterCallback: UIProperty<T>.(T) -> Unit = { v -> stateValues.keys.map { this[it](v) } }
): KTAFValue<T>(value) {
    internal val stateValues: MutableMap<UINodeState, T> = mutableMapOf(DEFAULT_STATE to value)
    private var activeState = DEFAULT_STATE

    fun setState(state: UINodeState) {
        if (activeState != state) {
            activeState = state
            (stateValues[state] ?: stateValues[DEFAULT_STATE]) ?. let(::updateValue)
        }
    }

    protected open fun updateValue(value: T) {
        super.set(value, {})
    }

    operator fun get(state: UINodeState) = { value: T ->
        stateValues[state] = value
        if (activeState == state || state == DEFAULT_STATE && !stateValues.containsKey(activeState)) updateValue(value)
    }

    override fun <TT : T> set(value: TT, init: TT.() -> Unit): TT {
        init(value)
        setterCallback(value)
        return value
    }
}

class UIAnimatedProperty<T: Animateable<T>, out N: UINode>(value: T,
                                                           private val node: N,
                                                           private val property: String,
                                                           var duration: Float = Animation.NORMAL,
                                                           var easing: EasingFunction = Easing.SMOOTH,
                                                           setter: UIProperty<T>.(T) -> Unit = { v -> stateValues.keys.map { this[it](v) } }
): UIProperty<T>(value, setter) {
    override fun updateValue(value: T) {
        val scene = node.scene.get()

        if (scene == null) {
            super.updateValue(value)
        }
        else {
            scene.animations.animate(node, property, this, value, duration, easing)
        }
    }
}

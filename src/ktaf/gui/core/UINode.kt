package ktaf.gui.core

import ktaf.graphics_old.DrawCtx
import observables.Observable
import observables.Signal

abstract class UINode {
    val widthProperty = StyledProperty(null as Int?)
    val heightProperty = StyledProperty(null as Int?)
    var width: Int? by widthProperty.delegate
    var height: Int? by heightProperty.delegate

    val parent: Observable<UIParent?> = Observable(null)
    val scene: Observable<UIScene?> = Observable(null)

    val stateChanged = Signal<Set<Property.State>>()
    private val states: MutableSet<Property.State> = mutableSetOf()

    fun pushState(state: Property.State) {
        if (states.add(state)) stateChanged.emit(states)
    }

    fun popState(state: Property.State) {
        if (states.remove(state)) stateChanged.emit(states)
    }

    fun hasState(state: Property.State) = states.contains(state)

    abstract fun draw(context: DrawCtx)
}

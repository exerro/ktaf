package ktaf.gui.core

enum class State {
    Default, Active,
    Hover, ActiveHover,
    Pressed, ActivePressed
}

val allStates = State.values().toList()
val activeStates = State.values().filter { it.active }
val inactiveStates = State.values().filter { !it.active }
val hoverStates = State.values().filter { it.hover }
val nonHoverStates = State.values().filter { !it.hover }
val pressedStates = State.values().filter { it.pressed }
val unpressedStates = State.values().filter { !it.pressed }

val State.active get() = when (this) {
    State.Active -> true
    State.ActiveHover -> true
    State.ActivePressed -> true
    else -> false
}

val State.hover get() = when (this) {
    State.Hover -> true
    State.ActiveHover -> true
    else -> false
}

val State.pressed get() = when (this) {
    State.Pressed -> true
    State.ActivePressed -> true
    else -> false
}

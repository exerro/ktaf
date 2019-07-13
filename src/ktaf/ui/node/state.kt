package ktaf.ui.node

import ktaf.core.KTAFMutableValue
import ktaf.ui.DEFAULT_STATE
import ktaf.ui.UINodeState

fun KTAFMutableValue<List<UINodeState>>.push(state: UINodeState) = set(get() + state)
fun KTAFMutableValue<List<UINodeState>>.remove(state: UINodeState) = set(get() - state)
fun KTAFMutableValue<List<UINodeState>>.pop() = set(get().dropLast(1).toMutableList())
fun KTAFMutableValue<List<UINodeState>>.clear() = set(mutableListOf())
fun KTAFMutableValue<List<UINodeState>>.current() = get().lastOrNull() ?: DEFAULT_STATE

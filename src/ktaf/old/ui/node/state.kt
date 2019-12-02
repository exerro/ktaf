package ktaf.ui.node

import ktaf.core.KTAFValue
import ktaf.ui.DEFAULT_STATE
import ktaf.ui.UINodeState

fun KTAFValue<List<UINodeState>>.push(state: UINodeState) = set(get() + state)
fun KTAFValue<List<UINodeState>>.remove(state: UINodeState) = set(get() - state)
fun KTAFValue<List<UINodeState>>.pop() = set(get().dropLast(1).toMutableList())
fun KTAFValue<List<UINodeState>>.clear() = set(mutableListOf())
fun KTAFValue<List<UINodeState>>.current() = get().lastOrNull() ?: DEFAULT_STATE

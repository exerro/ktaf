package ktaf.ui.node

fun UINode.requestFocus() { scene.get()?.focussedNode?.set(this) }
fun UINode.unfocus() { if (isFocused()) scene.get()?.focussedNode?.set(null) }
fun UINode.isFocused() = scene.get()?.focussedNode?.get() == this

package ktaf.gui.core

/** Interface on which nodes define constructor extension methods
 *
 *  Extensions should allow the following usage:
 *
 *  name(parameters...) {
 *      property.value = ...
 *  } */
interface GUIBuilderContext

/** A generic GUIBuilderContext that may be used. */
object GUIBuilder: GUIBuilderContext

fun <T> gui(fn: GUIBuilderContext.() -> T) = fn(GUIBuilder)

package ktaf.gui.core

/** An object that positions children. */
interface Positioner {
    /** Calculates the widths of children, given a width available to this
     *  object. */
    fun calculateChildrenWidths(availableWidth: Float)

    /** Calculates the heights of children, given a height available to this
     *  object.
     *
     *  Will be called after calculating widths. */
    fun calculateChildrenHeights(availableHeight: Float?)

    /** Positions the children.
     *
     *  Will be called after calculating sizes. */
    fun positionChildren()
}
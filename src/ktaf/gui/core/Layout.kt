package ktaf.gui.core

import geometry.vec2

abstract class Layout {
    /** Calculates the widths of children, given a width available to this
     *  object. */
    abstract fun calculateChildrenWidths(children: List<UINode>, availableWidth: Float)

    /** Calculates the heights of children, given a height available to this
     *  object.
     *
     *  Will be called after calculating widths. */
    abstract fun calculateChildrenHeights(children: List<UINode>, availableHeight: Float?)

    /** Positions the children.
     *
     *  Will be called after calculating sizes. */
    abstract fun positionChildren(children: List<UINode>, offset: vec2, size: vec2)
}

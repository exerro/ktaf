package ktaf.gui.core

interface UINodePositioning {
    /** Return an optional width the node should fall to if not filling its
     *  parent and with no fixed width set.
     *
     *  Called from calculateWidth() if necessary.
     *  Note: for parents, calculateChildrenWidths() will already have been
     *        called, so child widths can be used here. */
    fun getDefaultWidth(): Float?

    /** Return an optional height the node should fall to if not filling its
     *  parent and with no fixed height set.
     *
     *  @param width: calculated width of the node.
     *
     *  Called from calculateHeight() if necessary.
     *  Note: for parents, calculateChildrenHeights() will already have been
     *        called, so child sizes can be used here. */
    fun getDefaultHeight(width: Float): Float?
}
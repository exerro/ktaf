package ktaf.ui.elements

import ktaf.core.KTAFList
import ktaf.core.KTAFValue
import ktaf.core.joinTo
import ktaf.ui.layout.ListLayout
import ktaf.ui.layout.Spacing
import ktaf.ui.node.UIContainer
import ktaf.ui.node.UINode

class UIList<T>(
        val items: KTAFList<T>,
        private val createItem: (T) -> UINode
): UIContainer() {
    val listLayout = ListLayout()
    val spacing = KTAFValue(Spacing.fixed(16f))
    val alignment = KTAFValue(0.5f)

    init {
        layout(listLayout)

        spacing.joinTo(listLayout.spacing)
        alignment.joinTo(listLayout.alignment)

        items.connectAddedIndexed { i, item -> children.add(i, createItem(item)) }
        items.connectRemovedIndexed { i, _ -> children.removeAt(i) }
    }
}

package ktaf.gui.elements

import ktaf.gui.core.*
import ktaf.gui.layouts.VerticalListLayout
import java.lang.Float.max
import kotlin.collections.List

fun UIContainer.labeledPanel(label: String, fn: LabeledPanel.() -> Unit = {})
        = addChild(LabeledPanel(label)).also(fn)

fun GUIBuilderContext.labeledPanel(label: String, fn: LabeledPanel.() -> Unit = {})
        = LabeledPanel(label).also(fn)

////////////////////////////////////////////////////////////////////////////////

class LabeledPanel(label: String): Proxy<UINode>(LabeledPanelInternal(label)), UIContainer {
    override val children: List<UINode>
        get() = content.children

    override fun <T : UINode> addChild(child: T): T
            = content.addChild(child)

    override fun <T : UINode> addChild(index: Int, child: T): T
            = content.addChild(index, child)

    override fun <T : UINode> removeChild(child: T): T
            = content.removeChild(child)

    override fun clearChildren()
            = content.clearChildren()

    ////////////////////////////////////////////////////////////////////////////

    val label: Label
    private val content: Stack

    init {
        val i = node as LabeledPanelInternal
        this.label = i.label
        content = i.content
    }
}

private class LabeledPanelInternal(title: String): UIParent() {
    val label = addChild(gui { label(title) })
    val content = addChild(gui { stack() })

    override val layout = VerticalListLayout()

    override fun getDefaultWidth(): Float?
            = listOfNotNull(label.getDefaultWidth(), content.getDefaultWidth())
            .fold(0f, ::max)

    override fun getDefaultHeight(width: Float): Float?
            = listOfNotNull(label.getDefaultHeight(width), content.getDefaultHeight(width)).sum()
}

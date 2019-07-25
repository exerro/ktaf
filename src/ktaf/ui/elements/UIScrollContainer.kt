package ktaf.ui.elements

import ktaf.core.KTAFValue
import ktaf.core.joinTo
import ktaf.core.vec2
import ktaf.graphics.DrawContext2D
import ktaf.typeclass.minus
import ktaf.typeclass.plus
import ktaf.ui.UIAnimatedProperty
import ktaf.ui.layout.HDivLayout
import ktaf.ui.layout.height
import ktaf.ui.layout.pc
import ktaf.ui.layout.px
import ktaf.ui.node.UIContainer
import ktaf.ui.node.UINode
import ktaf.util.AABB
import kotlin.math.max
import kotlin.math.min

open class UIScrollContainer: UIContainer() {
    // TODO: support horizontal scrolling
    //    private val left = children.add(UIContainer()) {}
    //    val content = left.children.add(UIContainer())
    //    val scrollbarX = left.children.add(UISlider())

    val scroll = UIAnimatedProperty(vec2(0f), this, "scrollX")
    val content = children.add(UIScrollContainerContent(scroll)) {}
    val scrollbarY = children.add(UISlider()) { direction(UISliderDirection.VERTICAL) }
    val scrollX = KTAFValue(0f)
    val scrollY = KTAFValue(0f)

    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        context.push {
            context.scissor = AABB(position, position + size)
            super.draw(context, position, size)
        }
    }

    override fun handlesScroll() = true

    init {
        // scrollbars <-> scroll members
//        scrollbarX.x.joinTo(scrollX)
        scrollbarY.y.joinTo(scrollY)

        // scrollX/Y <-> scroll
        scrollX.connect { scroll.setValue(vec2(it, scroll.get().y)) }
        scrollY.connect { scroll.setValue(vec2(scroll.get().x, it)) }
        scroll.connect { (x, y) -> scrollX(x); scrollY(y) }

        // update vertical scrollbar size on various triggers
        content.computedHeight.connect { updateVerticalScrollbar() }
        computedHeight.connect { updateVerticalScrollbar() }
        scrollbarY.computedHeight.connect { updateVerticalScrollbar() }
        scrollbarY.padding.connect { updateVerticalScrollbar() }

        layout(HDivLayout(0.px())) {
            alignment(vec2(0f))
            scrollbarY.computedWidth.connect { sections[0](100.pc() - it.px()) }
        }

        onMouseScroll { event ->
            scrollY(scrollY.get() - event.direction.y * 40f)
        }
    }

    private fun updateVerticalScrollbar() {
        val ratio = computedHeight.get() / content.computedHeight.get()
        scrollbarY.sliderHeight((scrollbarY.computedHeight.get() - scrollbarY.padding.get().height) * max(0.15f, min(1f, ratio)))
        scrollbarY.yMax(max(0f, content.computedHeight.get() - computedHeight.get()))
    }
}

class UIScrollContainerContent(private val scroll: KTAFValue<vec2>): UIContainer() {
    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        super.draw(context, position - scroll.get(), size)
    }

    override fun getMouseHandler(position: vec2): UINode? {
        return super.getMouseHandler(position + scroll.get())
    }

    init {
        fill(false)
    }
}

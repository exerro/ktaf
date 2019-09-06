package ktaf.ui.elements

import geometry.minus
import geometry.plus
import geometry.vec2
import ktaf.core.KTAFValue
import ktaf.core.joinTo
import ktaf.graphics.DrawContext2D
import ktaf.ui.UIProperty
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

    val scroll = UIProperty(vec2(0f))
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
        content.currentComputedHeight.connect { updateVerticalScrollbar() }
        currentComputedHeight.connect { updateVerticalScrollbar() }
        scrollbarY.currentComputedHeight.connect { updateVerticalScrollbar() }
        scrollbarY.padding.connect { updateVerticalScrollbar() }

        layout(HDivLayout(0.px())) {
            alignment(vec2(0f))
            scrollbarY.currentComputedWidth.connect { sections[0](100.pc() - it.px()) }
        }

        onMouseScroll.connect { event ->
            scrollY(scrollY.get() - event.direction.y * 40f)
        }
    }

    private fun updateVerticalScrollbar() {
        val ratio = currentComputedHeight.get() / content.currentComputedHeight.get()
        scrollbarY.sliderHeight((scrollbarY.currentComputedHeight.get() - scrollbarY.padding.get().height) * max(0.15f, min(1f, ratio)))
        scrollbarY.yMax(max(0f, content.currentComputedHeight.get() - currentComputedHeight.get()))
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

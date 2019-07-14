import ktaf.core.*
import ktaf.ui.elements.*
import ktaf.ui.layout.*
import ktaf.ui.node.UIContainer
import ktaf.ui.node.UINode
import kotlin.math.max

fun positionDemo(): UINode {
    return UIContainer().apply {
        val tabs = children.add(UIContainer()) { layout(HDivLayout()) }
        val lower = children.add(UIView()) {}

        lower.horizontal()

        fun <T: UILayout> addPositionDemo(title: String, layout: T, fn: PositioningDemo<T>.() -> Unit) {
            val demo = PositioningDemo(layout)
            val button = tabs.children.add(UIButton(title)) {}
            val display = lower.children.add(demo.container) {}

            fn(demo)

            button.colour(Colour.orange)
            button.onClick { lower.show(display) }
        }

        addPositionDemo("List", ListLayout()) {
            val listLayout = layout
            val fillMode = KTAFValue(false)

            content.children.connectAdded { when (it) {
                is UIButton -> it.text("Button ${content.children.indexOf(it) + 1}")
                else -> Unit
            } }

            content.children.connectAdded { fillMode.connect(it.fill::setter) }
            content.children.connectAdded { it.onMouseClick { _ -> it.parent(null) } }

            content.children.connectRemoved { content.children.forEach { when (it) {
                is UIButton -> it.text("Button ${content.children.indexOf(it) + 1}")
            } } }

            (1 .. 10).forEach { _ -> content.children.add(UIButton("")) }

            controls.children.add(labelled("Alignment", UISlider(), {
                padding(Border(6f, 12f))
                direction(UISliderDirection.HORIZONTAL)
                height(32f)
                sliderWidth(64f)
                x.connect(listLayout.alignment::setter)
                x.connect { fillMode(false) }
            }))

            controls.children.add(spacingControls("Spacing", listLayout.spacing))

            controls.children.add(labelled("Button margins", UISlider(), {
                padding(Border(6f, 12f))
                direction(UISliderDirection.HORIZONTAL)
                height(32f)
                sliderWidth(64f)
                x.connect { content.children.forEach { it.margin(Border(x.get() * 32f)) } }
                content.children.connectAdded { it.margin(Border(x.get() * 32f)) }
            }))

            controls.children.add(UIContainer()) {
                children.add(UIButton("Add item")) {
                    fill(false)
                    padding(Border(12f, 24f))
                    onClick { content.children.add(UIButton("")) }
                }

                children.add(UIButton("Resize items")) {
                    fill(false)
                    padding(Border(12f, 24f))
                    onClick { content.children.forEach { it.height(Math.random().toFloat() * 48f + 32f) } }
                    onClick { content.children.forEach { it.width(Math.random().toFloat() * 80f + 96f) } }
                    onClick { fillMode(false) }
                }

                children.add(UIButton("Toggle fill")) {
                    fill(false)
                    padding(Border(12f, 24f))
                    onClick { fillMode(!fillMode.get()) }
                    onClick { if (fillMode.get()) content.children.forEach { it.width(null) } }

                    fillMode.connect { colour(if (it) Colour.green else Colour.red) }
                }

                layout(HDivLayout())
            }
        }

        addPositionDemo("Flow", FlowLayout()) {
            val flowLayout = layout

            content.children.connectAdded { when (it) {
                is UIButton -> it.text("Button ${content.children.indexOf(it) + 1}")
                else -> Unit
            } }

            content.children.connectAdded { it.fill(false) }
            content.children.connectAdded { it.onMouseClick { _ -> it.parent(null) } }

            content.children.connectRemoved { content.children.forEach { when (it) {
                is UIButton -> it.text("Button ${content.children.indexOf(it) + 1}")
            } } }

            (1 .. 10).forEach { _ -> content.children.add(UIButton("")) }

            controls.children.add(labelled("Vertical alignment", UISlider(), {
                padding(Border(6f, 12f))
                direction(UISliderDirection.HORIZONTAL)
                height(32f)
                sliderWidth(64f)
                x.connect(flowLayout.verticalAlignment::setter)
                x.connect { content.children.forEach { it.fill(false) } }
            }))

            controls.children.add(spacingControls("Vertical spacing", flowLayout.verticalSpacing))
            controls.children.add(spacingControls("Horizontal spacing", flowLayout.horizontalSpacing))

            controls.children.add(labelled("Fixed horizontal/vertical spacing", UISlider(), {
                padding(Border(6f, 12f))
                direction(UISliderDirection.HORIZONTAL)
                height(32f)
                sliderWidth(64f)
                x.connect { flowLayout.horizontalSpacing(Spacing.fixed(it * 16f)) }
                x.connect { flowLayout.verticalSpacing(Spacing.fixed(it * 16f)) }
            }))

            controls.children.add(labelled("Button margins", UISlider(), {
                padding(Border(6f, 12f))
                direction(UISliderDirection.HORIZONTAL)
                height(32f)
                sliderWidth(64f)
                x.connect { content.children.forEach { it.margin(Border(x.get() * 16f)) } }
                content.children.connectAdded { it.margin(Border(x.get() * 16f)) }
            }))

            controls.children.add(UIContainer()) {
                children.add(UIButton("Add item")) {
                    fill(false)
                    padding(Border(12f, 24f))
                    onClick { content.children.add(UIButton("")) }
                }

                children.add(UIButton("Resize items")) {
                    fill(false)
                    padding(Border(12f, 24f))
                    onClick { content.children.forEach { it.height(Math.random().toFloat() * 64f + 32f) } }
                    onClick { content.children.forEach { it.width(Math.random().toFloat() * 128f + 96f) } }
                }

//                children.add(UIButton("Toggle fill")) {
//                    fill(false)
//                    padding(Border(12f, 24f))
//                    onClick { content.children.forEach { it.fill(!it.fill.get()) } }
//                }

                layout(HDivLayout())
            }
        }

        addPositionDemo("Grid", GridLayout()) {
            val gridLayout = layout
            val fillMode = KTAFValue(false)

            content.children.connectAdded { when (it) {
                is UIButton -> it.text("Button ${content.children.indexOf(it) + 1}")
                else -> Unit
            } }

            content.children.connectAdded { fillMode.connect(it.fill::setter) }
            content.children.connectAdded { it.onMouseClick { _ -> it.parent(null) } }

            content.children.connectRemoved { content.children.forEach { when (it) {
                is UIButton -> it.text("Button ${content.children.indexOf(it) + 1}")
            } } }

            (1 .. 10).forEach { _ -> content.children.add(UIButton("")) }

            controls.children.add(labelled("Alignment", UISlider(), {
                padding(Border(6f, 12f))
                direction(UISliderDirection.BOTH)
                height(96f)
                sliderWidth(64f)
                value.connect(gridLayout.alignment::setter)
                value.connect { fillMode(false) }
            }))

            controls.children.add(labelled("Spacing", UIContainer()) {
                layout(ListLayout())

                fun slider(label: String) = hlabelled(children, label, UISlider()) {
                    height(32f)
                    backgroundColour(rgba(0.8f))
                    sliderWidth(64f)
                    padding(Border(6f, 12f))
                }

                val horizontal = slider("Horizontal")
                val vertical = slider("Vertical")

                horizontal.x.connect { gridLayout.spacing(vec2(horizontal.x.get(), vertical.x.get()) * vec2(48f)) }
                vertical.x.connect { gridLayout.spacing(vec2(horizontal.x.get(), vertical.x.get()) * vec2(48f)) }
            })

            controls.children.add(labelled("Sizing", UIContainer(), {
                padding(Border(12f))
                colour(rgba(0.8f))

                children.add(UIButton("Add row")) { onClick { gridLayout.rows(gridLayout.rows.get() + 1) } }
                children.add(UIButton("Add column")) { onClick { gridLayout.columns(gridLayout.columns.get() + 1) } }
                children.add(UIButton("Remove row")) { onClick { gridLayout.rows(max(1, gridLayout.rows.get() - 1)) } }
                children.add(UIButton("Remove column")) { onClick { gridLayout.columns(max(1, gridLayout.columns.get() - 1)) } }

                layout(FlowLayout()) {
                    horizontalSpacing(Spacing.fixed(16f))
                    verticalSpacing(Spacing.fixed(8f))
                }
            }))

            controls.children.add(labelled("Button margins", UISlider(), {
                padding(Border(6f, 12f))
                direction(UISliderDirection.HORIZONTAL)
                height(32f)
                sliderWidth(64f)
                x.connect { content.children.forEach { it.margin(Border(x.get() * 16f)) } }
                content.children.connectAdded { it.margin(Border(x.get() * 16f)) }
            }))

            controls.children.add(UIContainer()) {
                children.add(UIButton("Add item")) {
                    fill(false)
                    padding(Border(12f, 24f))
                    onClick { content.children.add(UIButton("")) }
                }

                children.add(UIButton("Resize items")) {
                    fill(false)
                    padding(Border(12f, 24f))
                    onClick { content.children.forEach { it.height(Math.random().toFloat() * 64f + 32f) } }
                    onClick { content.children.forEach { it.width(Math.random().toFloat() * 64f + 80f) } }
                    onClick { fillMode(false) }
                }

                children.add(UIButton("Toggle fill")) {
                    fill(false)
                    padding(Border(12f, 24f))
                    onClick { fillMode(!fillMode.get()) }
                    onClick { if (fillMode.get()) content.children.forEach { it.width(null) } }
                    onClick { if (fillMode.get()) content.children.forEach { it.height(null) } }

                    fillMode.connect { colour(if (it) Colour.green else Colour.red) }
                }

                layout(HDivLayout())
            }
        }

        layout(AreaLayout()) {
            areas {
                vsplit(64.px()) { labels("tabs", "content") }
            }

            elem(tabs, "tabs")
            elem(lower, "content")
        }
    }
}

private fun <T: UINode> labelled(label: String, elem: T, fn: T.() -> Unit): UIContainer {
    return UIContainer().apply {
        children.add(UILabel(label)) {
            colour(rgba(0.2f))
            textColour(rgba(1f))
        }
        children.add(elem, fn)
        layout(ListLayout())
    }
}

private fun <T: UINode> hlabelled(children: KTAFList<UINode>, label: String, elem: T, fn: T.() -> Unit): T {
    children.add(UIContainer()) {
        height(32f)

        this.children.add(UILabel(label)) {
            colour(rgba(0.3f))
            textColour(rgba(1f))
            alignment(vec2(1f, 0.5f))
        }
        this.children.add(elem, fn)

        layout(HDivLayout(30.pc()))
    }

    return elem
}

private fun spacingControls(label: String, spacing: KTAFValue<Spacing>): UIContainer {
    return labelled(label, UIContainer()) {
        padding(Border(12f))
        colour(rgba(0.8f))
        layout(ListLayout()) { this.spacing(Spacing.fixed(16f)) }

        children.add(labelled("Built-in", UIContainer()) {
            padding(Border(8f))
            colour(rgba(0.9f))

            children.add(UIButton("SPACE_AFTER")) { onClick { spacing(Spacing.SPACE_AFTER) } }
            children.add(UIButton("SPACE_AROUND")) { onClick { spacing(Spacing.SPACE_AROUND) } }
            children.add(UIButton("SPACE_BEFORE")) { onClick { spacing(Spacing.SPACE_BEFORE) } }
            children.add(UIButton("SPACE_BETWEEN")) { onClick { spacing(Spacing.SPACE_BETWEEN) } }
            children.add(UIButton("SPACE_WRAP")) { onClick { spacing(Spacing.SPACE_WRAP) } }
            children.add(UIButton("SPACE_EVENLY")) { onClick { spacing(Spacing.SPACE_EVENLY) } }

            layout(FlowLayout()) {
                horizontalSpacing(Spacing.fixed(16f))
                verticalSpacing(Spacing.fixed(8f))
            }
        })

        children.add(labelled("Custom", UIContainer()) {
            layout(ListLayout())

            fun slider(label: String) = hlabelled(children, label, UISlider()) {
                height(32f)
                backgroundColour(rgba(0.9f))
                sliderWidth(64f)
                padding(Border(6f, 12f))
            }

            val fixedSlider = slider("Fixed")
            val proportionalSlider = slider("Proportional")
            val offsetSlider = slider("Offset")
            children.add(UILabel("Note that finer control is possible")) {
                padding(Border(8f, 0f, 0f))
                alignment(vec2(0.5f))
            }

            fun update() {
                spacing(
                        Spacing.fixed(fixedSlider.x.get() * 20f) then
                                Spacing.proportional(proportionalSlider.x.get()) then
                                Spacing.proportionalOffset(offsetSlider.x.get())
                )
            }

            fixedSlider.x.connect { update() }
            proportionalSlider.x.connect { update() }
            offsetSlider.x.connect { update() }
        })
    }
}

private class PositioningDemo<T: UILayout>(val layout: T) {
    val container = UIContainer()
    val content = container.children.add(UIContainer()) {}
    val scrollContainer = container.children.add(UIScrollContainer()) {}
    val controls = scrollContainer.content

    init {
        content.layout(layout)
        content.padding(Border(16f))
        content.colour(rgba(0.15f))
        controls.padding(Border(24f, 16f))
        controls.layout(ListLayout()) { spacing(Spacing.fixed(32f)) }
        container.layout(HDivLayout())

        scrollContainer.colour(rgba(0.9f))
        scrollContainer.scrollbarY.width(16f)
        scrollContainer.scrollbarY.padding(Border(3f))
    }
}

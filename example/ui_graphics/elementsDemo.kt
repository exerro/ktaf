
import geometry.*
import ktaf.core.Colour
import ktaf.core.joinTo
import ktaf.core.rgba
import ktaf.ui.elements.*
import ktaf.ui.layout.*
import ktaf.ui.node.UIContainer
import ktaf.ui.node.UINode

fun elementsDemo(): UINode {
    return UIContainer().apply {
        colour(rgba(0.9f))
        padding(Border(16f))

        layout(FlowLayout()) {
            horizontalSpacing(Spacing.fixed(32f))
            verticalSpacing(Spacing.fixed(16f))
        }

        val label = children.add(UILabel("Label")) {}

        val button = children.add(UIButton("A button")) {
            onMouseClick.connect { text("Was clicked!") }
            clicked.connect { text("Was triggered!") }
        }

        val mirror = children.add(UILabel("Will mirror the button")) {}

        children.add(UIButton("Another button")) {
            width(192f)
            height(64f)
            colour(Colour.orange)
        }

        val sliders = children.add(UIContainer()) {
            colour(rgba(0f, 0.05f))
            layout(ListLayout(Spacing.fixed(20f))) { alignment(0f) }
        }

        val sliderRow1 = sliders.children.add(UIContainer()) { layout(HDivLayout(256.px())) }
        val sliderRow2 = sliders.children.add(UIContainer()) { layout(HDivLayout(256.px())) }

        val slider = sliderRow1.children.add(UISlider(1f, 3f, 11)) {
            padding(Border(8f, 16f))
            width(256f)
            sliderWidth(48f)
            sliderColour(Colour.purple)
        }

        val sliderLabel = sliderRow1.children.add(UILabel("(0, 0)")) {
            padding(Border(0f, 8f))
        }

        val slider2 = sliderRow2.children.add(UISlider(1f, 3f)) {
            direction(UISliderDirection.BOTH)
            width(256f)
            height(256f)
            ySteps(11)
            sliderColour(Colour.red)
        }

        val slider2Label = sliderRow2.children.add(UILabel("(0, 0)")) {
            padding(Border(0f, 8f))
        }

        children.add(UIContainer()) {
            width(200f)
            height(50f)
            colour(rgba(0.8f))
            padding(Border(8f))
            val l = layout(HDivLayout(50.pc()))

            children.add(UIButton("A")) {
                colour(Colour.purple)
                clicked.connect { l.sections[0](80.pc()) }
            }

            children.add(UIButton("B")) {
                colour(Colour.green)
                clicked.connect { l.sections[0](20.pc()) }
            }
        }

        children.add(UIScrollContainer()) {
            height(100f)

            scrollbarY.width(16f)
            scrollbarY.padding(Border(3f))

            content.children.add(UIButton("A")) {
                width(100f)
                height(100f)
                colour(Colour.orange)
            }

            content.children.add(UIButton("B")) {
                width(100f)
                height(100f)
                colour(Colour.blue)
            }

            content.layout(ListLayout(Spacing.fixed(16f)))
        }

        children.add(UIContainer()) {
            layout(HDivLayout(100.pc() - 40.px())) { spacing(4f) }

            val checkboxLabel = children.add(UILabel("Checkbox")) {
                colour(rgba(0.86f))
                padding(Border(4f, 8f))
            }
            checkboxLabel.target(children.add(UICheckbox()) {})
        }

        children.add(UIContainer()) {
            width(144f)
            colour(rgba(0.85f))
            layout(ListLayout()) { spacing(Spacing.fixed(4f)) }

            children.add(UILabel("Radio buttons")) {
                colour(rgba(0.2f))
                textColour(rgba(1f))
                alignment(vec2(0.5f))
            }

            val group = RadioButtonGroup()

            ('A' .. 'F').forEach { ch ->
                children.add(UIContainer()) {
                    colour(Colour.red)
                    padding(Border(0f, 8f))
                    layout(HDivLayout(100.pc() - 20.px()))

                    val label = children.add(UILabel(ch.toString())) {
                        padding(Border(4f, 8f))
                        textColour(rgba(1f))
                    }
                    label.target(children.add(UIRadioButton()) {
                        group(group)
                        colour(rgba(0.78f))
                    })
                }
            }
        }

        slider.x.joinTo(slider2.x)

        button.text.connect { mirror.text.set(it) }
        label.target(button)
        slider.value.connect { sliderLabel.text("(${slider.x}, ${slider.y})") }
        slider2.value.connect { slider2Label.text("(${slider2.x}, ${slider2.y})") }

        children.forEach { it.fill(false) }
    }
}

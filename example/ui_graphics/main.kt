import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.ui.Hotkey
import ktaf.ui.elements.UIButton
import ktaf.ui.elements.UIContainer
import ktaf.ui.layout.*
import ktaf.ui.node.UINode
import ktaf.ui.node.fill
import ktaf.ui.node.shrink
import ktaf.ui.scene.attachCallbacks
import ktaf.ui.scene.scene
import org.lwjgl.glfw.GLFW
import kotlin.math.max

fun main() = application("UI Graphics") {
    val context = DrawContext2D(viewport)
    val scene = scene(display, context) {
        root(UIContainer()) {
            var sizeGiven = true
            var margins = false

            fun UIContainer.addButton() {
                val container = this

                children.add(UIContainer()) {
                    val outer = this

                    colour(rgba(1f, 0.1f))

                    children.add(UIButton("Button ${children.size}")) {
                        width(96f)
                        height(32f)
                        colour(rgba(0.9f, 0.4f, 0.2f))
                        colour["hover"](rgba(0.8f, 0.3f, 0.1f))

                        onClick { outer.parent(null) }
                        onMouseEnter.connect { outer.colour(rgba(1f, 0.15f)) }
                        onMouseExit.connect { outer.colour(rgba(1f, 0.1f)) }

                        container.children.connectChanged {
                            text("Button ${container.children.indexOf(outer) + 1}")
                        }
                    }

                    layout(FillLayout()) {
                        alignment(vec2(0.5f))
                    }
                }
            }

            val buttons = children.add(UIContainer()) {
                (1 .. 10).map { addButton() }
                layout(FlowLayout())
            }

            val addButton = children.add(UIButton("ADD BUTTON")) {
                hotkeys.add(Hotkey(GLFW.GLFW_KEY_A))
                onClick { buttons.addButton() }
            }

            val toggleExpand = children.add(UIButton("TOGGLE EXPAND")) {
                hotkeys.add(Hotkey(GLFW.GLFW_KEY_T))
                onClick {
                    sizeGiven = !sizeGiven
                    buttons.children.forEach {
                        if (sizeGiven) it.fill() else it.shrink()
                    }
                }
            }

            val marginButton = children.add(UIButton("TOGGLE MARGIN")) {
                hotkeys.add(Hotkey(GLFW.GLFW_KEY_M))
                onClick {
                    margins = !margins
                    buttons.children.forEach {
                        it.margin(Border(if (margins) 32f else 0f))
                    }
                }
            }

            val switches = children.add(UIContainer()) {
                colour(rgba(0.1f))
                padding(Border(16f))

                layout(FlowLayout()) {
                    horizontalSpacing(Spacing.fixed(32f))
                    verticalSpacing(Spacing.fixed(16f))
                }

                children.add(UIButton("FILL LAYOUT")) {
                    colour(rgba(0.9f, 0.3f, 0.6f))
                    height(32f)
                    onClick { buttons.layout(FillLayout()) }
                }

                children.add(UIButton("LIST LAYOUT")) {
                    colour(rgba(0.9f, 0.3f, 0.6f))
                    height(32f)
                    onClick { buttons.layout(ListLayout()) }
                }

                children.add(UIButton("ALIGN LEFT")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { ListLayout() }) { it.alignment.set(0f) } }
                }

                children.add(UIButton("ALIGN CENTER")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { ListLayout() }) { it.alignment.set(0.5f) } }
                }

                children.add(UIButton("ALIGN RIGHT")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { ListLayout() }) { it.alignment.set(1f) } }
                }

                children.add(UIButton("SPACE_AFTER")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { ListLayout() }) { it.spacing.set(Spacing.SPACE_AFTER) } }
                }

                children.add(UIButton("SPACE_AROUND")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { ListLayout() }) { it.spacing.set(Spacing.SPACE_AROUND) } }
                }

                children.add(UIButton("SPACE_BETWEEN")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { ListLayout() }) { it.spacing.set(Spacing.SPACE_BETWEEN) } }
                }

                children.add(UIButton("SPACE_BEFORE")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { ListLayout() }) { it.spacing.set(Spacing.SPACE_BEFORE) } }
                }

                children.add(UIButton("SPACE_WRAP")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { ListLayout() }) { it.spacing.set(Spacing.SPACE_WRAP) } }
                }

                children.add(UIButton("SPACE_EVENLY")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { ListLayout() }) { it.spacing.set(Spacing.SPACE_EVENLY) } }
                }

                children.add(UIButton("FLOW LAYOUT")) {
                    colour(rgba(0.9f, 0.3f, 0.6f))
                    height(32f)
                    onClick { buttons.layout(FlowLayout()) }
                }

                children.add(UIButton("SPACE_AFTER")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { FlowLayout() }) { it.horizontalSpacing.set(Spacing.SPACE_AFTER) } }
                }

                children.add(UIButton("SPACE_AROUND")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { FlowLayout() }) { it.horizontalSpacing.set(Spacing.SPACE_AROUND) } }
                }

                children.add(UIButton("SPACE_BETWEEN")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { FlowLayout() }) { it.horizontalSpacing.set(Spacing.SPACE_BETWEEN) } }
                }

                children.add(UIButton("SPACE_BEFORE")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { FlowLayout() }) { it.horizontalSpacing.set(Spacing.SPACE_BEFORE) } }
                }

                children.add(UIButton("SPACE_WRAP")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { FlowLayout() }) { it.horizontalSpacing.set(Spacing.SPACE_WRAP) } }
                }

                children.add(UIButton("SPACE_EVENLY")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { FlowLayout() }) { it.horizontalSpacing.set(Spacing.SPACE_EVENLY) } }
                }

                children.add(UIButton("SPACE_AFTER")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { FlowLayout() }) { it.verticalSpacing.set(Spacing.SPACE_AFTER) } }
                }

                children.add(UIButton("SPACE_AROUND")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { FlowLayout() }) { it.verticalSpacing.set(Spacing.SPACE_AROUND) } }
                }

                children.add(UIButton("SPACE_BETWEEN")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { FlowLayout() }) { it.verticalSpacing.set(Spacing.SPACE_BETWEEN) } }
                }

                children.add(UIButton("SPACE_BEFORE")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { FlowLayout() }) { it.verticalSpacing.set(Spacing.SPACE_BEFORE) } }
                }

                children.add(UIButton("SPACE_WRAP")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { FlowLayout() }) { it.verticalSpacing.set(Spacing.SPACE_WRAP) } }
                }

                children.add(UIButton("SPACE_EVENLY")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { FlowLayout() }) { it.verticalSpacing.set(Spacing.SPACE_EVENLY) } }
                }

                children.add(UIButton("GRID LAYOUT")) {
                    colour(rgba(0.9f, 0.3f, 0.6f))
                    height(32f)
                    onClick { buttons.layout(GridLayout()) }
                }

                children.add(UIButton("ADD ROW")) {
                    width(96f)
                    height(32f)
                    onClick { setLayout(buttons, { GridLayout() }) { it.rows.set(it.rows.get() + 1) } }
                }

                children.add(UIButton("REMOVE ROW")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { GridLayout() }) { it.rows.set(max(1, it.rows.get() - 1)) } }
                }

                children.add(UIButton("ADD COLUMN")) {
                    width(144f)
                    height(32f)
                    onClick { setLayout(buttons, { GridLayout() }) { it.columns.set(it.columns.get() + 1) } }
                }

                children.add(UIButton("REMOVE COLUMN")) {
                    width(192f)
                    height(32f)
                    onClick { setLayout(buttons, { GridLayout() }) { it.columns.set(max(1, it.columns.get() - 1)) } }
                }

                children.add(UIButton("TOP LEFT")) {
                    width(192f)
                    height(32f)
                    onClick { setLayout(buttons, { GridLayout() }) { it.alignment.set(vec2(0f)) } }
                }

                children.add(UIButton("BOTTOM RIGHT")) {
                    width(192f)
                    height(32f)
                    onClick { setLayout(buttons, { GridLayout() }) { it.alignment.set(vec2(1f)) } }
                }

                children.add(UIButton("RANDOM ALIGN")) {
                    width(192f)
                    height(32f)
                    onClick { setLayout(buttons, { GridLayout() }) { it.alignment.set(vec2(Math.random().toFloat(), Math.random().toFloat())) } }
                }
            }

            layout(AreaLayout()) {
                areas {
                    split(40.pc()) { labels("left", "switches") }

                    area("left") {
                        vsplit(100.pc() - 64.px()) { labels("buttons", "button-row") }

                        area("button-row") {
                            split(33.pc(), 66.pc()) { labels("add-button", "toggle-button", "margin-button") }
                        }
                    }

                    elem(buttons, "buttons")
                    elem(addButton, "add-button")
                    elem(toggleExpand, "toggle-button")
                    elem(marginButton, "margin-button")
                    elem(switches, "switches")
                }
            }
        }
    }

    scene.attachCallbacks(this)
}

inline fun <reified T: UILayout> setLayout(element: UINode, create: () -> T, fn: (T) -> Unit) {
    when (val layout = element.layout.get()) {
        is T -> { fn(layout) }
        else -> { fn(element.layout(create())); fn(element.layout.get() as T) }
    }
}

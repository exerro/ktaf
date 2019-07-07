import ktaf.core.application
import ktaf.core.rgba
import ktaf.graphics.DrawContext2D
import ktaf.ui.*
import ktaf.ui.elements.UIButton
import ktaf.ui.elements.UICanvas
import ktaf.ui.elements.UIContainer
import ktaf.ui.elements.onClick

fun clearRoots(scene: UIScene) {
    scene.roots.forEach { scene.removeRoot(it) }
}

fun loadProjectsList(scene: UIScene) {
    scene.addRoot(UIContainer()) {
        val header = addChild(UIContainer()) {
            colour = rgba(0.08f, 0.09f, 0.11f)
            padding = Border(10f)

            val title = addChild(UIButton("Projects")) {
                colour = rgba(0f, 0f)
                font = font.scaleTo(32f)
            }

            layout(FillLayout())
        }

        val content = addChild(UIContainer()) {
            colour = rgba(0.14f, 0.15f, 0.17f)
            padding = Border(0f, 0f, 50f, 0f)

            val plist = addChild(UIContainer()) {
                colour = rgba(0f, 0f)

                for (item in listOf("Project 1", "Project 2", "Project 3")) {
                    addChild(UIButton(item)) {
                        colour = rgba(0.3f, 0.6f, 0.9f)
                        height = 50f
                        font = font.scaleTo(28f)

                        onClick {
                            clearRoots(scene)
                            loadProjectPage(scene, item)
                        }
                    }
                }

                layout(ListLayout()) {
                    spacing = Spacing.fixed(24f) within Spacing.SPACE_AROUND
                }
            }

            val newButton = addChild(UIButton("NEW")) {
                colour = rgba(0.9f, 0.35f, 0.3f)
            }

            layout(FreeLayout()) {
                hline("footer-start") {
                    percentage = 100f
                    offset = -50f
                }

                elem(plist) {
                    top = "top"
                    bottom = "footer-start"
                    leftOffset = -300f
                    leftPercentage = 50f
                    rightOffset = 300f
                    rightPercentage = 50f
                }

                elem(newButton) {
                    top = "footer-start"
                    bottom = "bottom"
                    leftOffset = -100f
                    leftPercentage = 50f
                    rightOffset = 100f
                    rightPercentage = 50f
                }
            }
        }

        layout(FreeLayout()) {
            hline("divider") {
                offset = 80f
            }

            elem(header) {
                top = "top"
                bottom = "divider"
                left = "left"
                right = "right"
            }

            elem(content) {
                top = "divider"
                bottom = "bottom"
                left = "left"
                right = "right"
            }
        }
    }
}

fun loadProjectPage(scene: UIScene, name: String) {
    scene.addRoot(UIContainer()) {
        val header = addChild(UIContainer()) {
            colour = rgba(0.08f, 0.09f, 0.11f)
            padding = Border(10f)

            val back = addChild(UIButton("BACK")) {
                width = 100f

                onClick {
                    clearRoots(scene)
                    loadProjectsList(scene)
                }
            }

            val title = addChild(UIButton(name)) {
                colour = rgba(0f, 0f)
                font = font.scaleTo(32f)
            }

            layout(FreeLayout()) {
                elem(title) {
                    top = "top"
                    bottom = "bottom"
                    leftOffset = -100f
                    rightOffset = 100f
                    leftPercentage = 50f
                    rightPercentage = 50f
                }

                elem(back) {
                    leftOffset = 32f
                    topOffset = 10f
                    bottomPercentage = 100f
                    bottomOffset = -10f
                }
            }
        }

        val content = addChild(UIContainer()) {
            padding = Border(16f)
            colour = rgba(0.14f, 0.15f, 0.17f)

            val board = addChild(UICanvas()) {

            }

            val robots = addChild(UIContainer()) {

            }

            val routines = addChild(UIContainer()) {

            }

            val tasks = addChild(UIContainer()) {

            }

            val components = addChild(UIContainer()) {

            }

            layout(FreeLayout()) {
                hline("separator-start") {
                    percentage = 60f
                    offset = -8f
                }

                hline("separator-end") {
                    percentage = 60f
                    offset = 8f
                }

                vline("c1-end") {
                    percentage = 33f
                    offset = -8f
                }

                vline("c2-start") {
                    percentage = 33f
                    offset = 8f
                }

                vline("c2-end") {
                    percentage = 66f
                    offset = -8f
                }

                vline("c3-start") {
                    percentage = 66f
                    offset = 8f
                }

                elem(board) {
                    top = "top"
                    left = "left"
                    bottom = "separator-start"
                    right = "c2-end"
                }

                elem(robots) {
                    top = "top"
                    left = "c3-start"
                    bottom = "separator-start"
                    right = "right"
                }

                elem(routines) {
                    top = "separator-end"
                    left = "left"
                    bottom = "bottom"
                    right = "c1-end"
                }

                elem(tasks) {
                    top = "separator-end"
                    left = "c2-start"
                    bottom = "bottom"
                    right = "c2-end"
                }

                elem(components) {
                    top = "separator-end"
                    left = "c3-start"
                    bottom = "bottom"
                    right = "right"
                }
            }
        }

        layout(FreeLayout()) {
            hline("divider") {
                offset = 80f
            }

            elem(header) {
                top = "top"
                bottom = "divider"
                left = "left"
                right = "right"
            }

            elem(content) {
                top = "divider"
                bottom = "bottom"
                left = "left"
                right = "right"
            }
        }
    }
}

fun main() = application("UI Test 2") {
    val context = DrawContext2D(viewport)
    val scene = scene(display, context)

    loadProjectsList(scene)

    scene.attachCallbacks(this)
}

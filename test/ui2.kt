import ktaf.core.application
import ktaf.core.rgba
import ktaf.graphics.DrawContext2D
import ktaf.ui.*
import ktaf.ui.elements.UIButton
import ktaf.ui.elements.UICanvas
import ktaf.ui.elements.UIContainer
import ktaf.ui.layout.*
import ktaf.ui.node.UINode
import ktaf.ui.scene.UIScene
import ktaf.ui.scene.attachCallbacks

/** Moves * to left in transition */
fun clearRootsLeft(scene: UIScene, node: UINode) {
//    scene.roots.forEach { scene.removeRoot(it) }
    when (val l = node.layout.get()) {
        is FreeLayout -> l.elem(node.children.last()) {
            leftPercentage = -100f
        }
    }
}

/** Moves * to right in transition */
fun clearRootsRight(scene: UIScene, node: UINode) {
//    scene.roots.forEach { scene.removeRoot(it) }
    when (val l = node.layout.get()) {
        is FreeLayout -> l.elem(node.children.last()) {
            leftPercentage = 100f
        }
    }
}

fun loadProjectsList(scene: UIScene, node: UINode) {
    node.children.add(UIContainer(), {
        val header = children.add(UIContainer(), {
            colour(rgba(0.08f, 0.09f, 0.11f))
            padding(Border(10f))

            val title = children.add(UIButton("Projects"), {
                colour(rgba(0f, 0f))
                font(font.get().scaleTo(32f))
            })

            layout(FillLayout())
        })

        val content = children.add(UIContainer(), {
            colour(rgba(0.14f, 0.15f, 0.17f))
            padding(Border(0f, 0f, 50f, 0f))

            val plist = children.add(UIContainer(), {
                colour(rgba(0f, 0f))

                for (item in listOf("Project 1", "Project 2", "Project 3")) {
                    children.add(UIButton(item), {
                        colour(rgba(0.3f, 0.6f, 0.9f))
                        height(50f)
                        font(font.get().scaleTo(28f))

                        onMouseClick {
                            clearRootsLeft(scene, node)
                            loadProjectPage(scene, node, item)
                        }
                    })
                }

                layout(ListLayout()) {
                    spacing(Spacing.fixed(24f) within Spacing.SPACE_AROUND)
                }
            })

            val newButton = children.add(UIButton("NEW"), {
                colour(rgba(0.9f, 0.35f, 0.3f))
            })

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
        })

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
    })
}

fun loadProjectPage(scene: UIScene, node: UINode, name: String) {
    node.children.add(UIContainer(), {
        val header = children.add(UIContainer(), {
            colour(rgba(0.08f, 0.09f, 0.11f))
            padding(Border(10f))

            val back = children.add(UIButton("BACK"), {
                width(100f)

                onClick {
                    clearRootsRight(scene, node)
                    loadProjectsList(scene, node)
                }
            })

            val title = children.add(UIButton(name), {
                colour(rgba(0f, 0f))
                font(font.get().scaleTo(32f))
            })

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
        })

        val content = children.add(UIContainer(), {
            padding(Border(16f))
            colour(rgba(0.14f, 0.15f, 0.17f))

            val board = children.add(UICanvas(), {

            })

            val robots = children.add(UIContainer(), {

            })

            val routines = children.add(UIContainer(), {

            })

            val tasks = children.add(UIContainer(), {

            })

            val components = children.add(UIContainer(), {

            })

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
        })

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
    })
}

fun main() = application("UI Test 2") {
    val context = DrawContext2D(viewport)
    val scene = scene(display, context)
    val node = scene.root(UIContainer()) {
        layout(FreeLayout())
    }

    loadProjectsList(scene, node)

    scene.attachCallbacks(this)
}

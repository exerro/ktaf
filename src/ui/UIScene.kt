package ui

import DrawContext2D

class UIScene(val context: DrawContext2D) {
    val roots = mutableListOf<UINode>()
}

fun UIScene.update(dt: Float) {
    roots.forEach {
        it.computeWidthInternal(context.viewport.width().toFloat())
        it.positionChildrenInternal(context.viewport.height().toFloat())
        it.update(dt)
    }
}

fun UIScene.draw() {
    roots.forEach {
        it.draw(context, it.margin.left, it.margin.top,
                context.viewport.width().toFloat() - it.margin.width,
                context.viewport.height().toFloat() - it.margin.height)
    }
}

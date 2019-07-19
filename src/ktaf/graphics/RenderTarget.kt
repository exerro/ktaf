package ktaf.graphics

import ktaf.core.KTAFValue
import ktaf.core.RatioValue
import ktaf.core.constant
import ktaf.core.vec2
import lwjglkt.GL

sealed class RenderTarget(
        minX: RatioValue,
        minY: RatioValue,
        maxX: RatioValue,
        maxY: RatioValue
) {
    val minX = KTAFValue(minX)
    val minY = KTAFValue(minY)
    val maxX = KTAFValue(maxX)
    val maxY = KTAFValue(maxY)

    abstract val width: Int
    abstract val height: Int

    val offset get() = vec2(
            minX.get()(width.toFloat()),
            minY.get()(height.toFloat())
    )

    val size get() = vec2(
            maxX.get()(width.toFloat()) - minX.get()(width.toFloat()),
            maxY.get()(height.toFloat()) - minY.get()(height.toFloat())
    )

    abstract fun draw(fn: () -> Unit)

    protected fun viewport() {
        GL.viewport(
                minX.get()( width.toFloat()).toInt(),
                minY.get()(height.toFloat()).toInt(),
                maxX.get()( width.toFloat()).toInt(),
                maxY.get()(height.toFloat()).toInt()
        )
    }

    class Screen(minX: RatioValue, minY: RatioValue, maxX: RatioValue, maxY: RatioValue, width: Int, height: Int): RenderTarget(minX, minY, maxX, maxY) {
        val screenWidth = KTAFValue(width)
        val screenHeight = KTAFValue(height)

        constructor(width: Int, height: Int): this(
                constant(0f),
                constant(0f),
                constant(width.toFloat()),
                constant(height.toFloat()),
                width,
                height
        )

        override val width: Int get() = screenWidth.get()
        override val height: Int get() = screenHeight.get()

        override fun draw(fn: () -> Unit) {
            viewport()
            fn()
        }
    }

//    class Framebuffer: RenderTarget() {
//        ...
//    }
}

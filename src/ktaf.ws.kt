import geometry.length
import geometry.vec2
import geometry.vec2_zero
import ktaf.core.application
import ktaf.data.property.const
import ktaf.graphics.Colour
import ktaf.graphics.DrawContext2D
import ktaf.graphics.RGBA
import ktaf.graphics.alpha
//import lwjglkt.gl.GLState
import lwjglktx.font.widthOf
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

fun clamp(v: Float, a: Float, b: Float) = max(a, min(b, v))
fun clamp(v: vec2, a: vec2, b: vec2) = vec2(clamp(v.x, a.x, b.x), clamp(v.y, a.y, b.y))

fun f() {

}

data class BBox(val pos: vec2, val size: vec2) {
    fun mlem(p: vec2): vec2 {
        val d = p - pos - size / 2f
        val dd = max(1f, d.length() - 32f)
        val f = min(0.2f, 100 / dd / dd)
        return d * f
    }
}

data class Buttonish(
        val x: Float,
        val y: Float,
        val width: Float,
        val text: String,
        val colour: RGBA
) {
    var effectiveX = x
    var effectiveY = y

    val rawBBox get() = BBox(vec2(x, y), vec2(width, 64f))
    val bbox get() = BBox(vec2(effectiveX, effectiveY), vec2(width, 64f))

    fun update(cursor: vec2) {
        val (ex, ey) = vec2(x, y) + rawBBox.mlem(cursor)
        effectiveX = ex
        effectiveY = ey
    }

    fun draw(ctx: DrawContext2D) {
        val w = ctx.DEFAULT_FONT.widthOf(text)
        val h = ctx.DEFAULT_FONT.lineHeight

        ctx.colour.value = colour
        ctx.rectangle(bbox.pos, bbox.size)
        ctx.colour.value = Colour.white
        ctx.write(text, bbox.pos + (bbox.size - vec2(w, h)) * 0.5f)
    }
}

fun main() = application {
    val buttons = listOf(
            Buttonish(100f, 100f, 200f, "Hello world", Colour.red)
    )

    window("Hello world", 1080, 720) { window ->
        window.update.subscribe(window) {
            val (cx, cy) = window.glfwWindow.cursorPosition
            val c = vec2(cx, cy)

            buttons.forEach {
                it.update(c)
            }
        }

        window.draw.subscribe(window) {
            val (cx, cy) = window.glfwWindow.cursorPosition
            val c = vec2(cx, cy)

            window.drawContext2D.begin()

            buttons.forEach {
                it.draw(window.drawContext2D)
            }

            window.drawContext2D.end()
        }
    }
}

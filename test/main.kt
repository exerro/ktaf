import core.*
import graphics.FNTFont
import graphics.*
import kotlin.math.sin

fun main() = application("Hello world") {
    GL.debug()

    val context = DrawContext2D(viewport)
//    val texture = loadTextureFile2D("img.jpg")

    draw {
        val font = FNTFont.DEFAULT_FONT
        val width = font.widthOf("Hello g")
        val height = font.height

        context.draw {
            context.colour = RGBA(1f, 1f, 1f)
            rectangle(vec2(10f), vec2(width, height))
            context.colour = RGBA(1f, 0f, 1f, sin(time) * 0.5f + 0.5f)
            write("Hello g", font, vec2(10f))
        }
    }

    draw {
        context.push {
//            translate(core.vec2(100f))
//            colour = RGB(0f, 1f, 1f)
//            scissor = AABB(core.vec2(30f), core.vec2(200f))
//            graphics.image(texture, core.vec2(0f), core.vec2(0.3f))
        }

//        context.graphics.push()
//        context.scissor = AABB(core.vec2(30f), core.vec2(200f))
//        context.graphics.image(texture, core.vec2(0f), core.vec2(0.3f))
//        context.pop()


    }

    update {
        println(fps)
    }
}

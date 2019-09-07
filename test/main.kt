import geometry.vec2
import ktaf.core.RGBA
import ktaf.core.application
import ktaf.graphics.*
import kotlin.math.sin

fun main() = application {
    display("Main") {
        val context = DrawContext2D(screen)
//    val texture = loadTextureFile2D("img.jpg")

        draw.connect {
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

        draw.connect {
            context.push {
                //            translate(ktaf.core.vec2(100f))
//            colour = RGB(0f, 1f, 1f)
//            scissor = AABB(ktaf.core.vec2(30f), ktaf.core.vec2(200f))
//            ktaf.graphics.image(texture, ktaf.core.vec2(0f), ktaf.core.vec2(0.3f))
            }

//        context.ktaf.graphics.push()
//        context.scissor = AABB(ktaf.core.vec2(30f), ktaf.core.vec2(200f))
//        context.ktaf.graphics.image(texture, ktaf.core.vec2(0f), ktaf.core.vec2(0.3f))
//        context.pop()


        }

        update.connect {
            println(fps)
        }
    }
}

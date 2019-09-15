//import geometry.vec2
//import ktaf.core.RGBA
//import ktaf.core.application
//import ktaf.graphics.*
//import lwjglkt.GLBLendFunction
//import lwjglkt.gl.GLOption
//import kotlin.math.sin
//
//fun main() = application {
//    display("Main") {
//        val context = context2D
////    val texture = loadTextureFile2D("img.jpg")
//
//        // enable blending
//        context.glContext.enable(GLOption.GL_BLEND)
//
//        context.glContext.postFragmentShaderState {
//            blendFunction(GLBLendFunction.GL_SRC_ALPHA, GLBLendFunction.GL_ONE_MINUS_SRC_ALPHA)
//        }
//
//        draw.connect {
//            val font = FNTFont.DEFAULT_FONT
//            val width = font.widthOf("Hello there")
//            val height = font.height
//
//            context.draw {
//                context.colour(RGBA(1f, 1f, 1f))
//                rectangle(vec2(10f), vec2(width, height))
//                context.colour(RGBA(1f, 0f, 1f, sin(time) * 0.5f + 0.5f))
//                write("Hello there", vec2(10f), font)
//            }
//        }
//
//        draw.connect {
//            context.push {
//                //            translate(ktaf.core.vec2(100f))
////            colour = RGB(0f, 1f, 1f)
////            scissor = AABB(ktaf.core.vec2(30f), ktaf.core.vec2(200f))
////            ktaf.graphics.image(texture, ktaf.core.vec2(0f), ktaf.core.vec2(0.3f))
//            }
//
////        context.ktaf.graphics.push()
////        context.scissor = AABB(ktaf.core.vec2(30f), ktaf.core.vec2(200f))
////        context.ktaf.graphics.image(texture, ktaf.core.vec2(0f), ktaf.core.vec2(0.3f))
////        context.pop()
//
//
//        }
//
//        update.connect {
//            println(fps)
//        }
//    }
//}

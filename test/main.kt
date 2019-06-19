import font.FNTFont

fun main() = application("Hello world") {
    val context = DrawContext2D(viewport)
//    val texture = loadTextureFile2D("img.jpg")

    draw {
        val width = FNTFont.DEFAULT_FONT.scaleTo(300f).widthOf("Hello g")
        val height = FNTFont.DEFAULT_FONT.scaleTo(300f).height
        context.colour = RGB(1f)
        context.rectangle(vec2(10f), vec2(width + 1, height))
        context.colour = RGB(1f, 0f, 1f)
        context.write("Hello g", FNTFont.DEFAULT_FONT.scaleTo(300f), vec2(10f))
    }

    draw {
        context.push {
//            translate(vec2(100f))
//            colour = RGB(0f, 1f, 1f)
//            scissor = AABB(vec2(30f), vec2(200f))
//            image(texture, vec2(0f), vec2(0.3f))
        }

//        context.push()
//        context.scissor = AABB(vec2(30f), vec2(200f))
//        context.image(texture, vec2(0f), vec2(0.3f))
//        context.pop()


    }

    update {
        println(fps)
    }
}

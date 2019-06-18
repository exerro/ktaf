
fun main() = application("Hello world") {
    val context = DrawContext2D(viewport)
    val texture = loadTextureFile2D("img.jpg")

    draw {
        context.push {
//            translate(vec2(100f))
//            colour = RGB(0f, 1f, 1f)
//            scissor = AABB(vec2(30f), vec2(200f))
//            image(texture, vec2(0f), vec2(0.3f))
        }

//        context.push()
        context.scissor = AABB(vec2(30f), vec2(200f))
        context.image(texture, vec2(0f), vec2(0.3f))
//        context.pop()
    }

    update {
        println(fps)
    }
}

import kotlin.math.sin

fun main() = application("Hello world") {
    println("Hello world")

    val context = computeOnMainThread { DrawContext2D(viewport) }
    val texture2 = computeOnMainThread { loadTextureFile2D("img.jpg") }

    draw {
        context.image(texture2, vec2(0f), vec2(0.3f))
    }

    update {
        println(fps)
    }
}

val s = System.currentTimeMillis()

fun pos(period: Float = 1f, offset: Float = 0f, amplitude: Float = 1f): Float {
    return sin((System.currentTimeMillis() - s) / 1000f / period - offset) * amplitude
}

fun timer(period: Float, offset: Float): Boolean {
    return ((System.currentTimeMillis() - s) / 1000f - offset) % period < period / 2
}

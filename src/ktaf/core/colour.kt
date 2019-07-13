package ktaf.core

typealias RGB = vec3
typealias RGBA = vec4

fun rgb(r: Float, g: Float, b: Float): RGB = vec3(r, g, b)
fun rgb(v: Float): RGB = rgb(v, v, v)
fun rgb(i: Int): RGB = rgb((i shr 16) / 255f, ((i shr 8) % 256) / 255f, (i % 256) / 255f)
fun rgb(c: String): RGB = rgb(c.drop(1).toInt(16))
fun rgba(r: Float, g: Float, b: Float, a: Float = 1f): RGBA = vec4(r, g, b, a)
fun rgba(v: Float, a: Float = 1f): RGBA = rgba(v, v, v, a)
fun rgba(i: Int): RGBA = rgba((i shr 24) / 255f, ((i shr 16) % 256) / 255f, ((i shr 8) % 256) / 255f, (i % 256) / 255f)
fun rgba(c: String): RGBA = rgba(c.drop(1).toInt(16))

fun RGB.rgba(a: Float = 1f) = rgba(x, y, z, a)
fun RGBA.rgb() = rgb(x, y, z)

fun RGBA.darken() = this * 0.9f
fun RGBA.lighten() = this / 0.9f

object Colour {
    val blue = rgba(0.27f, 0.54f, 0.81f)
    val orange = rgba(0.90f, 0.40f, 0.20f)
    val purple = rgba(0.40f, 0.20f, 0.60f)
}

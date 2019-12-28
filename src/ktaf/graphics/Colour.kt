package ktaf.graphics

import geometry.*
import kotlin.math.max
import kotlin.math.min

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
fun RGBA.alpha(a: Float) = rgba(x, y, z, a)

fun RGBA.darken() = vec3().length().let { this * max(it - 0.07f, 0f) / it }
fun RGBA.lighten() = vec3().length().let { this * min(it + 0.07f, MAX_BRIGHTNESS) / it }

object Colour {
    val white: RGBA = rgba(1.0f)
    val grey: RGBA  = rgba(0.4f)
    val black: RGBA = rgba(0.0f)

    val blue:   RGBA = rgb(0.27f, 0.54f, 0.81f).normalise(1.08f).rgba()
    val orange: RGBA = rgb(0.80f, 0.45f, 0.20f).normalise(1.13f).rgba()
    val red:    RGBA = rgb(1.00f, 0.23f, 0.23f).normalise(0.94f).rgba()
    val green:  RGBA = rgb(0.05f, 0.65f, 0.27f).normalise(0.82f).rgba()
    val purple: RGBA = rgb(0.40f, 0.20f, 0.60f).normalise(1.05f).rgba()
    val yellow: RGBA = rgb(0.90f, 0.84f, 0.00f).normalise(1.26f).rgba()
}

const val MAX_BRIGHTNESS = 1.73205081f

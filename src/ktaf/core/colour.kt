package ktaf.core

import ktaf.KTAFMutableValue

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

fun KTAFMutableValue<RGB>.rgb(r: Float, g: Float, b: Float) = set(vec3(r, g, b))
fun KTAFMutableValue<RGB>.rgb(v: Float) { rgb(v, v, v) }
fun KTAFMutableValue<RGB>.rgb(i: Int) { rgb((i shr 16) / 255f, ((i shr 8) % 256) / 255f, (i % 256) / 255f) }
fun KTAFMutableValue<RGB>.rgb(c: String) { rgb(c.drop(1).toInt(16)) }
fun KTAFMutableValue<RGBA>.rgba(r: Float, g: Float, b: Float, a: Float = 1f) = set(vec4(r, g, b, a))
fun KTAFMutableValue<RGBA>.rgba(v: Float, a: Float = 1f) { rgba(v, v, v, a) }
fun KTAFMutableValue<RGBA>.rgba(i: Int) { rgba((i shr 24) / 255f, ((i shr 16) % 256) / 255f, ((i shr 8) % 256) / 255f, (i % 256) / 255f) }
fun KTAFMutableValue<RGBA>.rgba(s: String) { rgba(s.drop(1).toInt(16)) }

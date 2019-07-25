package ktaf.graphics

import ktaf.core.vec2
import lwjglkt.GLTexture2
import lwjglkt.GLVAO

abstract class Font(val scale: Float) {
    val height: Float get() = scale * lineHeight
    abstract val lineHeight: Float
    abstract val baseline: Float

    abstract fun scaleTo(height: Float): Font

    abstract fun getCharSize(char: Char): vec2
    abstract fun getCharOffset(char: Char): vec2
    abstract fun getCharAdvance(char: Char): Float
    abstract fun getKerning(char1: Char, char2: Char): Float
    abstract fun getVAO(char: Char): GLVAO
    abstract fun getVAOVertexCount(char: Char): Int
    abstract fun getTexture(char: Char): GLTexture2?

    companion object {
        val DEFAULT_FONT by lazy { FNTFont.DEFAULT_FONT as Font }
    }
}

fun Font.widthOf(text: String, from: Int = 0, to: Int = text.length)
        = if (from >= to) 0f else (text.drop(from).take(to - from).zip(text.drop(from + 1)).map { (a, b) ->
              getKerning(a, b) + getCharAdvance(a)
          } .sum() + getCharSize(text.last()).x + getCharOffset(text.last()).x) * scale

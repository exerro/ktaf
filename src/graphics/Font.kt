package graphics

import GLTexture2
import GLVAO
import vec2

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
}

fun Font.widthOf(text: String)
        = (text.zip(text.drop(1)).map { (a, b) -> getKerning(a, b) + getCharAdvance(a) } .sum() +
           getCharSize(text.last()).x + getCharOffset(text.last()).x) * scale
package ktaf.util

import ktaf.graphics.Font
import ktaf.graphics.widthOf
import kotlin.math.max

fun getOverflow(text: String, font: Font, width: Float): Int {
    if (font.widthOf(text) <= width)
        return text.length

    for (i in 1 until text.length) {
        if (font.widthOf(text, 0, i) > width) return i
    }

    return text.length
}

fun findPreceedingWhitespace(text: String, character: Int): Int {
    if (text.isEmpty()) return 0
    if (character < text.length && text[character].isWhitespace()) return character
    (character - 1 downTo 0).forEach { i -> if (text[i].isWhitespace()) return i }
    return character
}

fun lineWrapText(text: String, font: Font, width: Float): Pair<String, String?> {
    val trimmedText = text.trim()
    val newline = trimmedText.indexOf("\n")

    if (newline != -1 && font.widthOf(trimmedText.substring(0, newline)) <= width)
        return trimmedText.substring(0, newline).trim() to trimmedText.substring(newline + 1).trim()

    val overflow = getOverflow(trimmedText, font, width)
    val wrapAt = max(1, findPreceedingWhitespace(trimmedText, overflow))

    if (wrapAt >= trimmedText.length)
        return trimmedText.substring(0, wrapAt).trim() to null

    return trimmedText.substring(0, wrapAt).trim() to trimmedText.substring(wrapAt).trim()
}

fun wrapText(text: String, font: Font, width: Float): List<String> {
    return generateSequence(lineWrapText(text, font, width)) { (_, rest) -> rest ?.let { lineWrapText(rest, font, width) } }
            .map { (line, _) -> line }
            .toList()
}

fun lineWrapTextLossless(text: String, font: Font, width: Float): Pair<String, String?> {
    val overflow = getOverflow(text, font, width)
    val wrapAt = findPreceedingWhitespace(text, overflow)
    return text.substring(0, wrapAt) to text.substring(wrapAt)
}

fun wrapTextLossless(text: String, font: Font, width: Float): List<String> {
    return generateSequence(lineWrapTextLossless(text, font, width)) { (_, rest) -> rest ?.let { lineWrapTextLossless(rest, font, width) } }
            .map { (line, _) -> line }
            .toList()
}

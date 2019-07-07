package ktaf.util

import ktaf.graphics.Font
import ktaf.graphics.widthOf

fun lineWrapText(text: String, font: Font, width: Float): Pair<String, String?> {
    val trimmedText = text.trim()

    if (font.widthOf(trimmedText) <= width)
        return trimmedText to null

    // TODO: fix issues with very small widths
    val overflow = (1 until trimmedText.length).firstOrNull { font.widthOf(trimmedText.substring(0, it + 1)) > width } ?: 1
    val length = (overflow - 1 downTo 0).first { !trimmedText[it].isWhitespace() }

    return trimmedText.substring(0, length + 1) to trimmedText.substring(overflow)
}

fun wrapText(text: String, font: Font, width: Float): List<String> {
    return generateSequence(lineWrapText(text, font, width)) { (_, rest) -> rest ?.let { lineWrapText(rest, font, width) } }
            .map { (line, _) -> line }
            .toList()
}

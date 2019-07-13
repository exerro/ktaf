package ktaf.util

import ktaf.graphics.Font
import ktaf.graphics.widthOf

fun lineWrapText(text: String, font: Font, width: Float): Pair<String, String?> {
    val trimmedText = text.trim()

    if (font.widthOf(trimmedText) <= width || trimmedText.length <= 1)
        return trimmedText to null

    // the character where the text overflows the width given
    val overflow = (1 until trimmedText.length).firstOrNull { font.widthOf(trimmedText.substring(0, it + 1)) > width } ?: 1
    trimmedText.substring(0, overflow).indexOf('\n').takeIf { it != -1 } ?.let { return trimmedText.substring(0, it).trim() to trimmedText.substring(it).trim() }
    val wrapAt = if (trimmedText[overflow].isWhitespace()) overflow
    else trimmedText.substring(0, overflow).indexOfLast(Char::isWhitespace).takeIf { it != -1 } ?: overflow

    return trimmedText.substring(0, wrapAt).trim() to trimmedText.substring(wrapAt).trim()
}

fun wrapText(text: String, font: Font, width: Float): List<String> {
    return generateSequence(lineWrapText(text, font, width)) { (_, rest) -> rest ?.let { lineWrapText(rest, font, width) } }
            .map { (line, _) -> line }
            .toList()
}

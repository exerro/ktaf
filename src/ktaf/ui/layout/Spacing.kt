package ktaf.ui.layout

data class Spacing(val fixed: Float, val spacer: (Float, Int) -> Float, val offset: (Float, Int) -> Float) {
    companion object {
        private val zero: (Float, Int) -> Float = { _, _ -> 0f }

        val SPACE_AROUND = align(0.5f)
        /** ...OO...OO...OO... */
        val SPACE_EVENLY = Spacing(0f, { s, n -> s / (n + 1) }, { s, _ -> s / 2 })
        /** ..OO....OO....OO.. */
        val SPACE_WRAP = Spacing(0f, { s, n -> s / n }, { s, _ -> s / 2 })
        /** OO......OO......OO */
        val SPACE_BETWEEN = Spacing(0f, { s, n -> s / (n - 1) }, zero)
        /** OOOOOO............ */
        val SPACE_AFTER = Spacing(0f, zero, zero)
        /** ............OOOOOO */
        val SPACE_BEFORE = Spacing(0f, zero, { s, _ -> s })
        /** OO.OO.OO...... */
        fun fixed(spacing: Float) = Spacing(spacing, zero, zero)
        /** ..<~OOOOOO~>.. */
        fun align(alignment: Float) = Spacing(0f, zero, { s, _ -> s * alignment })
    }

    infix fun then(other: Spacing): Spacing {
        return Spacing(fixed + other.fixed, { s, n -> spacer(s, n) + other.spacer(s, n) }, { s, n -> offset(s, n) + other.offset(s, n) })
    }
}

internal fun Spacing.fixed() = fixed

internal fun Spacing.evaluate(space: Float, n: Int): Pair<Float, Float> {
    val spacing = fixed + spacer(space - (n - 1) * fixed, n)
    val offset = offset(space - (n - 1) * spacing, n)
    return Pair(offset, spacing)
}

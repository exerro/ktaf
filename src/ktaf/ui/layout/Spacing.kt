package ktaf.ui.layout

typealias SpacingFunction = (Int, Float, Float) -> Float
data class Spacing(val init: SpacingFunction, val iter: SpacingFunction) {
    constructor(init: SpacingFunction, iter: Float): this(init, { _, _, _ -> iter })
    constructor(init: Float, iter: SpacingFunction): this({ _, _, _ -> init}, iter)
    constructor(init: Float, iter: Float): this({ _, _, _ -> init }, { _, _, _ -> iter })

    companion object {
        /** ......OOOOOO...... */
        val SPACE_AROUND = align(0.5f)
        /** ...OO...OO...OO... */
        val SPACE_EVENLY = Spacing({ n, s, c -> (s - c) / (n + 1) }, { n, s, c -> (s - c) / (n + 1) })
        /** ..OO....OO....OO.. */
        val SPACE_WRAP = Spacing({ n, s, c -> (s - c) / (n * 2) }, { n, s, c -> (s - c) / n })
        /** OO......OO......OO */
        val SPACE_BETWEEN = Spacing(0f) { n, s, c -> (s - c) / (n - 1) }
        /** OOOOOO............ */
        val SPACE_AFTER = Spacing(0f, 0f)
        /** ............OOOOOO */
        val SPACE_BEFORE = Spacing({ _, s, c -> s - c }, 0f)
        /** OO.OO.OO...... */
        fun fixed(spacing: Float) = Spacing(0f, spacing)
        /** ..<~OOOOOO~>.. */
        fun align(alignment: Float) = Spacing({ _, s, c -> (s - c) * alignment }, 0f)
    }
}

data class Spacing2(val fixed: Float, val spacer: (Float, Int) -> Float, val offset: (Float, Int) -> Float) {
    companion object {
        private val zero: (Float, Int) -> Float = { _, _ -> 0f }

        val SPACE_AROUND = align(0.5f)
        /** ...OO...OO...OO... */
        val SPACE_EVENLY = Spacing2(0f, { s, n -> s / (n + 1) }, { s, _ -> s / 2 })
        /** ..OO....OO....OO.. */
        val SPACE_WRAP = Spacing2(0f, { s, n -> s / n }, { s, _ -> s / 2 })
        /** OO......OO......OO */
        val SPACE_BETWEEN = Spacing2(0f, { s, n -> s / (n - 1) }, zero)
        /** OOOOOO............ */
        val SPACE_AFTER = Spacing2(0f, zero, zero)
        /** ............OOOOOO */
        val SPACE_BEFORE = Spacing2(0f, zero, { s, _ -> s })
        /** OO.OO.OO...... */
        fun fixed(spacing: Float) = Spacing2(spacing, zero, zero)
        /** ..<~OOOOOO~>.. */
        fun align(alignment: Float) = Spacing2(0f, zero, { s, _ -> s * alignment })
    }

    infix fun then(other: Spacing2): Spacing2 {
        return Spacing2(fixed + other.fixed, { s, n -> spacer(s, n) + other.spacer(s, n) }, { s, n -> offset(s, n) + other.offset(s, n) })
    }
}

internal fun Spacing2.fixed() = fixed

internal fun Spacing2.evaluate(space: Float, n: Int): Pair<Float, Float> {
    val spacing = fixed + spacer(space - (n - 1) * fixed, n)
    val offset = offset(space - (n - 1) * spacing, n)
    return Pair(offset, spacing)
}

infix fun Spacing.within(other: Spacing) = Spacing(
        { n, s, c -> other.init(n, s, c + (n - 1) + this.iter(n, s, c) + 2 * this.init(n, s, c)) },
        this.iter
)

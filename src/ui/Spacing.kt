package ui

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

infix fun Spacing.within(other: Spacing) = Spacing(
        { n, s, c -> other.init(n, s, c + (n-1) + this.iter(n, s, c) + 2 * this.init(n, s, c)) },
        this.iter
)

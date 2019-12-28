package ktaf.gui.core

/** Spacing between and around a set of nodes. */
open class Spacing internal constructor(
        val between: SpacingFunction,
        val around: SpacingFunction
) {
    /** Returns an initial offset and spacing between nodes. */
    fun apply(space: Float, children: Int): Pair<Float, Float> {
        val spacing = between(space, children)
        val offset = around(space - spacing * (children - 1), children)
        return offset to spacing
    }

    /** Returns the addition this spacing will make with zero free space. */
    fun minimum(children: Int): Float
            = between(0f, children) * (children - 1) + around(0f, children) * 2

    companion object {
        val AFTER = AroundSpacing { _, _ -> 0f }
        val BEFORE = AroundSpacing { s, _ -> s }
        val AROUND = AroundSpacing { s, _ -> s / 2 }

        val BETWEEN = BetweenSpacing { s, n -> s / (n - 1) }

        val WRAP = BetweenSpacing { s, n -> s / n } then AROUND
        val EVEN = BetweenSpacing { s, n -> s / (n + 1) } then AROUND
        val NONE = BetweenSpacing { _, _ -> 0f } then AFTER
    }
}

/** Spacing just around a set of nodes. */
class AroundSpacing(
        around: SpacingFunction
): Spacing({ _, _ -> 0f }, around) {
    companion object {
        fun exactly(pixels: Float) = AroundSpacing { _, _ -> pixels }
    }
}

/** Spacing just between nodes. */
class BetweenSpacing(
        between: SpacingFunction
): Spacing(between, { _, _ -> 0f }) {
    companion object {
        fun exactly(pixels: Float) = BetweenSpacing { _, _ -> pixels }
    }
}

/** Combines a between spacing value and around spacing value.
 *
 *  Note: the between spacing is always calculated first, with the around
 *        spacing being in respect to any remaining space. */
infix fun BetweenSpacing.then(spacing: AroundSpacing)
        = Spacing(between, spacing.around)

/** A function taking an available size and number of children, and returning
 *  a spacing value. */
typealias SpacingFunction = (Float, Int) -> Float

package ktaf.ui.elements

import ktaf.core.*
import ktaf.graphics.*
import ktaf.typeclass.plus
import ktaf.ui.layout.Border
import ktaf.ui.node.UINode
import ktaf.ui.node.push
import ktaf.ui.node.remove
import ktaf.util.AABB
import ktaf.util.getOverflow
import java.util.*
import kotlin.math.max

abstract class UITextBase: UINode() {
    val text = KTAFValue("")
    val lines = KTAFList<String>()
    val wrap = KTAFValue(true)

    open fun getBlockSpacing(line: Int): Float = 0f
    abstract fun generateSegments(line: String): List<UITextSegment>

    protected fun init() {
        lines.connectAddedIndexed { line, it ->
            segments.add(line, generateSegments(it))
            segmentsWrapped.add(line, wrapSegmentList(lines[line], segments[line], lastComputedWidth, wrap.get()))

            println(segments)
            println(segmentsWrapped)
        }

        lines.connectRemovedIndexed { line, _ ->
            segments.removeAt(line)
            segmentsWrapped.removeAt(line)
        }

        text.connect { lines.clear(); lines.addAll(it.split("\n")) }
    }

    protected fun updateLine(line: Int, value: String) {
        if (lines[line] != value) {
            lines.removeAt(line)
            lines.add(line, value)
        }
    }

    protected fun wrappingPropertyChanged() {
        segmentsWrapped = segments.mapIndexed { line, value ->
            wrapSegmentList(lines[line], value, lastComputedWidth, wrap.get())
        } .toMutableList()
    }

    protected fun segmentPropertyChanged() {
        segments = lines.map(this::generateSegments).toMutableList()
        wrappingPropertyChanged()
    }

    protected fun updateLine(line: Int, fn: (String) -> String) {
        updateLine(line, fn(lines[line]))
    }

    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        // TODO: internal offset for scrolling etc and alignment too
        val computedVerticalOffset = 0f
        val zero = vec2(0f)

        context.push {
            context.scissor = AABB(position, position + size)
            context.translate(vec2(0f, computedVerticalOffset))

            context.draw {
                context.colour = rgba(1f) // TODO
                rectangle(position, size)

                segmentsWrapped.forEachIndexed { blockIndex, block ->
                    val lineString = lines[blockIndex]

                    block.forEachIndexed { lineIndex, line ->
                        val lineHeight = line.map { it.font.height } .fold(0f, ::max)
                        val computedHorizontalOffset = 0f

                        context.translate(vec2(computedHorizontalOffset, 0f))

                        context.push {
                            line.forEach { segment ->
                                val segmentText = lineString.substring(segment.char1, segment.char1 + segment.length)
                                val segmentWidth = segment.font.widthOf(segmentText)

                                context.colour = segment.colour
                                write(segmentText, segment.font, zero)
                                context.translate(vec2(segmentWidth, 0f))
                            }
                        }

                        context.translate(vec2(0f, lineHeight))
                    }

                    context.translate(vec2(0f, getBlockSpacing(blockIndex)))
                }
            }
        }
    }

    override fun update(event: UpdateEvent) {
        if (computedWidth.get() != lastComputedWidth) {
            lastComputedWidth = computedWidth.get()
            wrappingPropertyChanged()
        }
    }

    override fun computeContentWidth(width: Float?): Float {
        return 0f // TODO
    }

    override fun computeContentHeight(width: Float, height: Float?): Float {
        return 0f // TODO
    }

    protected var segments: MutableList<List<UITextSegment>> = mutableListOf()
    private var segmentsWrapped: MutableList<List<List<UITextSegment>>> = mutableListOf()
    private var lastComputedWidth = 0f

    init {
        focused.connect { if (it) state.push(EDITING) else state.remove(EDITING) }
        padding(Border(8f, 16f))
    }

    companion object {
        const val EDITING = "editing"
    }
}

data class UITextSegment internal constructor(
        val font: Font,
        val colour: RGBA,
        val decoration: Set<UITextDecoration>,
        val line: Int,
        val char1: Int,
        val length: Int
)

enum class UITextDecoration {
    UNDERLINE,
    STRIKETHROUGH
}

private fun wrapSegmentList(
        line: String,
        segmentList: List<UITextSegment>,
        width: Float,
        wrap: Boolean
): List<List<UITextSegment>> {
    if (!wrap) return listOf(segmentList)

    var widthRemaining = width
    val output = mutableListOf(mutableListOf<UITextSegment>())
    val segmentQueue = LinkedList(segmentList)

    while (segmentQueue.isNotEmpty()) {
        val segment = segmentQueue.remove()
        val segmentWidth = segment.font.widthOf(line, segment.char1, segment.char1 + segment.length)

        if (segmentWidth > widthRemaining) {
            val overflow = getOverflow(line.substring(segment.char1, segment.char1 + segment.length), segment.font, widthRemaining)

            // TODO: trimming
            output.last().add(UITextSegment(
                    segment.font, segment.colour, segment.decoration,
                    segment.line,
                    segment.char1,
                    overflow - segment.char1
            ))
            output.add(mutableListOf())
            widthRemaining = width

            if (overflow < segment.length)
                segmentQueue.addFirst(UITextSegment(
                        segment.font, segment.colour, segment.decoration,
                        segment.line,
                        overflow,
                        segment.length - overflow
                ))
        }
        else {
            output.last().add(segment)
            widthRemaining -= segmentWidth
        }
    }

    return output
}

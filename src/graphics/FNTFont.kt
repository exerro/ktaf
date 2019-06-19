package graphics

import GLTexture2
import GLVAO
import createVAO
import div
import genElementBuffer
import genVertexColourBuffer
import genVertexNormalBuffer
import genVertexPositionBuffer
import genVertexUVBuffer
import loadTexture2D
import org.lwjgl.BufferUtils
import plus
import times
import vec2
import vec3
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

private typealias CharMap<T> = Map<Char, T>

// TODO: use baseline!

class FNTFont internal constructor(
        scale: Float,
        override val lineHeight: Float,
        override val baseline: Float,
        private val charSizes: CharMap<vec2>,
        private val charOffsets: CharMap<vec2>,
        private val charAdvances: CharMap<Float>,
        private val charKernings: CharMap<CharMap<Float>>,
        private val charVAOs: CharMap<GLVAO>,
        private val charTextures: CharMap<GLTexture2>
): Font(scale) {
    override fun scaleTo(height: Float): Font = FNTFont(
            height / lineHeight,
            lineHeight,
            baseline,
            charSizes,
            charOffsets,
            charAdvances,
            charKernings,
            charVAOs,
            charTextures
    )

    override fun getCharSize(char: Char): vec2 = charSizes[char]!!
    override fun getCharOffset(char: Char): vec2 = charOffsets[char]!!
    override fun getCharAdvance(char: Char): Float = charAdvances[char]!!
    override fun getKerning(char1: Char, char2: Char): Float = charKernings[char1]?.get(char2) ?: 0f
    override fun getVAO(char: Char): GLVAO = charVAOs[char]!!
    override fun getVAOVertexCount(char: Char) = 6
    override fun getTexture(char: Char): GLTexture2? = charTextures[char]

    companion object {
        val DEFAULT_FONT by lazy {
            load(FNTFont::class.java.getResourceAsStream("/font/open-sans/OpenSans-Regular.fnt"))
        }
    }
}

class FNTFontPreloader(
        internal val lineHeight: Float,
        internal val baseline: Float,
        internal val pages: Map<Int, String>,
        internal val charSizes: MutableMap<Char, vec2>,
        internal val charOffsets: MutableMap<Char, vec2>,
        internal val charUVs: MutableMap<Char, List<vec2>>,
        internal val charAdvances: MutableMap<Char, Float>,
        internal val charPages: MutableMap<Char, Int>,
        internal val kernings: MutableMap<Char, MutableMap<Char, Float>>
)

fun FNTFont.Companion.preload(content: String): FNTFontPreloader {
    val commonMatch = Regex("<common\\s+" +
            "lineHeight=\"(\\d+)\"\\s+" +
            "base=\"(\\d+)\"\\s+" +
            "scaleW=\"(\\d+)\"\\s+" +
            "scaleH=\"(\\d+)\"\\s+" +
            "pages=\"(\\d+)\"\\s+" +
            "packed=\"0\"/>").find(content) ?: error("Invalid common section in .fnt file")

    val pagesMatches = Regex("<page\\s+" +
            "id=\"(\\d+)\"\\s+" +
            "file=\"([^\"]*)\"\\s*" +
            "/>")
            .let { regex ->
                generateSequence(regex.find(content)) { regex.find(content, it.range.last + 1) }
            }

    val charMatches = Regex("<char\\s+" +
            "id=\"(\\d+)\"\\s+" +
            "x=\"(\\d+)\"\\s+" +
            "y=\"(\\d+)\"\\s+" +
            "width=\"(\\d+)\"\\s+" +
            "height=\"(\\d+)\"\\s+" +
            "xoffset=\"([+\\-]?\\d+)\"\\s+" +
            "yoffset=\"([+\\-]?\\d+)\"\\s+" +
            "xadvance=\"(\\d+)\"\\s+" +
            "page=\"(\\d+)\"\\s+" +
            "chnl=\"\\d+\"\\s*" +
            "/>")
            .let { regex ->
                generateSequence(regex.find(content)) { regex.find(content, it.range.last + 1) }
            }

    val kerningMatches = Regex("<kerning\\s+" +
            "first=\"(\\d+)\"\\s+" +
            "second=\"(\\d+)\"\\s+" +
            "amount=\"([+-]?\\d+)\"\\s+" +
            "/>")
            .let { regex ->
                generateSequence(regex.find(content)) { regex.find(content, it.range.last + 1) }
            }

    val lineHeight = commonMatch.groupValues[1].toInt()
    val baseline = commonMatch.groupValues[2].toInt()
    val scaleW = commonMatch.groupValues[3].toInt()
    val scaleH = commonMatch.groupValues[4].toInt()
    val pageIDs = 0 until commonMatch.groupValues[5].toInt()
    val pages = pagesMatches.map { it.groupValues[1].toInt() to it.groupValues[2] } .toMap()
    val charSizes = mutableMapOf<Char, vec2>()
    val charOffsets = mutableMapOf<Char, vec2>()
    val charUVs = mutableMapOf<Char, List<vec2>>()
    val charAdvances = mutableMapOf<Char, Float>()
    val charPages = mutableMapOf<Char, Int>()
    val kernings = mutableMapOf<Char, MutableMap<Char, Float>>()

    charMatches.forEach { match ->
        val id = match.groupValues[1].toInt()
        val x = match.groupValues[2].toInt()
        val y = match.groupValues[3].toInt()
        val width = match.groupValues[4].toInt()
        val height = match.groupValues[5].toInt()
        val xoffset = match.groupValues[6].toInt()
        val yoffset = match.groupValues[7].toInt()
        val xadvance = match.groupValues[8].toInt()
        val page = match.groupValues[9].toInt()
        val char = id.toChar()
        val uv = vec2(x.toFloat(), y.toFloat()) / vec2(scaleW.toFloat(), scaleH.toFloat())
        val duv = vec2(width.toFloat(), height.toFloat()) /  vec2(scaleW.toFloat(), scaleH.toFloat())

        charSizes[char] = vec2(width.toFloat(), height.toFloat())
        charOffsets[char] = vec2(xoffset.toFloat(), yoffset.toFloat())
        charUVs[char] = listOf(uv, uv + vec2(0f, duv.y), uv + duv, uv + vec2(duv.x, 0f))
        charAdvances[char] = xadvance.toFloat()
        charPages[char] = page
    }

    kerningMatches.forEach { match ->
        val first = match.groupValues[1].toInt()
        val second = match.groupValues[2].toInt()
        val amount = match.groupValues[3].toInt()

        (kernings.computeIfAbsent(first.toChar()) { mutableMapOf() }) [second.toChar()] = amount.toFloat()
    }

    pageIDs.forEach { if (!pages.contains(it)) error("Missing page '$it'") }

    return FNTFontPreloader(
            lineHeight.toFloat(),
            baseline.toFloat(),
            pages,
            charSizes,
            charOffsets,
            charUVs,
            charAdvances,
            charPages,
            kernings
    )
}

fun FNTFont.Companion.preloadFile(file: String): FNTFontPreloader
        = preload(String(Files.readAllBytes(Paths.get(file))))

fun FNTFont.Companion.load(preloader: FNTFontPreloader): FNTFont {
//    internal val lineHeight: Float,
//    internal val baseline: Float,
//    internal val pages: Map<Int, String>,
//    internal val charSizes: MutableMap<Char, vec2>,
//    internal val charOffsets: MutableMap<Char, vec2>,
//    internal val charUVs: MutableMap<Char, List<vec2>>,
//    internal val charAdvances: MutableMap<Char, Float>,
//    internal val charPages: MutableMap<Char, Int>
    val vaos = preloader.charUVs.map { (char, uvs) ->
        char to createVAO {
            genVertexPositionBuffer(listOf(
                    vec2(0f, 0f),
                    vec2(0f, 1f),
                    vec2(1f, 1f),
                    vec2(1f, 0f)
            ).map { it * preloader.charSizes[char]!! } .map { (x, y) -> vec3(x, y, 0f) })
            genVertexNormalBuffer(List(4) { vec3(0f, 0f, 1f) })
            genVertexUVBuffer(uvs)
            genVertexColourBuffer(4)
            genElementBuffer(listOf(0, 1, 2, 0, 2, 3))
        }
    } .toMap()

    val textureObjects = preloader.pages.map { (page, file) ->
        val input = FNTFont::class.java.getResourceAsStream(file)
        val byteArray = input.readAllBytes()
        val byteBuffer = BufferUtils.createByteBuffer(byteArray.size)
        byteBuffer.put(byteArray)
        byteBuffer.flip()
        page to loadTexture2D(byteBuffer)
    } .toMap()

    val textures = preloader.charPages.map { (char, page) ->
        char to textureObjects[page]!!
    } .toMap()

    return FNTFont(
            1f,
            preloader.lineHeight,
            preloader.baseline,
            preloader.charSizes,
            preloader.charOffsets,
            preloader.charAdvances,
            preloader.kernings,
            vaos,
            textures
    )
}

fun FNTFont.Companion.load(content: String): FNTFont
        = load(preload(content))

fun FNTFont.Companion.load(input: InputStream): FNTFont
        = load(String(input.readAllBytes()))

fun FNTFont.Companion.loadFile(file: String): FNTFont
        = load(preloadFile(file))

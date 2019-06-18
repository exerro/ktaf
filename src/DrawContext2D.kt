import kotlin.math.max

fun DrawContext2D.rectangle(position: vec2, size: vec2) {
    if (fill) {
        vao(rectangleVAO, 6,
                mat4_translate(position.vec3(0f)) *
                mat3_scale(size.vec3(1f)).mat4())
    }
    else {
        vao(rectangleVAO, 6,
                mat4_translate(position.vec3(0f)) *
                mat3_scale(vec3(size.x, lineWidth, 1f)).mat4())

        vao(rectangleVAO, 6,
                mat4_translate(position.vec3(0f)) *
                mat3_scale(vec3(lineWidth, size.y, 1f)).mat4())

        vao(rectangleVAO, 6,
                mat4_translate(vec3(position.x, position.y + size.y - lineWidth, 0f)) *
                mat3_scale(vec3(size.x, lineWidth, 1f)).mat4())

        vao(rectangleVAO, 6,
                mat4_translate(vec3(position.x + size.x - lineWidth, position.y, 0f)) *
                mat3_scale(vec3(lineWidth, size.y, 1f)).mat4())
    }
}

fun DrawContext2D.circle(position: vec2, radius: Float) {
    if (fill) {
        val points = calculateCirclePointCount(radius)
        vao(circleVAO(points), 3 * (points + 1),
                mat4_translate(position.vec3(0f)) *
                mat3_scale(radius).mat4())
    }
    else {
        TODO("outline circle rendering not yet implemented")
    }
}

fun DrawContext2D.image(image: GLTexture2, position: vec2 = vec2(0f), scale: vec2 = vec2(1f)) {
    image.useIn(0) {
        vao(rectangleVAO, 6,
                mat4_translate(position.vec3(0f)) *
                mat3_scale(scale.vec3(1f) * vec3(image.width.toFloat(), image.height.toFloat(), 1f)).mat4(),
                true
        )
    }
}

fun <T> DrawContext2D.draw(fn: DrawContext2D.() -> T)
        = fn(this)

fun <T> DrawContext2D.push(fn: DrawContext2D.() -> T): T {
    push()
    val result = fn()
    pop()
    return result
}

class DrawContext2D(val viewport: GLViewport) {
    var fill: Boolean
        get() = activeState.fill
        set(fill) { activeState.fill = fill }

    var colour: vec3
        get() = activeState.colour
        set(colour) { activeState.colour = colour }

    var lineWidth: Float
        get() = activeState.lineWidth
        set(width) { activeState.lineWidth = width }

    var scissor: AABB?
        get() = states.fold(state.scissor) { a, b -> b.scissor?.let { a?.intersection(it) } }
        set(scissor) { activeState.scissor = scissor?.intersection(AABB(vec2(0f), viewport.size)) }

    fun push() {
        states.add(DrawState(activeState))
    }

    fun pop() {
        if (states.isNotEmpty()) states.removeAt(states.size - 1)
    }

    fun translate(translation: vec2) {
        activeState.transform *= mat4_translate(translation.vec3(0f))
    }

    fun rotate(theta: Float) {
        activeState.transform *= mat3_rotate(theta, vec3(0f, 0f, -1f)).mat4()
    }

    fun rotateAbout(position: vec2, theta: Float) {
        translate(position)
        rotate(theta)
        translate(-position)
    }

    fun scale(scale: vec2) {
        activeState.transform *= mat3_scale(scale.vec3(1f)).mat4()
    }

    fun scale(scale: Float) {
        activeState.transform *= mat3_scale(vec3(scale, scale, 1f)).mat4()
    }

    fun vao(vao: GLVAO, vertexCount: Int, customTransform: mat4 = mat4_identity, textured: Boolean = false, mode: GLDrawMode = GLDrawMode.GL_TRIANGLES) {
        val sOffset = viewport.offset + vec2(scissor?.min?.x ?: 0f, viewport.size.y - (scissor?.max?.y ?: viewport.size.y))
        val sSize = scissor?.max?.minus(scissor?.min ?: vec2(0f)) ?: viewport.size
        GLViewport(sOffset.x.toInt(), sOffset.y.toInt(), sSize.x.toInt(), sSize.y.toInt()).setGLViewport()
        shaderProgram2D.uniform("transform", transform * customTransform)
        shaderProgram2D.uniform("colour", activeState.colour)
        shaderProgram2D.uniform("useTexture", textured)
        // TODO: replace with lwjgl-kt state calls
        //  maybe wrap drawing in some state-restoring function call that also sets the shader
        // GL11.glEnable(GL11.GL_BLEND)
        // GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        shaderProgram2D.useIn {
            vao.bindIn {
                GLDraw.drawElements(mode, vertexCount, 0)
            }
        }
    }

    private val state = DrawState()

    private val states: MutableList<DrawState> = mutableListOf()

    private val activeState: DrawState
        get() = if (states.isNotEmpty()) states.last() else state

    private val transform: mat4
        get() = (scissor?.let { it.max - it.min } ?: viewport.size) .let { viewSize ->
            mat4_identity *
                    mat3_scale(vec3(1f, -1f, 1f)).mat4() *
                    mat4_translate(vec3(-1f, -1f, 0f)) *
                    mat3_scale(vec3(2 / viewSize.x, 2 / viewSize.y, 1f)).mat4() *
                    mat4_translate(-vec3(scissor?.min?.x ?: 0f, scissor?.min?.y ?: 0f, 0f)) *
                    states.fold(state.transform) { a, b -> a * b.transform }
        }

    private var shaderProgram2D: GLShaderProgram = createGLShaderProgram {
        val fragment = shader(GLShaderType.GL_VERTEX_SHADER, "#version 400 core\n" +
                "\n" +
                "// model attributes\n" +
                "layout (location=0) in vec3 vertex;\n" +
                "layout (location=1) in vec2 vertex_uv;\n" +
                "layout (location=3) in vec3 vertex_colour;\n" +
                "\n" +
                "out vec3 fragment_colour;\n" +
                "out vec2 fragment_uv;\n" +
                "\n" +
                "uniform mat4 transform;\n" +
                "\n" +
                "void main(void) {\n" +
                "    gl_Position = transform * vec4(vertex, 1);\n" +
                "    fragment_colour = vertex_colour;\n" +
                "    fragment_uv = vertex_uv;\n" +
                "}")

        val vertex = shader(GLShaderType.GL_FRAGMENT_SHADER, "#version 400 core\n" +
                "\n" +
                "in vec3 fragment_colour;\n" +
                "in vec2 fragment_uv;\n" +
                "\n" +
                "uniform sampler2D textureSampler;\n" +
                "uniform vec3 colour = vec3(1, 1, 1);\n" +
                "uniform bool useTexture = false;\n" +
                "\n" +
                "void main(void) {\n" +
                "    gl_FragColor = vec4(colour * fragment_colour, 1.0);\n" +
                "    if (useTexture) gl_FragColor *= texture(textureSampler, fragment_uv);\n" +
                "}")

        link()
        validate()
        detach(fragment)
        detach(vertex)
    }

    private class DrawState(parent: DrawState? = null) {
        var fill: Boolean = parent?.fill ?: true
        var transform: mat4 = mat4_identity
        var colour: vec3 = parent?.colour ?: vec3(1f)
        var lineWidth: Float = parent?.lineWidth ?: 1f
        var scissor: AABB? = null
    }
}

private val circleCache = LinkedHashMap<Int, GLVAO>()
private const val MAX_CACHE_SIZE = 15

private fun calculateCirclePointCount(radius: Float)
        = max(radius.toInt(), 3)

private fun circleVAO(numPoints: Int): GLVAO {
    if (circleCache.size >= MAX_CACHE_SIZE) {
        for ((k, _) in circleCache) {
            circleCache.remove(k)
            break
        }
    }

    return circleCache.computeIfAbsent(numPoints) {
        createElementGLVAO(
                (1 .. numPoints).flatMap { i -> listOf(0, i, i % numPoints + 1) },
                (listOf(vec3(0f)) + (0 until numPoints).map { i -> mat3_rotate(i / numPoints.toFloat() * Math.PI.toFloat() * 2, vec3(0f, 0f, -1f)) * vec3(1f, 0f, 0f) }),
                List(numPoints + 1) { vec3(0f, 0f, 1f) }
        )
    }
}

private var rectangleVAO: GLVAO = createVAO {
    genElementBuffer(listOf(0, 1, 2, 3, 2, 0))
    genVertexPositionBuffer(listOf(
            vec3(0f, 1f, 0f),
            vec3(0f, 0f, 0f),
            vec3(1f, 0f, 0f),
            vec3(1f, 1f, 0f)
    ))
    genVertexNormalBuffer(List(4) { vec3(0f, 0f, 1f) })
    genVertexUVBuffer(listOf(
            vec2(0f, 1f),
            vec2(0f, 0f),
            vec2(1f, 0f),
            vec2(1f, 1f)
    ))
    genVertexColourBuffer(4)
}

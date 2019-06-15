import java.nio.file.Files
import java.nio.file.Paths

const val VERTEX_POSITION_ATTRIBUTE = 0
const val VERTEX_UV_ATTRIBUTE = 1
const val VERTEX_NORMAL_ATTRIBUTE = 2
const val VERTEX_COLOUR_ATTRIBUTE = 3

fun GLVAO.genAttributeFloatBuffer(
        data: List<vec3>,
        attribute: Int,
        dataSize: GLNumComponents,
        usage: GLBufferUsage = GLBufferUsage.GL_STATIC_DRAW
): GLVBO = bindIn { vbo(GLBufferType.GL_ARRAY_BUFFER) {
    bindIn { vertexAttribPointer(attribute, dataSize, GLAttribPointerType.GL_FLOAT, false, 0, 0) }
    enableVertexAttribArray(attribute)
    data(data.flatMap { it.unpack().toList() } .toFloatArray(), usage)
} }

fun GLVAO.genVertexPositionBuffer(data: List<vec3>, usage: GLBufferUsage = GLBufferUsage.GL_STATIC_DRAW): GLVBO = genAttributeFloatBuffer(
        data,
        VERTEX_POSITION_ATTRIBUTE,
        GLNumComponents.THREE,
        usage
)

fun GLVAO.genVertexUVBuffer(data: List<vec2>, usage: GLBufferUsage = GLBufferUsage.GL_STATIC_DRAW): GLVBO = bindIn { vbo(GLBufferType.GL_ARRAY_BUFFER) {
    bindIn { vertexAttribPointer(VERTEX_UV_ATTRIBUTE, GLNumComponents.TWO, GLAttribPointerType.GL_FLOAT, false, 0, 0) }
    enableVertexAttribArray(VERTEX_UV_ATTRIBUTE)
    data(data.flatMap { it.unpack().toList() } .toFloatArray(), usage)
} }

fun GLVAO.genVertexNormalBuffer(data: List<vec3>, usage: GLBufferUsage = GLBufferUsage.GL_STATIC_DRAW): GLVBO = genAttributeFloatBuffer(
        data,
        VERTEX_NORMAL_ATTRIBUTE,
        GLNumComponents.THREE,
        usage
)

fun GLVAO.genVertexColourBuffer(data: List<RGB>, usage: GLBufferUsage = GLBufferUsage.GL_STATIC_DRAW): GLVBO = genAttributeFloatBuffer(
        data,
        VERTEX_COLOUR_ATTRIBUTE,
        GLNumComponents.THREE,
        usage
)

fun GLVAO.genVertexColourBuffer(size: Int, usage: GLBufferUsage = GLBufferUsage.GL_STATIC_DRAW): GLVBO = genAttributeFloatBuffer(
        List(size) { vec3(1f) },
        VERTEX_COLOUR_ATTRIBUTE,
        GLNumComponents.THREE,
        usage
)

fun GLShaderProgram.shader(type: GLShaderType, source: String): GLShader {
    val shader = createGLShader(type) {
        source(source)
        compile()
    }
    attach(shader)
    return shader
}

fun GLShaderProgram.shaderFile(type: GLShaderType, sourceFile: String): GLShader
        = shader(type, String(Files.readAllBytes(Paths.get(sourceFile))))

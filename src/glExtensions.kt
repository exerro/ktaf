import java.nio.file.Files
import java.nio.file.Paths

const val VERTEX_POSITION_ATTRIBUTE = 0
const val VERTEX_UV_ATTRIBUTE = 1
const val VERTEX_NORMAL_ATTRIBUTE = 2
const val VERTEX_COLOUR_ATTRIBUTE = 3

val GLViewport.offset: vec2
    get() = vec2(x().toFloat(), y().toFloat())

val GLViewport.size: vec2
    get() = vec2(width().toFloat(), height().toFloat())

fun GLVAO.genElementBuffer(elements: List<Int>, usage: GLBufferUsage = GLBufferUsage.GL_STATIC_DRAW)
        = bindIn {
              val vbo = createVBO(GLBufferType.GL_ELEMENT_ARRAY_BUFFER) {
                  data(elements.toIntArray(), usage)
                  this.bind()
              }
              vbo(vbo)
          }

fun GLVAO.genAttributeFloatBuffer(
        data: List<vec3>,
        attribute: Int,
        dataSize: GLNumComponents,
        usage: GLBufferUsage = GLBufferUsage.GL_STATIC_DRAW
): GLVBO = bindIn {
    val vbo = createVBO(GLBufferType.GL_ARRAY_BUFFER) {
        bindIn { vertexAttribPointer(attribute, dataSize, GLAttribPointerType.GL_FLOAT, false, 0, 0) }
        enableVertexAttribArray(attribute)
        data(data.flatMap { it.unpack().toList() } .toFloatArray(), usage)
    }
    vbo(vbo)
}

fun GLVAO.genVertexPositionBuffer(data: List<vec3>, usage: GLBufferUsage = GLBufferUsage.GL_STATIC_DRAW): GLVBO = genAttributeFloatBuffer(
        data,
        VERTEX_POSITION_ATTRIBUTE,
        GLNumComponents.THREE,
        usage
)

fun GLVAO.genVertexUVBuffer(data: List<vec2>, usage: GLBufferUsage = GLBufferUsage.GL_STATIC_DRAW): GLVBO = bindIn {
    val vbo = createVBO(GLBufferType.GL_ARRAY_BUFFER) {
        bindIn { vertexAttribPointer(VERTEX_UV_ATTRIBUTE, GLNumComponents.TWO, GLAttribPointerType.GL_FLOAT, false, 0, 0) }
        enableVertexAttribArray(VERTEX_UV_ATTRIBUTE)
        data(data.flatMap { it.unpack().toList() } .toFloatArray(), usage)
    }
    vbo(vbo)
}

fun GLVAO.genVertexNormalBuffer(data: List<vec3>, usage: GLBufferUsage = GLBufferUsage.GL_STATIC_DRAW): GLVBO = genAttributeFloatBuffer(
        data,
        VERTEX_NORMAL_ATTRIBUTE,
        GLNumComponents.THREE,
        usage
)

fun GLVAO.genVertexColourBuffer(data: List<vec3>, usage: GLBufferUsage = GLBufferUsage.GL_STATIC_DRAW): GLVBO = genAttributeFloatBuffer(
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

fun GLShaderProgram.uniform(uniform: String, value: Boolean) { uniform1i(uniformLocation(uniform), if (value) 1 else 0) }
fun GLShaderProgram.uniform(uniform: String, value: Int    ) { uniform1i(uniformLocation(uniform), value) }
fun GLShaderProgram.uniform(uniform: String, value: Float  ) { uniform1f(uniformLocation(uniform), value) }
fun GLShaderProgram.uniform(uniform: String, value: vec2   ) { uniform2f(uniformLocation(uniform), value.x, value.y) }
fun GLShaderProgram.uniform(uniform: String, value: vec3   ) { uniform3f(uniformLocation(uniform), value.x, value.y, value.z) }
fun GLShaderProgram.uniform(uniform: String, value: vec4   ) { uniform4f(uniformLocation(uniform), value.x, value.y, value.z, value.w) }
fun GLShaderProgram.uniform(uniform: String, value: mat3   ) { uniformMatrix3ft(uniformLocation(uniform), value.elements) }
fun GLShaderProgram.uniform(uniform: String, value: mat4   ) { uniformMatrix4ft(uniformLocation(uniform), value.elements) }

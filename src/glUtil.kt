
fun createStandardGLVAO(vertices: List<vec3>, normals: List<vec3>, uvs: List<vec2>, colours: List<RGB>) = createVAO {
    genVertexPositionBuffer(vertices)
    genVertexNormalBuffer(normals)
    genVertexUVBuffer(uvs)
    genVertexColourBuffer(colours)
}

fun createStandardGLVAO(vertices: List<vec3>, normals: List<vec3>, uvs: List<vec2>, colours: Boolean = true) = createVAO {
    genVertexPositionBuffer(vertices)
    genVertexNormalBuffer(normals)
    genVertexUVBuffer(uvs)
    if (colours) genVertexColourBuffer(vertices.size)
}

fun createStandardGLVAO(vertices: List<vec3>, normals: List<vec3>, colours: Boolean = true) = createVAO {
    genVertexPositionBuffer(vertices)
    genVertexNormalBuffer(normals)
    if (colours) genVertexColourBuffer(vertices.size)
}

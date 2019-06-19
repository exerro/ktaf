
fun createStandardGLVAO(vertices: List<vec3>, normals: List<vec3>, uvs: List<vec2>, colours: List<vec3>) = createVAO {
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

fun createElementGLVAO(elements: List<Int>, vertices: List<vec3>, normals: List<vec3>, uvs: List<vec2>, colours: List<vec3>) = createVAO {
    genVertexPositionBuffer(vertices)
    genVertexNormalBuffer(normals)
    genVertexUVBuffer(uvs)
    genVertexColourBuffer(colours)
    genElementBuffer(elements)
}

fun createElementGLVAO(elements: List<Int>, vertices: List<vec3>, normals: List<vec3>, uvs: List<vec2>, colours: Boolean = true) = createVAO {
    genVertexPositionBuffer(vertices)
    genVertexNormalBuffer(normals)
    genVertexUVBuffer(uvs)
    if (colours) genVertexColourBuffer(vertices.size)
    genElementBuffer(elements)
}

fun createElementGLVAO(elements: List<Int>, vertices: List<vec3>, normals: List<vec3>, colours: Boolean = true) = createVAO {
    genVertexPositionBuffer(vertices)
    genVertexNormalBuffer(normals)
    if (colours) genVertexColourBuffer(vertices.size)
    genElementBuffer(elements)
}

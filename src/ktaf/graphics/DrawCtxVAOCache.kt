package ktaf.graphics

import geometry.vec2
import geometry.vec3
import ktaf.core.*
import lwjglkt.createVAO

internal class DrawCtxVAOCache {
    val rectangleVAO by lazy {
        createVAO {
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
    }

    val quadVAO by lazy {
        createVAO {
            genElementBuffer(listOf(0, 1, 2, 3, 2, 0))
            genVertexPositionBuffer(listOf(
                    vec3(0f, 0f, 0f),
                    vec3(0f, 0f, 0f),
                    vec3(0f, 0f, 0f),
                    vec3(0f, 0f, 0f)
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
    }

    val triangleVAO by lazy {
        createVAO {
            genElementBuffer(listOf(0, 1, 2))
            genVertexPositionBuffer(listOf(
                    vec3(0f, 1f, 0f),
                    vec3(0f, 0f, 0f),
                    vec3(1f, 0f, 0f)
            ))
            genVertexNormalBuffer(List(3) { vec3(0f, 0f, 1f) })
            genVertexUVBuffer(listOf(
                    vec2(0f, 1f),
                    vec2(0f, 0f),
                    vec2(1f, 0f)
            ))
            genVertexColourBuffer(3)
        }
    }
}

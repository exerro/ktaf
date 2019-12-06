package ktaf.core

import geometry.*
import ktaf.data.Value
import ktaf.data.property.mutableProperty
import ktaf.data.property.vec3AnimatedProperty
import ktaf.util.compareTo

// TODO
abstract class Camera(
        private val screenSize: Value<vec2>
) {
    val viewportPosition = mutableProperty(vec2(0f))
    val viewportSize = mutableProperty(screenSize.value)
    val position = vec3AnimatedProperty(vec3_zero)
    val rotation = vec3AnimatedProperty(vec3_zero)

    abstract val projection: mat4
    abstract val inverseProjection: mat4

    val transform get()
    = projection * mat4_translate(-position.value) * rotation.value.toInverseRotationMatrix().mat4()

    val inverseTransform get()
    = rotation.value.toRotationMatrix().mat4() * mat4_translate(position.value) * inverseProjection

    /** Use a point in viewport coordinates to generate a ray going into the scene. */
    fun project(point: vec2): Ray
            = TODO()

    init {
        viewportSize <- screenSize
    }
}

class Ray(val position: vec3, direction: vec3) {
    val direction = direction.normalise()

    fun extend(length: Float)
            = position + direction * length

    fun intersection(plane: Plane)
            = extend((position - plane.point) dot plane.normal)
}

class Plane(val point: vec3, normal: vec3) {
    val normal = normal.normalise()
}

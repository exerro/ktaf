package ktaf.resource

import lwjglkt.gl.GLContext
import lwjglkt.gl.GLCurrentContext

abstract class GLResourceLoader<T: Any>(
        val context: GLContext
): ResourceLoader<T>() {
    protected abstract fun loadWithCurrentContext(
            resource: ResourceIdentifier,
            current: GLCurrentContext
    ): T

    override fun load(resource: ResourceIdentifier): T {
        return context.waitToMakeCurrent { loadWithCurrentContext(resource, it) }
    }

    companion object {
        fun <T: Any> create(
                context: GLContext,
                fn: (ResourceIdentifier, GLCurrentContext) -> T
        ): GLResourceLoader<T> {
            return object: GLResourceLoader<T>(context) {
                override fun loadWithCurrentContext(
                        resource: ResourceIdentifier,
                        current: GLCurrentContext
                ) = fn(resource, current)
            }
        }
    }
}

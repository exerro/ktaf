package ktaf.resource

import lwjglkt.gl.GLContext
import lwjglkt.gl.GLCurrentContext
import lwjglkt.gl.GLTexture2
import lwjglkt.util.loadTexture2D

class ImageResourceLoader(
        context: GLContext
): GLResourceLoader<GLTexture2>(context) {
    override fun loadWithCurrentContext(
            resource: ResourceIdentifier,
            current: GLCurrentContext
    ): GLTexture2 {
        return current.loadTexture2D(resource.bytes)
    }
}

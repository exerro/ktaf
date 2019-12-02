package ktaf.core

import ktaf.graphics.Font
import lwjglkt.gl.GLContext

class FNTFontLoader(
        private val resource: ResourceStream,
        private val glContext: GLContext
): ResourceLoader<Font>() {
    override fun load(): Font {
        resource.content()
        TODO()
    }
}

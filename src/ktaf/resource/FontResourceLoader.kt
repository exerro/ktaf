package ktaf.resource

import ktaf.graphics.FNTFont
import ktaf.graphics.Font
import ktaf.graphics.load
import lwjglkt.gl.GLContext
import lwjglkt.gl.GLCurrentContext

class FNTFontResourceLoader(
        context: GLContext
): GLResourceLoader<Font>(context) {
    override fun loadWithCurrentContext(resource: ResourceIdentifier, current: GLCurrentContext): Font {
        return FNTFont.load(context, resource.content)
    }
}

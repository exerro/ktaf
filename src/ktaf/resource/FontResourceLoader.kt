package ktaf.resource

import lwjglkt.gl.GLContext
import lwjglkt.gl.GLCurrentContext
import lwjglktx.font.FNTFont
import lwjglktx.font.Font
import lwjglktx.font.load

class FNTFontResourceLoader(
        context: GLContext
): GLResourceLoader<Font>(context) {
    override fun loadWithCurrentContext(resource: ResourceIdentifier, current: GLCurrentContext): Font {
        return FNTFont.load(context, resource.content)
    }
}

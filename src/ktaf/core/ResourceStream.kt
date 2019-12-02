package ktaf.core

import java.nio.file.Files
import java.nio.file.Paths

sealed class ResourceStream {
    abstract fun content(): String

    companion object {
        fun content(str: String) = StringResourceStream(str) as ResourceStream
        fun resource(url: String) = JavaResourceStream(url) as ResourceStream
        fun file(path: String) = FileResourceStream(path) as ResourceStream
    }
}

private class StringResourceStream(
        private val content: String
): ResourceStream() {
    override fun content() = content
}

private class JavaResourceStream(
        private val url: String
): ResourceStream() {
    override fun content()
            = String(this::class.java.getResourceAsStream(url).readAllBytes())
}

private class FileResourceStream(
        private val path: String
): ResourceStream() {
    override fun content()
            = String(Files.readAllBytes(Paths.get(path)))
}

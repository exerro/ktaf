package ktaf.resource

import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Paths

sealed class ResourceIdentifier {
    abstract val content: String
    abstract val bytes: ByteBuffer

    companion object {
        fun fromContent(content: String)
                = ContentResourceIdentifier(content) as ResourceIdentifier

        fun fromFile(path: String)
                = FileResourceIdentifier(path) as ResourceIdentifier
    }
}

private class ContentResourceIdentifier(
        override val content: String
): ResourceIdentifier() {
    override val bytes get()
            = ByteBuffer.wrap(content.toByteArray())
}

private class FileResourceIdentifier(
        val path: String
): ResourceIdentifier() {
    override val content get()
            = String(Files.readAllBytes(Paths.get(path)))

    override val bytes: ByteBuffer get() {
        val file = RandomAccessFile(File(path), "r")
        return file.channel.map(FileChannel.MapMode.READ_ONLY, 0, file.channel.size())
    }
}

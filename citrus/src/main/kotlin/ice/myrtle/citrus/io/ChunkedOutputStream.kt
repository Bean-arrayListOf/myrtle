package ice.myrtle.citrus.io

import com.google.gson.Gson
import java.io.*
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class ChunkedOutputStream(
	private val outputDir: String?,
	private val fileName: String?,
	private val fileDescription: String
) : OutputStream() {
	private var chunkNumber = 0
	private val buffer: ByteArrayOutputStream
	private val sha512Digest: MessageDigest
	private val gson = Gson()

	init {
		this.buffer = ByteArrayOutputStream()
		this.sha512Digest = MessageDigest.getInstance("SHA-512")
	}

	@Throws(IOException::class)
	override fun write(b: Int) {
		buffer.write(b)
		sha512Digest.update(b.toByte())
		if (buffer.size() >= CHUNK_SIZE) {
			flushChunk()
		}
	}

	@Throws(IOException::class)
	override fun write(b: ByteArray?, off: Int, len: Int) {
		buffer.write(b, off, len)
		sha512Digest.update(b, off, len)
		if (buffer.size() >= CHUNK_SIZE) {
			flushChunk()
		}
	}

	@Throws(IOException::class)
	override fun flush() {
		if (buffer.size() > 0) {
			flushChunk()
		}
	}

	@Throws(IOException::class)
	override fun close() {
		flush()
		buffer.close()
	}

	@Throws(IOException::class)
	private fun flushChunk() {
		val data = buffer.toByteArray()
		val hash = sha512Digest.digest(data)

		// Create metadata
		val metadata = Metadata(FILE_TYPE, VERSION, chunkNumber, bytesToHex(hash))
		val metadataJson = gson.toJson(metadata)
		val metadataBytes = metadataJson.toByteArray(StandardCharsets.UTF_8)

		// Write file description, metadata, and data to file
		val chunkFileName = String.format("%s_%05d.chunk.dat", fileName, chunkNumber)
		val chunkFile = File(outputDir, chunkFileName)
		FileOutputStream(chunkFile).use { fos ->
			// Write file description length (4 bytes)
			val descriptionBytes = fileDescription.toByteArray(StandardCharsets.UTF_8)
			fos.write(intToBytes(descriptionBytes.size))
			// Write file description
			fos.write(descriptionBytes)
			// Write metadata length (4 bytes)
			fos.write(intToBytes(metadataBytes.size))
			// Write metadata
			fos.write(metadataBytes)
			// Write data
			fos.write(data)
		}
		// Reset buffer and increment chunk number
		buffer.reset()
		chunkNumber++
	}

	private fun intToBytes(value: Int): ByteArray {
		return byteArrayOf(
			(value shr 24).toByte(),
			(value shr 16).toByte(),
			(value shr 8).toByte(),
			value.toByte()
		)
	}

	private fun bytesToHex(bytes: ByteArray): String {
		val hex = StringBuilder()
		for (b in bytes) {
			hex.append(String.format("%02x", b))
		}
		return hex.toString()
	}

	private class Metadata(
		private val fileType: String?,
		private val version: Int,
		private val chunkNumber: Int,
		private val hash: String?
	)

	companion object {
		private const val FILE_TYPE = "CHUNKED_DAT"
		private const val VERSION = 1
		private val CHUNK_SIZE = 1024 * 1024 // 1MB per chunk
	}
}
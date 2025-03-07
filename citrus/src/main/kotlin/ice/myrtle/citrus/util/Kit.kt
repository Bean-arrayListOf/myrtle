package ice.myrtle.citrus.util

import java.io.*
import java.net.URL
import java.util.*

val contextClassLoader = Thread.currentThread().contextClassLoader

const val ANSI_RED = "\u001B[1;31m"
const val ANSI_GREEN = "\u001B[1;32m"
const val ANSI_YELLOW = "\u001B[1;33m"
const val ANSI_BLUE = "\u001B[1;34m"
const val ANSI_PURPLE = "\u001B[1;35m"
const val ANSI_CYAN = "\u001B[1;36m"
const val ANSI_WHITE = "\u001B[1;37m"
const val ANSI_RESET = "\u001B[0m"
const val ANSI_BLACK = "\u001B[30m"
const val ANSI_RED_BACKGROUND = "\u001B[41m"
const val ANSI_GREEN_BACKGROUND = "\u001B[42m"
const val ANSI_YELLOW_BACKGROUND = "\u001B[43m"
const val ANSI_BLUE_BACKGROUND = "\u001B[44m"
const val ANSI_PURPLE_BACKGROUND = "\u001B[45m"
const val ANSI_CYAN_BACKGROUND = "\u001B[46m"
const val ANSI_WHITE_BACKGROUND = "\u001B[47m"
const val ANSI_BLACK_BACKGROUND = "\u001B[40m"
const val ANSI_RESET_BACKGROUND = "\u001B[49m"
const val ANSI_BOLD = "\u001B[1m"
const val ANSI_ITALIC = "\u001B[3m"
const val ANSI_UNDERLINE = "\u001B[4m"
const val ANSI_BLINK = "\u001B[5m"
const val ANSI_INVERSE = "\u001B[7m"
const val ANSI_INVISIBLE = "\u001B[8m"
const val ANSI_STRIKETHROUGH = "\u001B[9m"

fun fileOf(vararg path: String): File{
	return File(path.joinToString(File.separator))
}

fun uuidV3Of(): String = UUID.randomUUID().toString()

fun hexFromBytes(hex: String): ByteArray{
	require(hex.length % 2 == 0) { "Hex 字符串长度必须是偶数" }

	val bytes = ByteArray(hex.length / 2)
		var i = 0
		while (i < hex.length) {
			val byteStr = hex.substring(i, i + 2)
			bytes[i / 2] = byteStr.toInt(16).toByte()
			i += 2
		}
	return bytes
}

fun base64FromBytes(base64: String): ByteArray{
	return Base64.getDecoder().decode(base64)
}

fun Any?.out(){
	print(this)
}

fun Any?.outLine(){
	println(this)
}

fun inputStreamOf(file: String): InputStream {
	val files = file.split(":")

	if (files.size == 1) {
		return FileInputStream(file)
	}

	val type = files[0]
	val f = files[1]

	when(type){
		"file","f" -> {
			return FileInputStream(f)
		}
		"classpath","cp" -> {
			val url = getResource(f) ?: throw FileNotFoundException("file not found: $file")
			return url.openStream()
		}
		else -> {
			return FileInputStream(f)
		}
	}
}

fun readerOf(file: String): Reader{
	return inputStreamOf(file).reader()
}

fun getResource(path: String): URL?{
	return contextClassLoader.getResource(path)
}
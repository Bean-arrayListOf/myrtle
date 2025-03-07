package ice.myrtle.citrus.jna

import com.sun.jna.Library
import com.sun.jna.Native

interface Libc : Library {
	companion object{
		@JvmStatic
		val INSTANCE = Native.load("c", Libc::class.java)
	}
	fun system(command: String): Int
}
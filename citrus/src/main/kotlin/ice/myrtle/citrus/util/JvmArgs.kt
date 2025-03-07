package ice.myrtle.citrus.util

import java.util.*


open class JvmArgs {
	private val args: HashMap<String, String> = hashMapOf()

	companion object {
		@JvmStatic
		val local = JvmArgs(System.getProperties())
		@JvmStatic
		fun now(properties: Properties): JvmArgs {
			return JvmArgs(properties)
		}
	}

	constructor(properties: Properties) {
		properties.forEach { t, u ->
			args[t.toString()] = u.toString()
		}
	}

	fun getBoolean(key: Any, default: Boolean): Boolean {
		return (args[key] ?: return default).toBoolean()
	}

	fun getInt(key: Any, default: Int): Int {
		return (args[key] ?: return default).toInt()
	}

	fun getLong(key: Any, default: Long): Long {
		return (args[key] ?: return default).toLong()
	}

	fun getFloat(key: Any, default: Float): Float {
		return (args[key] ?: return default).toFloat()
	}

	fun getDouble(key: String, default: Double): Double {
		return (args[key] ?: return default).toDouble()
	}

	fun getString(key: String, default: String): String {
		return (args[key] ?: return default)
	}


}
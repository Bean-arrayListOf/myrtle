package ice.myrtle.message

import com.google.gson.GsonBuilder
import ice.myrtle.citrus.util.readerOf
import kotlin.random.Random

object Master {
	@JvmStatic
	fun main(vararg args: String) {
		val Gson = GsonBuilder().create()
		val list = Gson.fromJson(readerOf(args[0]), List::class.java) as List<String>

		println("\u001B[1;31mERROR:\u001B[0m "+list[Random.nextInt(list.size-1)])

	}
}
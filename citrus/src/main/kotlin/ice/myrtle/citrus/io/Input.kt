package ice.myrtle.citrus.io

import java.io.Serializable
import java.util.*

object Input {
	private val input = Scanner(System.`in`)

	data class UserBooleanElement(
		val title: String,
		val dtitle: String,
		val help: String,
		val trues: List<String>,
		val falses: List<String>,
		val yc: List<String>
	) : Serializable

	val defaultUBE = UserBooleanElement("[y/n]:",":","请输入[y/n]",listOf("y","yes","true"),listOf("n","no","false"),listOf())

	@JvmStatic
	fun nextStringLine(title: String?): String {
		if (title != null) {
			print(title)
		}
		return input.nextLine()
	}

	@JvmStatic
	fun nextStringLine(): String{
		return nextStringLine(null)
	}

	@JvmStatic
	fun nextString(title: String?): String{
		if (title != null) {
			print(title)
		}
		return input.next()
	}

	@JvmStatic
	fun nextString(): String{
		return nextString(null)
	}

	@JvmStatic
	fun nextInt(title: String?): Int{
		if (title != null) {
			print(title)
		}
		return input.nextInt()
	}

	@JvmStatic
	fun nextInt(): Int{
		return nextInt(null)
	}

	@JvmStatic
	fun nextDouble(title: String?): Double{
		if (title != null) {
			print(title)
		}
		return input.nextDouble()
	}

	@JvmStatic
	fun nextDouble(): Double{
		return nextDouble(null)
	}

	@JvmStatic
	fun nextFloat(title: String?): Float{
		if (title != null) {
			print(title)
		}
		return input.nextFloat()
	}

	@JvmStatic
	fun nextFloat(): Float{
		return nextFloat(null)
	}

	@JvmStatic
	fun nextLong(title: String?): Long{
		if (title != null) {
			print(title)
		}
		return input.nextLong()
	}

	@JvmStatic
	fun nextLong(): Long{
		return nextLong(null)
	}

	@JvmStatic
	fun nextByte(title: String?): Byte{
		if (title != null) {
			print(title)
		}
		return input.nextByte()
	}

	@JvmStatic
	fun nextByte(): Byte{
		return nextByte(null)
	}

	@JvmStatic
	fun nextBoolean(title: String?): Boolean{
		if (title != null) {
			print(title)
		}
		return input.nextBoolean()
	}

	@JvmStatic
	fun nextBoolean(): Boolean{
		return nextBoolean(null)
	}

	@JvmStatic
	fun nextChar(title: String?): Char{
		if (title != null) {
			print(title)
		}
		return input.next().single()
	}

	@JvmStatic
	fun nextChar(): Char{
		return nextChar(null)
	}

	@JvmStatic
	fun nextShort(title: String?): Short{
		if (title != null) {
			print(title)
		}
		return input.nextShort()
	}

	@JvmStatic
	fun nextShort(): Short{
		return nextShort(null)
	}

	@JvmStatic
	fun nextUserBoolean(ube: UserBooleanElement): Int{
		var i = 1
		while (true) {
			if (i!=1) {
				if ((i%5)==0) {
					println("[重复次数:$i]${ube.help}")
				}
				print(ube.dtitle)
			}else {
				print(ube.title)
			}
			val s = input.nextLine()
			if (ube.trues.contains(s)) {
				return 1
			}
			if (ube.falses.contains(s)) {
				return 0
			}
			if (ube.yc.contains(s)) {
				return -1
			}
			i++
		}
	}
}
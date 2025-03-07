package ice.myrtle.core.lang

import com.google.gson.Gson
import java.io.Serializable

/**
 * Bytes序列化Hex
 */
class BaseCore {

	/**
	 * 字符替换表数据类。
	 *
	 * 该类用于存储字符替换关系和分隔字符，以便在处理字符串时进行特定的替换操作。
	 * 主要应用于需要根据预定义规则转换字符串的场景，例如加密、解密或者编码、解码。
	 *
	 * @param replaceChars 字符替换映射表，存储了需要进行替换的字符对及其对应关系。
	 *                     初始为空映射，可通过后续操作添加替换规则。
	 * @param separateChar 分隔字符，用于在处理字符串时作为特定的分隔符号。
	 *                     该字符本身不参与替换操作，但可能影响字符串的处理方式。
	 */
	data class CharTable(
		val replaceChars: HashMap<Char, Char> = hashMapOf(),
		val separateChar: Char
	) : Serializable

	companion object {
		val defaultCharTable: CharTable = initDefaultCharTable()
		val hobbyCharTable: CharTable = initHobbyCharTable()

		@JvmStatic
		fun now(replaceChars: HashMap<Char, Char>, separateChar: Char): CharTable {
			return CharTable(replaceChars, separateChar)
		}

		fun initDefaultCharTable(): CharTable {
			val defaultCharTableMap = hashMapOf<Char, Char>()
			defaultCharTableMap['1'] = 'A'
			defaultCharTableMap['2'] = 'B'
			defaultCharTableMap['3'] = 'C'
			defaultCharTableMap['4'] = 'D'
			defaultCharTableMap['5'] = 'E'
			defaultCharTableMap['6'] = 'F'
			defaultCharTableMap['7'] = 'G'
			defaultCharTableMap['8'] = 'H'
			defaultCharTableMap['9'] = 'I'
			defaultCharTableMap['0'] = 'J'
			defaultCharTableMap['-'] = 'K'
			return CharTable(defaultCharTableMap, 'M')
		}

		fun initHobbyCharTable(): CharTable {
			val hobbyCharTableMap = hashMapOf<Char, Char>()
			hobbyCharTableMap['1'] = '□'
			hobbyCharTableMap['2'] = '■'
			hobbyCharTableMap['3'] = '○'
			hobbyCharTableMap['4'] = '●'
			hobbyCharTableMap['5'] = '△'
			hobbyCharTableMap['6'] = '▽'
			hobbyCharTableMap['7'] = '⬢'
			hobbyCharTableMap['8'] = '⧉'
			hobbyCharTableMap['9'] = '♦'
			hobbyCharTableMap['0'] = '⬚'
			hobbyCharTableMap['-'] = '→'
			return CharTable(hobbyCharTableMap, '↔')
		}
	}

	class EncBean() {
		var head: Boolean = false
		var charTable: CharTable = defaultCharTable
		var bytes: ByteArray?
			get() = field ?: throw NullPointerException("bytes is null")

		init {
			bytes = null
		}
	}

	fun enc(block: (EncBean) -> Unit): String {
		val bean = EncBean()
		block(bean)
		return enc(bean.head, bean.charTable, bean.bytes!!)
	}

	fun enc(charTable: CharTable, bytes: ByteArray): String {
		return enc(true, charTable, bytes)
	}

	/**
	 * 对字节数据进行编码。
	 *
	 * 此函数接受一个布尔值指示是否在结果前添加字符表的JSON表示，一个字符表用于替换特定字符，以及一个字节数组作为待编码的数据。
	 * 它首先将字节数组转换为字符串，然后根据字符表替换特定字符，最后根据需要添加字符表的JSON表示并返回编码后的字符串。
	 *
	 * @param head 指示是否在结果前添加字符表的JSON表示的布尔值。
	 * @param charTable 用于字符替换的字符表。
	 * @param bytes 待编码的字节数组。
	 * @return 编码后的字符串。
	 */
	fun enc(head: Boolean, charTable: CharTable, bytes: ByteArray): String {
		// 将字节数组转换为字符串，字符之间用$分隔
		val str = bytes.joinToString("$")
		// 初始化列表为转换后的字符串
		var list = str
		// 遍历字符表中的替换字符，进行替换
		for (c in charTable.replaceChars) {
			list = list.replace(c.key, c.value)
		}
		// 将$替换为字符表中的分隔字符
		list = list.replace('$', charTable.separateChar)
		// 如果头部标志为真，添加字符表的JSON表示到结果中
		if (head) {
			val json = Gson().toJson(charTable)
			return "${json}$:${list}"
		}
		// 如果头部标志为假，直接返回替换后的字符串
		return list
	}

	fun enc(bytes: ByteArray): String {
		return enc(true, defaultCharTable, bytes)
	}

	/**
	 * 解码函数，使用给定的字符表和编码字符串解码。
	 *
	 * @param coreAscii 编码字符串，格式为"字符表标识符:编码字符串"。
	 * @return 解码后的字节数组。
	 */
	fun dec(coreAscii: String): ByteArray {
		// 将核心ASCII字符串按":"分割，以获取字符表标识符和编码字符串
		val temp = coreAscii.split("$:")
		// 使用Gson从字符表标识符部分反序列化为CharTable对象
		val charTable = Gson().fromJson(temp[0], CharTable::class.java)
		// 使用字符表和编码字符串进行解码，并返回解码后的字节数组
		return dec(charTable, temp[1])
	}

	fun dec(charTable: CharTable, coreAscii: String): ByteArray {
		var str = coreAscii
		for (c in charTable.replaceChars) {
			str = str.replace(c.value, c.key)
		}
		val split = str.split(charTable.separateChar)
		val ba = ByteArray(split.size)
		for (c in split.indices) {
			ba[c] = split[c].toInt().toByte()
		}
		return ba
	}
}
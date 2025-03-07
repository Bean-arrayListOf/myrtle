package ice.myrtle.core.lang

import java.sql.ResultSet

/**
 * IResultSet类实现了Iterator<List<Any>>、Closeable和Myrtle接口，
 * 用于封装数据库的ResultSet对象，提供迭代访问数据库查询结果的功能。
 *
 * @param rs 被封装的ResultSet对象，用于访问数据库查询结果。
 */
class IResultSet(private val rs: ResultSet) : Iterator<List<Any>>, AutoCloseable {

	companion object {
		@JvmStatic
		fun now(rs: ResultSet): IResultSet {
			return IResultSet(rs)
		}
	}

	/**
	 * 判断是否还有下一个结果集。
	 *
	 * @return 如果还有下一个结果集，返回true；否则返回false。
	 */
	override fun hasNext(): Boolean {
		return rs.next()
	}

	/**
	 * 获取下一个结果集的行。
	 *
	 * @return 返回包含当前行所有列值的列表。
	 */
	override fun next(): List<Any> {
		return arrayListOf<Any>().let { list ->
			// 循环获取当前行所有列的值，并添加到列表中。
			for (i in 0 until rs.metaData.columnCount) {
				list.add(rs.getObject((i + 1)))
			}
			list
		}
	}

	/**
	 * 关闭当前的结果集。
	 */
	override fun close() {
		rs.close()
	}
}

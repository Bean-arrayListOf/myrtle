package ice.myrtle.citrus.db.pojo

import java.io.Serializable

data class MS_INDEX(
	val aIndex: Long,
	val aDB: String,
	val aKey: String,
	val bDataTable: String,
	val bDataType: Int,
	val aTimestamp: Long
) : Serializable

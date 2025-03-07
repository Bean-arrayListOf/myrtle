package ice.myrtle.citrus.db.pojo

enum class MapDBType(public val index: Int) {
	T_ByteArray(1),
	T_String(2),
	T_Long(3),
	T_Int(4),
	T_Short(5),
	T_Byte(6),
	T_Char(7),
	T_Float(8),
	T_Double(9),
	T_Boolean(10),
	T_List(11)

}
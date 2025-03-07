package ice.myrtle.citrus.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import ice.myrtle.citrus.db.pojo.MapDBType
import ice.myrtle.citrus.util.uuidV3Of
import java.io.File
import java.sql.Connection
import javax.sql.DataSource

class MapDB : AutoCloseable {
	private val connect: Connection
	private val defaultDB: String = "master"
	private val defaultXTable: String = "MDB_INDEX"
	private val defaultXDB: String = "MDB"
	constructor(dataSource: DataSource) {
		this.connect = dataSource.connection

		createIndex()
	}
	constructor(file: String){
		val hikariConfig = HikariConfig()
		hikariConfig.driverClassName = "org.h2.Driver"
		hikariConfig.jdbcUrl = "jdbc:h2:file:${File(file).absolutePath};MV_STORE=true"
		hikariConfig.username = "SA"
		hikariConfig.password = "7F8D2E9C-AB1F-4E3D-A6C2-B5D0F8B3E7A1"
		val dataSource = HikariDataSource(hikariConfig)
		this.connect = dataSource.connection

		createIndex()
	}

	private fun createIndex(){
		val sql = """
			create table if not exists "$defaultXTable"(
				"A_INDEX" long primary key auto_increment unique not null comment '抽象_索引',
				"A_DB" varchar(500) not null default 'master' comment '抽象_数据库',
				"A_KEY" varchar(500) not null comment '抽象_键',
				"B_DATA_TABLE" varchar(50) not null unique comment '实际_值表',
				"A_DATA_TYPE" int not null default '1' comment '实际_值类型',
				"A_TIMESTAMP" timestamp not null default current_timestamp comment '抽象_时间戳'
			);
		""".trimIndent()

		connect.prepareStatement(sql).use { ps ->
			ps.executeUpdate()
		}
	}

	private fun createDataTable(uuid: String){
		val sql = """
			create table "$uuid"(
				"X_INDEX" long primary key auto_increment unique not null comment '实际_索引',
				"X_DATA" bytea comment '实际_值'
			);
		""".trimIndent()

		connect.prepareStatement(sql).use { ps ->
			ps.executeUpdate()
		}
	}

	fun contains(db: String,key: String): Boolean {
		val sql = """
			select COUNT(*) from "$defaultXTable" where "A_DB"=? and "A_KEY"=?;
		""".trimIndent()
		return connect.prepareStatement(sql).use { ps ->
			ps.setString(1,db)
			ps.setString(2,key)
			ps.executeQuery().use { rs ->
				rs.next()
				rs.getInt(1) > 0
			}
		}
	}

	private fun update(db: String,key: String,value: List<ByteArray>){
		val sql = """
			select "B_DATA_TABLE" from "$defaultXTable" where "A_DB"=? and "A_KEY"=?;
		""".trimIndent()

		val uuid = connect.prepareStatement(sql).use { ps ->
			ps.setString(1,db)
			ps.setString(2,key)
			ps.executeQuery().use { rs ->
				rs.next()
				rs.getString(1)
			}
		}

		dropDataTable(uuid)

		insertDataType(uuid,value)
	}

	private fun insert(db: String,key: String,type: MapDBType,value: List<ByteArray>){
		val uuid = uuidV3Of()
		createDataTable(uuid)

		val sql = """
			insert into "$defaultXTable"(A_DB, A_KEY, B_DATA_TABLE, A_DATA_TYPE) values(?, ?, ?, ?);
		""".trimIndent()

		connect.prepareStatement(sql).use { ps ->
			ps.setString(1,db)
			ps.setString(2,key)
			ps.setString(3,uuid)
			ps.setInt(4,type.index)
			ps.executeUpdate()
		}

		insertDataType(uuid,value)
	}

	fun put(db: String,key: String,value: List<ByteArray>){
		if(contains(db,key)){
			update(db,key,value)
		}else{
			insert(db,key,MapDBType.T_ByteArray,value)
		}
	}

	fun get(db: String,key: String): List<ByteArray>{
		val sql = """
			select "A_DATA_TYPE" from "$defaultXTable" where "A_DB"=? and "A_KEY"=?;
		""".trimIndent()
		val uuid = connect.prepareStatement(sql).use { ps ->
			ps.setString(1,db)
			ps.setString(2,key)
			ps.executeQuery().use { rs ->
				rs.next()
				rs.getString(1)
			}
		}

		return selectDataTable(uuid)
	}

	private fun selectDataTable(uuid: String): List<ByteArray>{
		val sql = """
			select * from "$uuid";
		""".trimIndent()
		return connect.prepareStatement(sql).use { ps ->
			ps.executeQuery().use { rs ->
				val list = mutableListOf<ByteArray>()
				while (rs.next()) {
					list.add(rs.getBytes("X_DATA"))
				}
				list
			}
		}
	}

	private fun insertDataType(uuid: String,value: List<ByteArray>){
		val sql = """
			insert into "$uuid"("X_DATA") values(?);
		""".trimIndent()
		connect.prepareStatement(sql).use { ps ->
			value.forEach {
				ps.setBytes(1,it)
				ps.executeUpdate()
			}
		}
	}

	private fun dropDataTable(uuid: String){
		val sql = """
			drop table "$uuid";
		""".trimIndent()

		connect.prepareStatement(sql).use { ps ->
			ps.executeUpdate()
		}
	}

	override fun close() {
		if (connect.isClosed) {
			print(1)
			connect.close()
		}
	}
}
package ice.myrtle.citrus.util

import org.apache.commons.codec.digest.DigestUtils
import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.Serializer
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Paths

class Password : AutoCloseable {
	private val temp: File = Paths.get(System.getProperty("java.tmpdir"),uuidV3Of()+".map.db").toFile()
	private val db: DB

	constructor(){
		this.db = DBMaker.fileDB(temp).fileMmapEnable().make()
	}

	constructor(inputStream: InputStream){
		FileOutputStream(temp).use { fos ->
			inputStream.transferTo(fos)
		}
		this.db = DBMaker.fileDB(temp).fileMmapEnable().make()
	}

	fun put(group: String,userName: String,password: String){
		db.hashMap(group).keySerializer(Serializer.STRING).valueSerializer(Serializer.BYTE_ARRAY).createOrOpen().use { map ->
			if (!map.containsKey(userName)) {
				map[userName] = DigestUtils.sha512(password)
			}else{
				throw Exception("用户名重复")
			}
		}
	}

	fun verify(group: String,userName: String,password: String): Boolean{
		db.hashMap(group).keySerializer(Serializer.STRING).valueSerializer(Serializer.BYTE_ARRAY).createOrOpen().use { map ->
			return map[userName].contentEquals(DigestUtils.sha512(password))
		}
	}

	fun remove(group: String,userName: String){
		db.hashMap(group).keySerializer(Serializer.STRING).valueSerializer(Serializer.BYTE_ARRAY).createOrOpen().use { map ->
			map.remove(userName)
		}
	}

	fun update(group: String, userName: String,oldPassword: String,newPassword: String){
		if (verify(group,userName,oldPassword)) {
			db.hashMap(group).keySerializer(Serializer.STRING).valueSerializer(Serializer.BYTE_ARRAY).createOrOpen().use { map ->
				map[userName] = DigestUtils.sha512(newPassword)
			}
		}else{
			throw Exception("密码错误")
		}
	}

	fun save(file: File){
		FileOutputStream(file).use { fos ->
			save().use { fis ->
				fis.transferTo(fos)
			}
		}
	}

	fun save(): InputStream{
		return FileInputStream(temp)
	}

	override fun close() {
		if (!db.isClosed()) {
			db.close()
		}
	}
}
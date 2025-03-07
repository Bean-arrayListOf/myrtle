package ice.myrtle.citrus.io

import org.slf4j.LoggerFactory
import java.util.*

abstract class AutoClosed : AutoCloseable {
	protected var isClose = false
	protected constructor(){
		closed()
	}
	protected fun closed(){
		val t = Thread{
			run {
				val log = LoggerFactory.getLogger(this@run::class.java)
				try {
					if (!isClose) {
						//log.info("closed")
						close()
					}
				}catch (e: Throwable){
					log.error("auto closed error",e)
				}
			}
		}
		t.name = "AC-${this::class.java.typeName}-${UUID.randomUUID()}"
		Runtime.getRuntime().addShutdownHook(t)
	}
}
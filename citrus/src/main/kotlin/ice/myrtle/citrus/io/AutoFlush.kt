package ice.myrtle.citrus.io

import org.slf4j.LoggerFactory
import java.io.Flushable
import java.util.*

abstract class AutoFlush : Flushable {
	protected abstract val time: Long
	protected fun autoFlashKit(){
		val thread = Thread {
			run {
				val log = LoggerFactory.getLogger(this@run::class.java)
				while (true) {
					try {
						Thread.sleep(time)
						flush()
					} catch (e: Throwable) {
						log.error("auto flushd error",e)
					}
				}
			}
		}
		thread.name = "AF-${this::class.java.typeName}-${UUID.randomUUID()}"
		thread.isDaemon = true
		thread.start()
	}
}
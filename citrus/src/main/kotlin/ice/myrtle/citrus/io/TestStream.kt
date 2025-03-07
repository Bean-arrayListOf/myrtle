package ice.myrtle.citrus.io

class TestStream : AutoClosed {
	constructor():super()
	constructor(title: String){
		println(title)
		closed()
	}

	override fun close() {
		isClose = true
		println("close")
	}
}
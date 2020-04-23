package test

class TestTest extends TestX
{
	println("new " + getClass.getSimpleName)

	def test1 {
		println("test 1")
	}
	
	def test2 {
		println("test 2")
		assertEqualsV(1.1,1.2,.3)
	}
	
	def test3 {
		println(">>"+tolStats)
	}
}


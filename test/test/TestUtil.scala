package test

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic
import scala.math.abs
import m.Stats
import junit.framework.{Test, Assert => JAssert, TestCase}


//@Test
abstract class TestX extends TestCase
{
	val defTol = 0.0000000000001
	val tolStats = new Stats

		def assertTrue(b:Boolean)
		= JAssert.assertTrue(b)

	def assertTrue(msg:String, b:Boolean)
		= JAssert.assertTrue(msg, b)

	def assertFalse(b:Boolean)
	  = JAssert.assertFalse(b)

	def assertEquals(expected:Double, actual:Double, tol:Double=defTol)
		= JAssert.assertEquals(expected, actual, tol)

	def assertEquals(msg:String, expected:Double, actual:Double, tol:Double)
		= JAssert.assertEquals(msg, expected, actual, tol)

	def assertEquals(expected:Long, actual:Long)
		= JAssert.assertEquals(expected, actual)

	def assertEquals(expected:Int, actual:Int)
		= JAssert.assertEquals(expected, actual)

	def assertEquals(msg:String, expected:Long, actual:Long)
		= JAssert.assertEquals(msg, expected, actual)

	def assertEquals(expected:String, actual:String)
		= JAssert.assertEquals(expected, actual)

	def assertEquals(expected:AnyRef, actual:AnyRef)
		= JAssert.assertEquals(expected, actual)

	def assertNull(b:Any) = JAssert.assertNull(b)

	def fail(msg:String=null) = JAssert.fail(msg)

	def assertEqualsV(expected:Double, actual:Double, tol:Double) {
		val diff = abs(actual - expected)
		tolStats << diff
		printf("expected:%,f actual:%,f tol:"+tol+" diff:%,f%n", expected, actual, diff)
		Console.flush
		assertEquals(expected, actual, tol)
	}

	def assertEqualsS(expected:Any, actual:Any)
	  = assertEquals(expected.toString, actual.toString)
}


// allow assertions on spawned threads
// don't forget to call verify()
class AsyncTest(test:TestX, tests:Int, timeout:Int=5)
{
	private val fail  = new atomic.AtomicBoolean(false)
	private val latch = new CountDownLatch(tests)

	def assertEquals(expected:Any, actual:Any) {
		if (expected != actual) {
			Console.err.printf("%s != %s%n", expected.asInstanceOf[AnyRef], actual.asInstanceOf[AnyRef])
			fail.set(true)
		}

		if (latch.getCount == 0)
			throw new Exception("your tests are screwy")

		latch.countDown
	}

	def verify {
		var timeouts = timeout
		while(!latch.await(1, TimeUnit.SECONDS)) {
			println("Waiting for Async Test")
			timeouts -= 1
			if (timeouts == 0)
				test.fail("Timed out")
		}
		test.assertFalse(fail.get)
	}
}
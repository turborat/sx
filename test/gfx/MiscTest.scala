package gfx

import x.X._
import java.util.concurrent.ConcurrentLinkedQueue
import test.{AsyncTest, TestX}

class InvokeLaterTests extends SwingHelperTestSuite("AWT-EventQueue-")
{
	def fun(f: => Unit) = SwingHelper.invokeLater(f)
}

class InvokeAsyncTests extends SwingHelperTestSuite("SwingWorker-", orderCounts=false)
{
	def fun(f: => Unit) = SwingHelper.invokeAsync(f)
}

abstract class SwingHelperTestSuite(threadPrefix:String, orderCounts:Boolean=true) extends TestX
{
	def fun(f: => Unit)

	def testThreads {
		var actualThread:String = null

		val tests = 1000
		val asyncTest = new AsyncTest(this, tests)

		assertTrue(Thread.currentThread.getName.startsWith("main"))

		loop(1000) {
			fun {
				actualThread = Thread.currentThread.getName
				asyncTest.assertEquals(true, actualThread.startsWith(threadPrefix))
			}
		}

		asyncTest.verify
	}

	def testOrder {
		val tests = 1000
		val q = new ConcurrentLinkedQueue[Int]
		val asyncTest = new AsyncTest(this, tests)

		for (n <- 1 to tests) {
			q.add(n)
			fun {
				val m = q.remove()
				if (orderCounts)
					asyncTest.assertEquals(n,m)
				else
					asyncTest.assertEquals(1,1) // just countdown
			}
		}

		asyncTest.verify

		assertTrue(q.isEmpty())
	}
}

package x

import test.TestX
import x.X._
import scala.collection._
import m.RNG
import x.ScatterGather._
import java.util.concurrent.atomic.AtomicInteger

class ScatterGatherTest extends TestX
{
	def test1 {
		val sg = new ScatterGather[Thread]
		val sleep = 200

		loop (6) {
			sg.submit {
				Thread.sleep(sleep)
				Thread.currentThread
			}
		}

		var out:List[Thread] = null
		val millis = timeit {
	  	out = sg.await
		} / 1000 / 1000

		val set = mutable.Set[Thread]()
		for (thread <- out) {
			if (set.contains(thread))
				fail()
			set += thread
		}

		assertEquals(6, set.size)
		printf("millis:%,d sleep:%,d%n", millis, sleep)
		assertTrue(millis >= sleep)
		assertTrue(millis < sleep * 1.5)
	}

	def testOrdering {
		val sg = new ScatterGather[Int]

		for (n <- 0 to 200)
			sg.submit {
				Thread.sleep(RNG.irand(100))
				n
			}

		var n0 = 0
		for (n <- sg.await) {
			assertEquals(n0, n)
			n0 += 1
		}
	}

	def testStripe {
		val nums = makeList(new Range(0, 15, 1)) { a => a }
		val out = ScatterGather.stripe(nums, 3) {
			n => (n, Thread.currentThread.getId)
		}
		out foreach println
		// doesn't test anything ....
	}

	def testStripes {
		assertEqualsS("List((0,5), (5,10), (10,15))", ScatterGather.stripes(15,3))
		assertEqualsS("List((0,8), (8,15))", ScatterGather.stripes(15,2))
		assertEqualsS("List((0,1), (1,2), (2,3))", ScatterGather.stripes(3,5))
		assertEqualsS("List((0,3))", ScatterGather.stripes(3,1))
		assertEqualsS("List((0,1))", ScatterGather.stripes(1,100))
		assertEqualsS("List((0,1), (1,2))", ScatterGather.stripes(2,100))
		assertEqualsS("List()", ScatterGather.stripes(0,100))
	}

  def testStripeOrdering {
		val nums = makeList(new Range(0, 12, 1)) { a => a }
		val out = ScatterGather.stripe(nums, 3) { n => n }
		var n = 0
		for (o <- out) {
			assertEquals(n, o)
			n += 1
		}
	}

	def testStripeThreads {
		class TX {
			var thread:Thread = null
		}

		val strips = 16
		val tm = new mutable.HashMap[Int,TX]
		val nums:List[Int] = makeList(new Range(0,100,1)) { n => n }
		var n0 = 0

		for (range <- stripes(nums.size, strips)) {
			val tx = new TX
			for (n <- range._1 until range._2)
				tm(n) = tx
		}

		assertEquals(nums.size, tm.size) // 2 b safe

		stripe(nums, strips) {
			n =>
		  synchronized (tm) {
				val tx = tm(n)
				if (tx.thread == null) {
					tx.thread = Thread.currentThread
				}
				else
					assertEquals(tx.thread, Thread.currentThread)
				0
			}
		}
	}

	def testStripeResults {
		loop(100) {
			val len = RNG.irand(100).toInt
			val l1 = makeList(0 to len) { x => RNG.irand(0,100) }
			val strips = RNG.irand(1,100).toInt

			assertTrue(l1.size > 0)

			val l2 = stripe(l1, strips=strips) { x => x }
			assertEqualsS(l1,l2)
		}
	}

	def testSloop {
		val n = new AtomicInteger
		sloop(1000, strips=100) {
			n.incrementAndGet
		}
		assertEquals(1000,n.get)
	}

	def XtestSloopRet {
		val l = sloop(6, strips=3) {
			33
		}
		assertEqualsS(List(33,33,33,33,33,33),l)
	}

	def testSloopThreads {
			val threads = new mutable.HashSet[Thread]
			sloop(loops=40,strips=4) {
				synchronized {
					threads += Thread.currentThread
				}
			}
			assertEquals(4, threads.size)
	}

	def testSloopNoInterleaving {
		var lastThread:String = null
		val inARow = new AutoMap[String,Int] {
			def create(k:String) = 1
		}

		sloop(loops=100,strips=1) {
			val thisThread = Thread.currentThread.getName
			synchronized {
				if (lastThread == thisThread) {
					val n = inARow(thisThread)
					inARow += (thisThread, n + 1)
				}
				else
					lastThread = thisThread
			}
		}

		println(inARow)
		assertEquals(1, inARow.size)
		assertEquals(100, inARow.min._2)
	}

	def testSloopInterleaving {
		var lastThread:String = null
		val inARow = new AutoMap[String,Int] {
			def create(k:String) = 1
		}

		sloop(loops=200,strips=4) {
			val thisThread = Thread.currentThread.getName
			synchronized {
				if (lastThread == thisThread) {
					val n = inARow(thisThread)
					inARow += (thisThread, n + 1)
				}
				else
					lastThread = thisThread
			}
		}

		import scala.collection.JavaConversions._
		// if no interleaving then we will get 200/4 in a row
		inARow.values foreach { n => assertTrue(n <= 50) }
		println(inARow)
	}
}


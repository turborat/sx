package x

import x.X._
import test.TestX
import collection.mutable
import java.io.File
import m.{BoxMullerGaussian, RNGS, RNG}
import java.util.concurrent.{TimeUnit, CountDownLatch}

class XTest extends TestX
{
  def testTimeIt() {
    val time = timeit {
      println("blah")
    }
		printf("%,d%n", time)
		assertTrue(time > 0)
  }

	def testPTime {
		val x = ptime("blah: ") { 2 * 2 }
		assertEquals(4, x)
	}

	def testPrettyTime {
		assertEquals("nothing", prettyTime(0))
		assertEquals("1.0 ns", prettyTime(1))
		assertEquals("12 ns", prettyTime(12))
		assertEquals("123 ns", prettyTime(123))
		assertEquals("1.2 us", prettyTime(1234))
		assertEquals("12 us", prettyTime(12345))
		assertEquals("123 us", prettyTime(123456))
		assertEquals("1.2 ms", prettyTime(1234567))
		assertEquals("12 ms", prettyTime(12345678))
		assertEquals("123 ms", prettyTime(123456789))
		assertEquals("1.2 s", prettyTime(1234567891))
		assertEquals("12 s", prettyTime(12345678901l))
	}

	def testNof {
		var n = 0
		assertEquals("List(1, 2, 3, 4, 5)", nof(5) { n += 1 ; n }.toString)
		assertEquals("List(4, 3, 2)", nof(3) { n -= 1 ; n }.toString)
		assertEquals("List(1, 0, -1)", nof(3) { n -= 1 ; n }.toString)
	}

	def testLoop {
		var n = 0
		loop (0) { n += 1 }
		assertEquals(0,n)
		loop (3) { n += 1 }
		assertEquals(3,n)
	}

	def testMakeMap1 {
		val arr = List(1,2,3)
		val map = makeMap(arr) { i => (i+"",i*2) }
		val i = map("1")

		assertEquals(3, map.size)
		assertEquals(2, map("1"))
		assertEquals(4, map("2"))
		assertEquals(6, map("3"))

		map("1") *= 3
    map("2") -= 2
		map -= "3"

		assertEquals(2, map.size)
		assertEquals(6, map("1"))
		assertEquals(2, map("2"))
	}

	def testMakeMap2 {

		class Dummy(s:String)
		{
			def bePround = s + " is dumb"
		}

		val arr = List("1", "2", "3")
		val map = makeMap (arr) { s => (Integer.parseInt(s), new Dummy(s)) }

		map(1).bePround

		println(map)
	}

	def testMakeList1 {
		val iterable:Iterable[Int] = List(1,2,3)
		val list = makeList(iterable) { _.toString }
		assertEquals(3, list.length)
		assertEquals("1", list(0))
		assertEquals("2", list(1))
		assertEquals("3", list(2))
	}

	def testMakeList2 {
		val iterable:Iterator[Int] = List(1,2,3).iterator
		val list = makeList(iterable) { _ * 2 }
		assertEquals(3, list.length)
		assertEquals(2, list(0))
		assertEquals(4, list(1))
		assertEquals(6, list(2))
	}

//	def testMakeListNulls {
//		val list = makeList[Int,Int](List(1,2,3,4)) { n => if (n < 2) n else null }
//		assertEquals(2, list.length)
//		assertEquals(1, list(0))
//		assertEquals(2, list(1))
//	}


	def testJoin {
		assertEquals("", join(List()))
		assertEquals("1 2 3", join(List(1,2,3)))
		assertEquals("1-2-3", join(List(1,2,3), delim="-"))
		assertEquals("123", join(List(1,2,3), delim=""))
	}

	def testSlurpSpew {
		val list1 = {
			var bldr = new mutable.ListBuffer[Any]
			val rng = RNG()
			loop(20) {
				bldr += rng.srand(32)
			}
			bldr.toList
		}

		val file = new File("test.file")
		if (file.exists)
			file.delete

		spew(file, list1)

		val list2 = slurp(file)

		assertEquals(20, list1.length)
		assertEquals(20, list2.length)

		for (i <- 0 until 20)
			assertEquals(list1(i).toString, list2(i))

		file.delete
		assertFalse(file.exists)
	}

	def testToList {
		assertEqualsS("List(1, 2, 3)", toList(Array(1,2,3)))
	}

	def testOnOf {
		assertFalse(oneOf(1))
		assertTrue(oneOf(1,1))
		assertTrue(oneOf(1,2,1))
		assertFalse(oneOf(0,2,1))
		assertFalse(oneOf("blah","fart"))
		assertTrue(oneOf("blah","fart","blah"))
	}

	class MyClassOuter

	def testObjName {
		class MyClassInner
		assertEquals("MyClassInner$1", objName(new MyClassInner))
		assertEquals("MyClassOuter", objName(new MyClassOuter))
		assertEquals("MyClassTop", objName(new MyClassTop))

		class RNGSBoxMuller extends RNGS(1) with BoxMullerGaussian
		assertEquals("RNGSBoxMuller$1", objName(new RNGSBoxMuller))
		assertEquals("RNGS", objName(new RNGS(1) with BoxMullerGaussian))
	}

	def testFmtCH3 {
		assertEquals("12", fmtCH3(12d))
		assertEquals("12.333", fmtCH3(12.333d))
		assertEquals("12.333", fmtCH3(37/3d))
		assertEquals("1'200", fmtCH3(1200d))
		assertEquals("1'200.444", fmtCH3(1200.44444d))
	}

	def testQuestionMark {
		assertEquals(1, ?(true,1,0))
		assertEquals(1, ?(false,0,1))
	}

  def testThreadException {
    var n = 0
    val latch = new CountDownLatch(1)

    thread(interval=10) {
      n += 1
      if (n == 1)
        throw new NullPointerException
      else
        latch.countDown
    }

    latch.await(200, TimeUnit.MILLISECONDS)
    print(n)
    assertTrue(n > 1)
  }

	def testSchedule {
		val threadName = new WaitFor[String]
		X.schedule(1000) {
			println("hello from " + Thread.currentThread)
			threadName.set(Thread.currentThread.getName)
		}
		assertEquals("BGTHREAD", threadName.get(2000, TimeUnit.MILLISECONDS))
	}
}

class MyClassTop

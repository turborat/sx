package x

import test.TestX
import scala.collection.mutable._
import x.X._
import m.RNG

class NKeyTest extends TestX
{
	def test1 {
		val key = new NKey("one", "two", 1)
		assertEqualsS("[one, two, 1]", key.toString)
		assertEquals(key, new NKey("one", "two", 1))
		assertFalse(key == new NKey("one", "two, 2"))
		assertFalse(key.hashCode == new NKey("one", "two, 2").hashCode)
		assertTrue(key.hashCode == new NKey("one", "two", 1).hashCode)
	}

	def test2 {
		val m = new java.util.HashMap[NKey,Int]
		val key = new NKey("abc", 123)

		m.put(key, 1212)

		assertEquals(1212, m.get(key))
		assertEquals(1212, m.get(new NKey("abc", 123)))
	}

//	def test3 {
//		val map = new MapX[NKey,Int]
//		map += (new NKey(1,2,3), 123)
//		map(new NKey(1,2,3))
//	}
//
//	def testX {
//		val map = new MapX[(Int,Int),Int]
//		map += ((1,3), 123)
//		map(1,3)
//	}
}


object NKeySpeedathlon
{
	def main(args:Array[String])
	{
		loop(10) {
			go
			println
		}
	}

	def go {
		val n = 1000*100
		val m1 = new HashMap[(Int,Int),Int]
		val m2 = new HashMap[NKey,Int]
		val keys = new ListBuffer[(Int,Int)]

		loop (n) {
			val v1, v2 = RNG.irand(10000).toInt
			m1((v1,v2)) = 123
			m2(new NKey(v1,v2)) = 123
			keys += ((v1,v2))
		}

		val sa = new SpeedathlonX
		sa.contend("tuple.hits") {
			for ((v1,v2) <- keys)
				m1((v1,v2))
		}

		sa.contend("nkey.hits") {
			for ((v1,v2) <- keys)
				m2(new NKey(v1,v2))
		}

		sa.compete
	}
}
package x

import test.TestX

class MapXTest extends TestX
{
	def test1 {
		val m = new MapX[String,Int]
		assertEquals(0,m.size)
		assertEqualsS("[]",m)

		m += ("one", 1)
		m += ("two", 2)
		assertEqualsS("[one:1 two:2]",m)
		assertEquals(2, m.size)
	}

	def testIt {
		val m = new MapX[String,Int]
		m += ("z", 1)
		m += ("y", 5)
		m += ("a", 3)
		m += ("d", 7)

		val it = m.iterator
		assertTrue(it.hasNext)
		assertEqualsS("a:3", it.next)
		assertTrue(it.hasNext)
		assertEqualsS("d:7", it.next)
		assertTrue(it.hasNext)
		assertEqualsS("y:5", it.next)
		assertTrue(it.hasNext)
		assertEqualsS("z:1", it.next)
		assertFalse(it.hasNext)
	}

	def testFloorCeil {
		val m = new MapX[String,Int]
		m += ("z", 1)
		m += ("y", 5)
		m += ("a", 3)
		m += ("d", 7)

		assertEqualsS("a:3", m.floorEntry("b"))
		assertEqualsS("a:3", m.floorEntry("c"))
		assertEqualsS("d:7", m.floorEntry("d"))

		assertEqualsS("y:5", m.ceilEntry("w"))
		assertEqualsS("y:5", m.ceilEntry("x"))
		assertEqualsS("y:5", m.ceilEntry("y"))
		assertEqualsS("z:1", m.ceilEntry("z"))
	}

	def testIteration {
		val map = new MapX[Double,String]
		map += (2, "2")
		map += (22, "22")
		map += (13, "13")

		var sum = 0d
		var str = ""
		for ((k,v) <- map) {
			println(k,v)
			sum += k
			str += v
		}

		assertEquals(37, sum)
		assertEqualsS(21322, str)
	}

	def testNull {
		val map = new MapX[Double,String]

		try {
			map(121)
			fail()
		}
		catch {
			case e:Disaster => println(e)
			case _ => fail()
		}

		assertFalse(map.contains(121))
		map += (121, null)

		assertTrue(map.contains(121))

		try {
			map(121)
			fail()
		}
		catch {
			case e:Disaster => println(e)
			case _ => fail()
		}

		// so...... you can put nulls in but never get them out. BEWARE !
	}

	def testNull2 {
		val map = new MapX[Double,String](true)
		val s:String = map(121)
		assertNull(s)
		assertNull(map(121))
		map += (121,"blah")
		assertEquals("blah", map(121))
	}
	
	def testTupleKey {
		val map = new scala.collection.mutable.HashMap[(Int,String),String]
		assertFalse(map.contains((1,"one")))
		map((1,"one")) = "ONE"
		assertTrue(map.contains((1,"one")))
		assertEquals("ONE", map((1,"one")))
	}
}

class AutoMapTest extends TestX
{
	def test1 {
		val m = new AutoMap[String,Int] {
			def create(k:String) = 1
		}
		assertFalse(m.contains("one"))
		assertEquals(0,m.size)
		assertEquals(1, m("one"))
		assertTrue(m.contains("one"))
		assertEquals(1,m.size)
	}

	def test2 {
		val m = new AutoMap[Double,Double] {
			def create(k:Double) = k * 2
		}
		assertFalse(m.contains(1))
		assertEquals(0,m.size)
		assertEquals(2, m(1))
		assertTrue(m.contains(1))
		assertEquals(1,m.size)
	}

	def test3 {
		var c = 0
		val m = new AutoMap[Double,Double] {
			def create(k:Double):Double = {
				c += 1
				k
			}
		}
		assertEquals(0,c)
		m(1)
		m(1)
		m(1)
		m(1)
		assertEquals(1,c)
		m(2)
		m(2)
		m(2)
		m(2)
		assertEquals(2,c)
	}

	def testRemove {
		val m = new MapX[String,String]
		m("abc") = "123"
		assertTrue(m.contains("abc"))
		assertNull(m -= "ABC")
		assertEquals("123", m -= "abc")
		assertFalse(m.contains("abc"))
	}
}
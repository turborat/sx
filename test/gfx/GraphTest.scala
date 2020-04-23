package gfx

import scala.math._
import test.TestX


class PointsTest extends TestX
{
	def testAdd() {
		val points = new Points
		assertTrue(points.x0.isNaN)
		assertTrue(points.xN.isNaN)
		assertTrue(points.y0.isNaN)
		assertTrue(points.yN.isNaN)

		points.+=(1,2)

		assertEquals(1, points.x0)
		assertEquals(1, points.xN)
		assertEquals(2, points.y0)
		assertEquals(2, points.yN)

		points.+=(3,4)

		assertEquals(1, points.x0)
		assertEquals(3, points.xN)
		assertEquals(2, points.y0)
		assertEquals(4, points.yN)

		points.+=(-3,-4)

		assertEquals(-3, points.x0)
		assertEquals(3, points.xN)
		assertEquals(-4, points.y0)
		assertEquals(4, points.yN)
	}

	def testToStringComparator() {
		val points = new Points
		assertEquals("{}", points.toString)

		points.+=(1,2)
		assertEquals("{(1.0,2.0)}", points.toString)

		points.+=(0,1)
		assertEquals("{(0.0,1.0), (1.0,2.0)}", points.toString)

		points.+=(3,4)
		assertEquals("{(0.0,1.0), (1.0,2.0), (3.0,4.0)}", points.toString)
	}

	def testIterator() {
		val points = new Points
		assertFalse(points.iterator.hasNext)

		points.+=(1,2)
		points.+=(0,1)
		points.+=(3,4)

		val it = points.iterator

		assertTrue(it.hasNext)
		assertEquals("(0.0,1.0)", it.next.toString)

		assertTrue(it.hasNext)
		assertEquals("(1.0,2.0)", it.next.toString)

		assertTrue(it.hasNext)
		assertEquals("(3.0,4.0)", it.next.toString)

		assertFalse(it.hasNext)
	}
}


class FunctionTest extends TestX
{
	def test1() {
		val fun = new Function(0,Pi,sin)
		assertEquals(0, fun.x0, .0000001)
		assertEquals(Pi, fun.xN, .0000001)
		assertTrue(fun.y0.isNaN)
		assertTrue(fun.yN.isNaN)

		fun.xSteps(1000)
		assertEquals(0, fun.x0, .0000001)
		assertEquals(Pi, fun.xN, .0000001)
		assertEquals(0, fun.y0, .0000001)
		assertEquals(1, fun.yN, .0000001)
	}

	def test2() {
		val fun = new Function(0,2*Pi,sin)
		assertEquals(0, fun.x0, .0000001)
		assertEquals(2*Pi, fun.xN, .0000001)
		assertTrue(fun.y0.isNaN)
		assertTrue(fun.yN.isNaN)

		fun.xSteps(1000)
		assertEquals(0, fun.x0, .0000001)
		assertEquals(2*Pi, fun.xN, .0000001)
		assertEquals(-1, fun.y0, .0000001)
		assertEquals(1, fun.yN, .0000001)
	}

	def testIterator() {
		val fun = new Function(-2, 3, (x:Double) => 2*x)
		fun.xSteps(5)
		val it = fun.iterator
		assertTrue(it.hasNext)
		assertEquals("(-2.0,-4.0)", it.next.toString)
		assertTrue(it.hasNext)
		assertEquals("(-1.0,-2.0)", it.next.toString)
		assertTrue(it.hasNext)
		assertEquals("(0.0,0.0)", it.next.toString)
		assertTrue(it.hasNext)
		assertEquals("(1.0,2.0)", it.next.toString)
		assertTrue(it.hasNext)
		assertEquals("(2.0,4.0)", it.next.toString)
		assertTrue(it.hasNext)
		assertEquals("(3.0,6.0)", it.next.toString)
		assertFalse(it.hasNext)
	}
}
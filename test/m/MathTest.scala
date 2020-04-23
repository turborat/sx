package m

import m.Math._
import test.TestX
import scala.math._
import x.X._

class MathTest extends TestX
{
	def testSum1() {
		val list = List(2d, 4d, 8d)

		def f[T](i:T) :Double = {
			println("got " + i)
			1d
		}

		var sum = Math.sum(list, f)
		assertEquals(3, sum, .000000000001)
	}

	def testSum2() {
		val sum = Math.sum(List(2d, 4d, 8d), (i:Double) => i )
		assertEquals(14, sum, .000000000001)
	}

  def testSum3() {
		val sum = Math.sum(List(2d, 4d, 8d))
		assertEquals(14, sum, .000000000001)
	}

	def testAvg() {
		assertEquals(3, avg(List(1d,3d,5d)), 0.00000000000001)
	}

	def testdList() {
		class C(val i:Double) {}
		val list = List(new C(1), new C(-1), new C(3))
		val dlist = Math.dlist(list, (c:C) => c.i)
		assertEquals(List(1d, -1d, 3).toString, dlist.toString)
	}

	def testDivisible {
		assertTrue(divisible(4,2))
		assertFalse(divisible(3,2))
		assertTrue(divisible(-4,2))
		assertFalse(divisible(-3,2))
	}

	def testD {
		val tol = 0.0000001
		assertEquals(1, D(33, (x:Double) => x), tol)
		assertEquals(1, D(-33, (x:Double) => x), tol)

		assertEquals(2, D(1, (x:Double) => x*x), tol)
		assertEquals(-2, D(-1, (x:Double) => x*x), tol)
		assertEquals(4, D(2, (x:Double) => x*x), tol)
		assertEquals(-4, D(-2, (x:Double) => x*x), tol)

		assertEquals(0, D(0, (x:Double) => x*x*x), tol)
		assertEquals(3, D(1, (x:Double) => x*x*x), tol)
		assertEquals(3, D(-1, (x:Double) => x*x*x), tol)
		assertEquals(12, D(2, (x:Double) => x*x*x), tol)
		assertEquals(12, D(-2, (x:Double) => x*x*x), tol)

		assertEquals(0, D(1212, (x:Double) => 12123), tol)

		loop(10000) {
			val x = RNG.rand(0,100)
			assertEquals(-sin(x), D(x,cos), tol)
			assertEquals(cos(x), D(x,sin), tol)
		}
	}

	def testPoint {
		assertTrue(new Point(1.1, 2.2) == new Point(1.1, 2.2))
		assertTrue(new Point(1.1, 2.2) equals  new Point(1.1, 2.2))

		assertFalse(new Point(1.2, 2.2) == new Point(1.1, 2.2))
		assertFalse(new Point(1.2, 2.2) equals  new Point(1.1, 2.2))

		assertFalse(new Point(1.1, 2.3) == new Point(1.1, 2.2))
		assertFalse(new Point(1.1, 2.3) equals  new Point(1.1, 2.2))

		assertEqualsS("(1.1,2.2)", new Point(1.1, 2.2))
		assertEqualsS("(1.1,-2.2)", new Point(1.1, -2.2))
	}

	def testInterpolate {
		assertEquals(new Point(2,2), interpolate(new Point(1,1), new Point(3,3), 2))
		assertEquals(new Point(1,1), interpolate(new Point(1,1), new Point(3,3), 1))
		assertEquals(new Point(3,3), interpolate(new Point(1,1), new Point(3,3), 3))

		assertEquals(new Point(2,-2), interpolate(new Point(1,-1), new Point(3,-3), 2))
		assertEquals(new Point(1,-1), interpolate(new Point(1,-1), new Point(3,-3), 1))
		assertEquals(new Point(3,-3), interpolate(new Point(1,-1), new Point(3,-3), 3))

		assertEquals(new Point(-2,2), interpolate(new Point(-3,3), new Point(-1,1), -2))
		assertEquals(new Point(-1,1), interpolate(new Point(-3,3), new Point(-1,1), -1))
		assertEquals(new Point(-3,3), interpolate(new Point(-3,3), new Point(-1,1), -3))

		assertEquals(new Point(7/3.,7/3.), interpolate(new Point(1,1), new Point(3,3), 7/3.))
		assertEquals(new Point(7/3.,-7/3.), interpolate(new Point(1,-1), new Point(3,-3), 7/3.))
		assertEquals(new Point(-7/3.,7/3.), interpolate(new Point(-3,3), new Point(-1,1), -7/3.))
		assertEquals(new Point(-7/3.,-7/3.), interpolate(new Point(-3,-3), new Point(-1,-1), -7/3.))
	}

	def testInterpolateTuple {
		assertEquals((2d,2d),interpolate((1d,1d),(3d,3d),2d))
		assertEquals((1d,1d),interpolate((1d,1d),(3d,3d),1d))
		assertEquals((3d,3d),interpolate((1d,1d),(3d,3d),3d))

		assertEquals((2d,-2d),interpolate((1d,-1d),(3d,-3d),2d))
		assertEquals((1d,-1d),interpolate((1d,-1d),(3d,-3d),1d))
		assertEquals((3d,-3d),interpolate((1d,-1d),(3d,-3d),3d))

		assertEquals((-2d,2d),interpolate((-3d,3d),(-1d,1d),-2d))
		assertEquals((-1d,1d),interpolate((-3d,3d),(-1d,1d),-1d))
		assertEquals((-3d,3d),interpolate((-3d,3d),(-1d,1d),-3d))

		assertEquals((7/3d,7/3d),interpolate((1d,1d),(3d,3d),7/3d))
		assertEquals((7/3d,-7/3d),interpolate((1d,-1d),(3d,-3d),7/3d))
		assertEquals((-7/3d,7/3d),interpolate((-3d,3d),(-1d,1d),-7/3d))
		assertEquals((-7/3d,-7/3d),interpolate((-3d,-3d),(-1d,-1d),-7/3d))
	}

	def testCmp {
		assertTrue(cmp(1,1,0))
		assertFalse(cmp(1,1,-1d))
		assertTrue(cmp(1.000001,1.1,0.1))
		assertTrue(cmp(1.1,1.000001,0.1))
		assertFalse(cmp(1,1.11,0.1))
		assertTrue(cmp(1,1.11,0.11000001))
	}

	def testFactorial {
		assertEquals(1, factorial(-5))
		assertEquals(1, factorial(0))
		assertEquals(1, factorial(1))
		assertEquals(2, factorial(2))
		assertEquals(6, factorial(3))
		assertEquals(120, factorial(5))
		assertEquals(720, factorial(6))
	}
}
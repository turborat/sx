package m

import m.Integrator._
import scala.math._
import x.X._
import test.TestX

class IntegratorTest extends TestX
{
	def linear(x:Double) = x
	def flat(x:Double) = 2.

	def testTrapezoid1() {
		assertEquals(0., trapezoid(0,0,linear),.00000001)

		assertEquals(.5, trapezoid(0,1,linear), .00000001)
		assertEquals(-.5, trapezoid(-1,0,linear), .00000001)

		assertEquals(2., trapezoid(0,2,linear), .00000001)
		assertEquals(-2., trapezoid(-2,0,linear), .00000001)

		assertEquals(0., trapezoid(-2,2,linear), .00000001)

		assertEquals(0, trapezoid(0,0,flat), .00000001)
		assertEquals(20, trapezoid(0,10,flat), .00000001)
		assertEquals(2, trapezoid(0,1,flat), .00000001)
		assertEquals(12., trapezoid(-3,3,flat), .00000001)
	}

	def testTrapezoid2() {
		assertEquals(2, trapezoid(0,Pi,sin), .00000001)
		assertEquals(1, trapezoid(0,Pi/2,sin), .00000001)
		assertEquals(0, trapezoid(0,2*Pi,sin), .00000001)
		assertEquals(0, trapezoid(-Pi,Pi,sin), .00000001)
	}

	def testTimeTrapezoid() {
		for (m <- 1 to 5) // warm-up
		for (n <- 1 to 7) {
			val s = pow(10,n).toInt
			val elapsed  = timeit {
				trapezoid(0,Pi,sin,steps=s)
			}

			if (m==4)
				printf("Integrating sin with %,d steps took %,d ns%n", s, elapsed)
		}
	}
}
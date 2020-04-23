package m

import scala.math._
import test.TestX

class GaussianTest extends TestX
{
	def testPdf1() {
		assertEquals(0.398942, Gaussian.pdf(0), 0.000001)
		assertEquals(0.241971, Gaussian.pdf(1), 0.000001)
		assertEquals(0.241971, Gaussian.pdf(-1), 0.000001)
		assertEquals(0.053991, Gaussian.pdf(2), 0.000001)
		assertEquals(0.053991, Gaussian.pdf(-2), 0.000001)

		assertEquals(0.00991738, Gaussian.pdf(E), 0.000001)
		assertEquals(0.00286915, Gaussian.pdf(Pi), 0.000001)
	}

	def testPdf2() {
		assertEquals(0.125794, Gaussian.pdf(1,2,3), 0.000001)
		assertEquals(-0.125794, Gaussian.pdf(-1,-2,-3), 0.000001)
		assertEquals(0.241971, Gaussian.pdf(3,2,1), 0.000001)
		assertEquals(-0.241971, Gaussian.pdf(-3,-2,-1), 0.000001)
	}

	def testCdf1() {
		assertEquals(.5, Gaussian.cdf(0), 0.000001)
		assertEquals(0.841345, Gaussian.cdf(1), 0.000001)
		assertEquals(0.158655, Gaussian.cdf(-1), 0.000001)
		assertEquals(0.97725, Gaussian.cdf(2), 0.000001)
		assertEquals(0.0227501, Gaussian.cdf(-2), 0.000001)

		assertEquals(0.996719, Gaussian.cdf(E), 0.000001)
		assertEquals(0.99916, Gaussian.cdf(Pi), 0.000001)
	}

	def testCdf2() {
		assertEquals(0.369441, Gaussian.cdf(1,2,3), 0.000001)
		assertEquals(0.841345, Gaussian.cdf(3,2,1), 0.000001)
	}
}
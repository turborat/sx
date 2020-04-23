package fin.opt

import test.TestX
import x.TheFly
import x.X._
import m.{Stats, RNG}

class EurOptXTestSuite extends EurOptTestSuite(new OptionBuilder.EurOptX, tol = 0.01)

class EurOptMCTestSuite extends EurOptTestSuite(new OptionBuilder.EurOptMC, tol = 0.15, greekTol = 0.25)

//class EurOptMCSlowTestSuite extends EurOptTestSuite(new OptionBuilder.EurOptMCSlow, tol=0.15, greekTol=0.25)
//class EurOptFD0TestSuite extends EurOptTestSuite(new OptionBuilder.EurOptFD0,tol=1, greekTol=4)
class EurOptFDTestSuite extends EurOptTestSuite(new OptionBuilder.EurOptFD, tol = 1.1, greekTol = 5)


abstract class EurOptTestSuite(bldr: OptionBuilder[_ <: Opt], tol: Double, greekTol: Double)
	extends TestX
{
	def this(bldr: OptionBuilder[_ <: Opt], tol: Double) = this(bldr, tol, tol)

	val opt = bldr.build(S = 100, K = 100, v = 0.5, r = 0.05, T = 1, call = true)

	def testVCalls
	{
		assertEqualsV(21.79, opt.V, tol)

		assertEqualsV(15.82, opt.reval(S = 90).V, tol)
		assertEqualsV(28.51, opt.reval(S = 110).V, tol)

		assertEqualsV(26.37, opt.reval(K = 90).V, tol)
		assertEqualsV(17.96, opt.reval(K = 110).V, tol)

		assertEqualsV(14.23, opt.reval(v = 0.3).V, tol)
		assertEqualsV(29.20, opt.reval(v = 0.7).V, tol)

		assertEqualsV(20.55, opt.reval(r = 0.02).V, tol)
		assertEqualsV(22.63, opt.reval(r = 0.07).V, tol)

		assertEqualsV(15.12, opt.reval(T = 0.5).V, tol)
		assertEqualsV(26.96, opt.reval(T = 1.5).V, tol)
	}

	def testVPuts
	{
		assertEqualsV(16.91, opt.reval(call = false).V, tol)

		assertEqualsV(20.94, opt.reval(S = 90, call = false).V, tol)
		assertEqualsV(13.63, opt.reval(S = 110, call = false).V, tol)

		assertEqualsV(11.98, opt.reval(K = 90, call = false).V, tol)
		assertEqualsV(22.59, opt.reval(K = 110, call = false).V, tol)

		assertEqualsV(9.35, opt.reval(v = 0.3, call = false).V, tol)
		assertEqualsV(24.32, opt.reval(v = 0.7, call = false).V, tol)

		assertEqualsV(18.57, opt.reval(r = 0.02, call = false).V, tol)
		assertEqualsV(15.87, opt.reval(r = 0.07, call = false).V, tol)

		assertEqualsV(12.65, opt.reval(T = 0.5, call = false).V, tol)
		assertEqualsV(19.74, opt.reval(T = 1.5, call = false).V, tol)
	}

	def testDelta
	{
		assertEqualsV(0.63, opt.delta, greekTol)
		assertEqualsV(0.46, opt.reval(S = 80).delta, greekTol)
		assertEqualsV(0.76, opt.reval(S = 120).delta, greekTol)

		assertEqualsV(-0.36, opt.reval(call = false).delta, greekTol)
		assertEqualsV(-0.53, opt.reval(S = 80, call = false).delta, greekTol)
		assertEqualsV(-0.23, opt.reval(S = 120, call = false).delta, greekTol)
	}

	def testGamma
	{
		assertEqualsV(0.007, opt.gamma, greekTol)
		assertEqualsV(0.009, opt.reval(S = 80).gamma, greekTol)
		assertEqualsV(0.004, opt.reval(S = 120).gamma, greekTol)

		assertEqualsV(0.007, opt.reval(call = false).gamma, greekTol)
		assertEqualsV(0.009, opt.reval(S = 80, call = false).gamma, greekTol)
		assertEqualsV(0.004, opt.reval(S = 120, call = false).gamma, greekTol)
	}

	def testTheta
	{
		assertEqualsV(-11.47, opt.theta, greekTol)
		assertEqualsV(-9.25, opt.reval(S = 80).theta, greekTol)
		assertEqualsV(-12.05, opt.reval(S = 120).theta, greekTol)

		assertEqualsV(-6.71, opt.reval(call = false).theta, greekTol)
		assertEqualsV(-4.49, opt.reval(S = 80, call = false).theta, greekTol)
		assertEqualsV(-7.29, opt.reval(S = 120, call = false).theta, greekTol)
	}

	def testVega
	{
		assertEqualsV(37.52, opt.vega, greekTol)
		assertEqualsV(31.76, opt.reval(S = 80).vega, greekTol)
		assertEqualsV(37.08, opt.reval(S = 120).vega, greekTol)

		assertEqualsV(37.52, opt.reval(call = false).vega, greekTol)
		assertEqualsV(31.76, opt.reval(S = 80, call = false).vega, greekTol)
		assertEqualsV(37.08, opt.reval(S = 120, call = false).vega, greekTol)
	}

	def testRho
	{
		assertEqualsV(41.89, opt.rho, greekTol)
		assertEqualsV(26.20, opt.reval(S = 80).rho, greekTol)
		assertEqualsV(55.64, opt.reval(S = 120).rho, greekTol)

		assertEqualsV(-53.23, opt.reval(call = false).rho, greekTol)
		assertEqualsV(-68.91, opt.reval(S = 80, call = false).rho, greekTol)
		assertEqualsV(-39.47, opt.reval(S = 120, call = false).rho, greekTol)
	}

	def testReval
	{
		val opt2 = opt.reval()
		for (dirt <- new TheFly(classOf[Opt]).members) {
			printf("Testing %s : %s == %s ... ", dirt, dirt(opt), dirt(opt2))
			assertTrue(dirt.toString, dirt(opt) == dirt(opt2))
			println("ok")
		}
	}
}


class EurOptMCTests extends TestX
{

	class EZOpt extends EurOptMC(S = 100, K = 90, v = 0.2, r = 0.05, T = 1, call = true)
	{
		walks = 1000
		name = "EurOptMC"
	}

	def testSeed
	{
		assertTrue(new EZOpt().seed != new EZOpt().seed)
		val opt1 = new EZOpt
		val opt2 = opt1.reval()
		assertEquals(opt1.seed, opt2.seed)
	}

	def testV
	{
		assertTrue(new EZOpt().V != new EZOpt().V)
		val opt1 = new EZOpt
		val opt2 = opt1.reval()
		assertEquals(opt1.V, opt2.V)
	}

	def testReval
	{
		val opt1 = new EZOpt
		{
			verbose = RNG.irand(10).toInt
			walks = RNG.irand(1000) * 8
		}

		val opt2 = opt1.reval()
		assertEquals(opt1.walks, opt2.walks)
		assertEquals(opt1.verbose, opt2.verbose)
		assertEquals(opt1.seed, opt2.seed)
	}

	def testReval2
	{
		val opt1 = new EZOpt
		{
			verbose = 1
		}
		val opt2 = opt1.reval(S = 33)
		val opt3 = opt1.reval(S = 33)

		assertTrue(opt1.S != opt2.S)
		assertTrue(opt1.V != opt2.V)

		assertTrue(opt2.S == opt3.S)
		assertTrue(opt2.V == opt3.V)
	}

	def testRNG
	{
		val opt = new EZOpt()
		{
			walks = 1000

			override def go(rng: RNG): Double =
			{
				assertEqualsS("-Mersenne Twister w/Box Müller", rng)
				1
			}
		}

		opt.V()
	}

	def testVsXplicit
	{
		var pass = 0
		val tests = 200
		val tol = 0.03

		val diffStats = new Stats

		val mcBldr = new OptionBuilder.EurOptMC
		val xBldr = new OptionBuilder.EurOptX

		loop(tests) {
			val opt1 = mcBldr.build()
			val opt2 = xBldr.clone(opt1)
			//			println(opt1 + "\n" + opt2)

			val diff = opt2.V - opt1.V
			diffStats << diff
			if (diff * diff <= tol * tol)
				pass += 1
		}

		val pct = 100 * pass / tests
		printf("Percent pass at %f tolerance: %d %% %n", tol, pct)
		printf("Diff: avg:%f stdev:%f%n", diffStats.avg, diffStats.stdev)
		assertTrue(pct > 80)
	}

	def testToString
	{
		val opt = new EurOptMC(S = 100d, K = 90d, v = 0.2, T = 4 / 3., r = 0.01, call = true)
		assertEqualsS("S:100.00 K:90.00 v:0.20 r:0.01 T:1.33 CALL - EurOptMC NAS:1 walks:1,000,000", opt)
	}
}

class EurOptXTests extends TestX
{
	def testCopyCtor
	{
		val opt1 = new EurOptX(S = 123, K = 132, v = .3, r = .01, T = .33, call = false)
		val opt2 = new EurOptX(opt1)

		assertEquals(opt1.S, opt2.S)
		assertEquals(opt1.K, opt2.K)
		assertEquals(opt1.v, opt2.v)
		assertEquals(opt1.r, opt2.r)
		assertEquals(opt1.T, opt2.T)
		assertTrue(opt1.call == opt2.call)
		assertEquals(opt1.V, opt2.V)
	}
}

class EurOptMCSlowTests extends TestX
{
	def test1
	{
		val opt = new EurOptMCSlow(S = 12, K = 10, v = 0.2, r = .01, T = 1.2, call = true)
		{
			verbose = 0
		}
		assertEquals(2.36, opt.V, 0.01)
		assertEquals(opt.V, opt.reval().V)
	}

	def testToString
	{
		val opt = new EurOptMCSlow(S = 12, K = 10, v = 0.2, r = .01, T = 1.2, call = true)
		assertEqualsS("S:12.00 K:10.00 v:0.20 r:0.01 T:1.20 CALL - EurOptMCSlow NAS:100 walks:1,000,000", opt)
	}
}


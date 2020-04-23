package fin.opt

import test.TestX

class OptionTests extends TestX
{
	def testToString
	{
		val opt = new Opt with CDGreeks
		{
			val opt = this
			val S = 100d
			val K = 90d
			val v = 0.2
			val r = 0.01
			val T = 4 / 3.
			val call = true

			def V() = 1000 * 1000

			def reval(S: Double, K: Double, v: Double, r: Double, T: Double, call: Boolean) = null

			override def flavour = "sweeeeet"
		}

		assertEqualsS("S:100.00 K:90.00 v:0.20 r:0.01 T:1.33 CALL - sweeeeet", opt)
	}
}

class CDGreekTests extends TestX
{

	class EzCDGreeks(opt: Opt) extends CDGreeks
	{
		val S = opt.S
		val K = opt.K
		val v = opt.v
		val r = opt.r
		val T = opt.T
		val call = opt.call

		def V = opt.V

		def reval(S: Double = S, K: Double = K, v: Double = v, r: Double = r, T: Double = T, call: Boolean = call)
		= new EzCDGreeks(opt.reval(S, K, v, r, T, call))
	}

	val tol = 0.00001
	val optXATM = new EurOptX(S = 100, K = 100, v = 0.5, r = 0.05, T = 1, call = true)
	val optSATM = new EzCDGreeks(optXATM)

	val optXOTM = optXATM.reval(S = 80)
	val optSOTM = new EzCDGreeks(optXOTM)

	val optXITM = optXATM.reval(S = 120)
	val optSITM = new EzCDGreeks(optXITM)

	def testDelta
	{
		assertEqualsV(optXATM.delta, optSATM.delta, tol)
		assertEqualsV(optXOTM.delta, optSOTM.delta, tol)
		assertEqualsV(optXITM.delta, optSITM.delta, tol)
	}

	def testGamma
	{
		assertEqualsV(optXATM.gamma, optSATM.gamma, tol)
		assertEqualsV(optXOTM.gamma, optSOTM.gamma, tol)
		assertEqualsV(optXITM.gamma, optSITM.gamma, tol)
	}

	def testTheta
	{
		assertEqualsV(optXATM.theta, optSATM.theta, tol)
		assertEqualsV(optXOTM.theta, optSOTM.theta, tol)
		assertEqualsV(optXITM.theta, optSITM.theta, tol)
	}

	def testRho
	{
		assertEqualsV(optXATM.rho, optSATM.rho, tol)
		assertEqualsV(optXOTM.rho, optSOTM.rho, tol)
		assertEqualsV(optXITM.rho, optSITM.rho, tol)
	}

	def testVega
	{
		assertEqualsV(optXATM.vega, optSATM.vega, tol)
		assertEqualsV(optXOTM.vega, optSOTM.vega, tol)
		assertEqualsV(optXITM.vega, optSITM.vega, tol)
	}
}
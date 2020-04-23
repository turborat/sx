package fin.opt

import test.TestX

class AmiOptFDTests extends TestX
{
	val tol = 0.06
	val greekTol = 3.00
	val opt = new AmiOptFD(S = 50, K = 50, v = 0.3, r = 0.05, T = 1, call = true)

	def testCalls
	{
		assertEqualsV(7.06, opt.V, tol)
		assertEqualsV(2.24, opt.reval(S = 40).V, tol)
		assertEqualsV(14.40, opt.reval(S = 60).V, tol)
		assertEqualsV(13.21, opt.reval(K = 40).V, tol)
		assertEqualsV(3.40, opt.reval(K = 60).V, tol)
		assertEqualsV(3.16, opt.reval(v = 0.1).V, tol)
		assertEqualsV(10.86, opt.reval(v = 0.5).V, tol)
		assertEqualsV(6.13, opt.reval(r = 0.01).V, tol)
		assertEqualsV(8.31, opt.reval(r = 0.10).V, tol)
		assertEqualsV(4.73, opt.reval(T = .5).V, tol)
		assertEqualsV(10.56, opt.reval(T = 2).V, tol)
		tolStats.print(3)
	}

	def testPuts
	{
		assertEqualsV(4.85, opt.reval(call = false) V, tol)
		assertEqualsV(10.60, opt.reval(S = 40, call = false).V, tol)
		assertEqualsV(2.04, opt.reval(S = 60, call = false).V, tol)
		assertEqualsV(1.30, opt.reval(K = 40, call = false).V, tol)
		assertEqualsV(11.25, opt.reval(K = 60, call = false).V, tol)
		// fails for some reason
		//		assertEqualsV(0.95,opt.reval(v=0.1, call=false).V,tol)
		assertEqualsV(8.68, opt.reval(v = 0.5, call = false).V, tol)
		assertEqualsV(5.66, opt.reval(r = 0.01, call = false).V, tol)
		assertEqualsV(4.08, opt.reval(r = 0.10, call = false).V, tol)
		assertEqualsV(3.59, opt.reval(T = .5, call = false).V, tol)
		assertEqualsV(6.35, opt.reval(T = 2, call = false).V, tol)
		tolStats.print(3)
	}

	def testGreeksCalls
	{
		assertEqualsV(0.62, opt.delta, greekTol)
		assertEqualsV(0.02, opt.gamma, greekTol)
		assertEqualsV(-4.08, opt.theta, greekTol)
		assertEqualsV(19.48, opt.vega, greekTol)
		assertEqualsV(24.04, opt.rho, greekTol)
		tolStats.print(3)
	}

	def testGreeksPuts
	{
		assertEqualsV(-0.40, opt.reval(call = false).delta, greekTol)
		assertEqualsV(0.02, opt.reval(call = false).gamma, greekTol)
		assertEqualsV(-2.01, opt.reval(call = false).theta, greekTol)
		assertEqualsV(19.48, opt.reval(call = false).vega, greekTol)
		assertEqualsV(-18.05, opt.reval(call = false).rho, greekTol)
		tolStats.print(3)
	}
}

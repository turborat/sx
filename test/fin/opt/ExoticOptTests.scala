package fin.opt

import test.TestX

class ExoticOptTests extends TestX
{
	def testAsian
	{
		val tol = 0.05
		val opt = new AsianOptMC(S = 10, K = 10, v = 0.2, r = 0.03, T = 2, call = true)
		{
			walks = 10 * 1000
			verbose = 0
		}

		assertEquals(opt.V, opt.V)
		assertEquals(opt.V, opt.reval().V)

		assertEqualsV(0.78, opt.V, tol)
		assertEqualsV(0.09, opt.reval(S = 8).V, tol)
		assertEqualsV(2.29, opt.reval(S = 12).V, tol)
		assertEqualsV(0.17, opt.reval(K = 12).V, tol)
		assertEqualsV(0.69, opt.reval(r = 0.01).V, tol)
		assertEqualsV(1.40, opt.reval(v = 0.4).V, tol)
		assertEqualsV(0.53, opt.reval(T = 1).V, tol)
		assertEqualsV(0.49, opt.reval(call = false).V, tol)
		assertEqualsV(1.76, opt.reval(call = false, K = 12).V, tol)
	}
}

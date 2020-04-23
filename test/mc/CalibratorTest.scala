package mc

import test.TestX
import x.X._
import m.RNG
import fin.opt.EurOptMC

abstract class CalibratorTest extends TestX
{
	def test1 {
		val sim = new Sim {
			def go(rng: RNG) = rng.nrand
		}

		val stdev = 0.01
		val results = new Calibrator(sim).targetStdev(stdev)

		assertTrue(results.last.stdev <= stdev)
		assertTrue(results(results.size-2).stdev > stdev)
		assertTrue(oneOf(results.last.iter, 16384, 8192))
	}

	def testMCRNGS {
		val opt = new EurOptMC(S=12, K=14, v=0.2, r=0.01, T=1, call=true)

		val stdev = 0.005
		val results = new Calibrator(opt) { verbose = 0 }.targetStdev(stdev)

		assertTrue(results.last.stdev <= stdev)
		assertTrue(results(results.size-2).stdev > stdev)
		assertTrue(oneOf(results.last.iter, 16384, 32768, 65536))
	}

	def testIterationsSeq {
		val itit = new ExpSeq().iterator
		for (pow <- 2 to 1000) {
			assertTrue(itit.hasNext)
			assertEquals(2<<pow, itit.next)
		}
	}
}
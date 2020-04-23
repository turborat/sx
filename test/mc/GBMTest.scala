package mc

import test.TestX
import x.X._
import m.{Stats, RNG, Seeder}
import x.SpeedathlonX

class GBMTest extends TestX
{
	def test1 {
		val (gbm1, gbm2) = GBMSpeedTest.twoGBMS
		val x1 = ptime("GBM") { gbm1.sN }
		val x2 = ptime("SimpleGBM") { gbm2.sN }
		assertEquals(x1, x2)
	}
}


object GBMSpeedTest
{
	// 21.2.2012
  // 1.00 -  51 us - GBM Base
  // 1.45 -  74 us - GBM Real

	def main(args:Array[String]) {
		val stats = new Stats

		loop(1000*10) {
			val (gbm1, gbm2) = twoGBMS
			val sa = new SpeedathlonX
			sa.contend("GBM Real") { gbm1.sN }
			sa.contend("GBM Base") { gbm2.sN }
			val results = sa.compete
//			results foreach println
//			println

			results(0).id match {
				case "GBM Base" => stats << (results(1).score - results(0).score)
				case "GBM Real" => stats << (results(0).score - results(1).score)
			}
		}

		println(stats)
	}

	def twoGBMS:(GBM,BaselineGBM) = {
		val seed = Seeder.next
		val s0 = RNG.rand(0,10)
		val drift = RNG.rand
		val diffusion = RNG.rand
		val steps = RNG.irand(100,1000).toInt
		val T = RNG.rand(0,10)

		val gbm1 = new GBM(S0=s0, drift=drift, diffusion=diffusion, steps=steps, T=T, rng=RNG(seed))
		{
			ps = new PathStuff {
				def atStep(s: Double, step: Double) = null
				def atEnd(s: Double) = s
			}
		}

		val gbm2 = new BaselineGBM(S=s0, drift=drift, diffusion=diffusion, steps=steps, T=T, rng=RNG(seed))

		(gbm1,gbm2)
	}
}

private [mc] class BaselineGBM(S:Double, drift:Double, diffusion:Double, steps:Int, T:Double, rng:RNG)
{
	def sN:Double = {
		var s = S
		val tStep = T.toDouble / steps
		var n = steps
		val tStepSqrt = scala.math.sqrt(tStep)

		while (n > 0) {
			s *= 1 + (drift * tStep) + (diffusion * tStepSqrt * rng.nrand)
			n -= 1
		}

		s
	}
}

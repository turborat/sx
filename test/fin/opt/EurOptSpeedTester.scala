package fin.opt

import m.RNG
import mc.{MC, Sim}
import scala.math._
import x.X._
import x.Speedathlon

object EurOptSpeedTester
{

	// abstract monte-carlo
	class EurOptBaseline(val S: Double, val K: Double, val v: Double, val r: Double, val T: Double, val call: Boolean)
		extends Sim with Opt with CDGreeks
	{
		var walks = 2 * 1000 * 1000
		var NAS = 100

		def go(rng: RNG) =
		{
			var s = S
			val dt = T / NAS
			var step = 0
			val dtSqrt = sqrt(dt)

			while (step < NAS) {
				s *= 1 + (r * dt) + (v * dtSqrt * rng.nrand)
				step += 1
			}

			if (call)
				max(0, s - K) * exp(-r * T)
			else
				max(0, K - s) * exp(-r * T)
		}

		final def V(): Double = new MC(this, walks).run.res

		def copy(S: Double, K: Double, v: Double, r: Double, T: Double, call: Boolean) = null

		def reval(S: Double, K: Double, v: Double, r: Double, T: Double, call: Boolean) = null
	}

	def main(args: Array[String])
	{
		val S = 100
		val K = 100
		val v = 0.2
		val r = 0.04
		val T = 3
		val call = true

		val sa = new Speedathlon[Opt]
		sa.contenders += new EurOptBaseline(S = S, K = K, v = v, r = r, T = T, call = call)
		sa.contenders += new EurOptMCSlow(S = S, K = K, v = v, r = r, T = T, call = call)

		loop(5) {
			sa.compete() {
				opt => opt.V
			} foreach println
			println
		}
	}
}

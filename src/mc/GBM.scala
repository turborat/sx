package mc

import m.RNG
import scala.math._


class GBM(S0:Double, diffusion:Double, drift:Double, T:Double, steps:Int, rng:RNG)
{
	protected var ps:PathStuff = null

	def sN:Double = {
		if (steps < 1)
			throw new IllegalArgumentException("steps="+steps)

		assert (ps == null && steps == 1 || ps != null && steps > 1)

		if (steps == 1)
			oneStep
		else
			walk
	}

	private def walk:Double = {
		val dt = T / steps
		val dtSqrt = sqrt(dt)
		var s = S0
		var step = 0

		while (step < steps) {
			s *= 1 + (drift * dt) + (diffusion * dtSqrt * rng.nrand)
			ps.atStep(s,step)
			step += 1
		}

		ps.atEnd(s)
	}

	private def oneStep:Double = {
		S0 * exp((drift-(diffusion*diffusion)/2)*T + diffusion*sqrt(T)*rng.nrand)
	}
}


trait PathStuff
{
	def atStep(s:Double, step:Double)
	def atEnd(s:Double):Double
}



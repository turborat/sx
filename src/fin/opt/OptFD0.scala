package fin.opt

import scala.math._
import x.X._
import m.Math._
import x.{Disaster, MapX}

// Explicit Finite Difference Method as Wilmott would do it (with the exception of the interpolation)
abstract class OptFD0(val S:Double, val K:Double, val v:Double, val r:Double, val T:Double, val call:Boolean, val USA:Boolean)
	extends Opt with CDGreeks
{
	var NAS = 20

	def V:Double =
	{
		if (values.contains(S))
			values(S)
		else
//			Disaster("interpolation not enabled")
			interpolate(values.floorEntry(S), values.ceilEntry(S), S)._2
	}

	val values = {
		val S = Array.ofDim[Double](NAS + 1) // asset price array
		val VOld = Array.ofDim[Double](NAS + 1) // to the right
		val VNew = Array.ofDim[Double](NAS + 1)
		val dummy = Array.ofDim[Double](NAS + 1, 3)

		val dS = 2 * K / NAS

		var dt:Double = 0
		var NTS:Int = 0

		dt = 0.9 / NAS / NAS / v / v // time step must be small otherwise no convergence
		// normally choose 1 but .9 is smaller ergo better
		NTS = (T / dt).toInt + 1 // number of time steps
		dt = T / NTS

		for (i <- 0 to NAS) {
			// Setup nodes along expiration
			S(i) = i * dS
			VOld(i) = max(?(call, 1, -1) * (S(i) - K), 0)
			dummy(i)(0) = S(i)
			dummy(i)(1) = VOld(i) // Payoff
		}

		// business loop
		for (k <- 1 to NTS) {
			for (i <- 1 to NAS - 1) {
				val Delta = (VOld(i + 1) - VOld(i - 1)) / 2 / dS // central difference
				val Gamma = (VOld(i + 1) - 2 * VOld(i) + VOld(i - 1)) / dS / dS
				val Theta = -0.5 * v * v * S(i) * S(i) * Gamma -
					r * S(i) * Delta + r * VOld(i) // Black-Scholes
				VNew(i) = VOld(i) - Theta * dt
			}

			VNew(0) = VOld(0) * (1 - r * dt) // PV , S=0
			VNew(NAS) = 2 * VNew(NAS - 1) - VNew(NAS - 2) // linear extrapolation , Infinity

			for (i <- 0 to NAS) {
				VOld(i) = VNew(i)
			}

			// Early exercise - omit this for European
			if (USA) {
				for (i <- 0 until NAS) {
					// decide to exercise early
					VOld(i) = max(VOld(i), dummy(i)(1))
				}
			}
		}

		for (i <- 0 to NAS) {
			dummy(i)(2) = VOld(i)
		}

		val ret = new MapX[Double, Double]
		for (i <- 0 to NAS) {
			ret +=(dummy(i)(0), dummy(i)(2))
		}

		ret
	}
}
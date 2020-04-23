package fin.opt

import mc._
import m._
import scala.math._


// price an option by monte-carlo
abstract class OptMC(val S:Double, val K:Double, val v:Double, val r:Double, val T:Double, val call:Boolean)
	extends Sim with Opt with CDGreeks
{
	var walks:Long
	var NAS:Int
	var seed = Seeder.next
	var verbose = 0

	// implement these
	def copy(S:Double, K:Double, v:Double, r:Double, T:Double, call:Boolean):OptMC
	def pathStuff():PathStuff

	def go(rng:RNG) = {
		val gbm = new GBM(S0=S, drift=r, diffusion=v, T=T, steps=NAS, rng=rng) {
			ps = pathStuff()
		}

		payoff(gbm.sN)
	}

	final def V():Double
		= new MC(this, walks, seed=seed, verbose=verbose).run.res

	final def reval(S:Double, K:Double, v:Double, r:Double, T:Double, call:Boolean):OptMC = {
		val opt = copy(S,K,v,r,T,call)
		opt.walks = walks
		opt.NAS = NAS
		opt.seed = seed
		opt.verbose = verbose
		opt
	}

	final def payoff(s:Double):Double
		= max(0, if (call) s-K else K-s) * exp(-r*T)

	override def flavour = "%s NAS:%,d walks:%,d" format (name, NAS, walks)
}

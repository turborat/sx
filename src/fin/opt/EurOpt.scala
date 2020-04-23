package fin.opt

import scala.math._
import m.Gaussian._
import m._
import mc.{PathStuff, GBM}
import x.X


// price by one-shot monte-carlo
class EurOptMC(S:Double, K:Double, v:Double, r:Double, T:Double, call:Boolean)
	extends OptMC(S,K,v,r,T,call)
{
	var walks = 1000*1000l
	var NAS = 1

	def pathStuff = null

	def copy(S:Double, K:Double, v:Double, r:Double, T:Double, call:Boolean):EurOptMC
		= new EurOptMC(S,K,v,r,T,call)

	def gbm(rng:RNG)
	  = new GBM(S0=S, drift=r, diffusion=v, T=T, steps=1, rng=rng)
}


// as a trivial, non-exotic example
class EurOptMCSlow(S:Double, K:Double, v:Double, r:Double, T:Double, call:Boolean)
	extends OptMC(S,K,v,r,T,call)
{
	var walks = 1000*1000l
	var NAS = 100

	// oddly, re-using the same path stuff is slower
	def pathStuff = new PathStuff {
		override def atStep(s:Double, step:Double) = null
		override def atEnd(s:Double):Double = s
	}

	def copy(S:Double, K:Double, v:Double, r:Double, T:Double, call:Boolean)
		= new EurOptMCSlow(S,K,v,r,T,call)
}


// explicit black-scholes
class EurOptX(val S:Double, val K:Double, val v:Double, val r:Double, val T:Double, val call:Boolean)
	extends Opt
{
	def this(opt:Opt) = this(opt.S, opt.K, opt.v, opt.r, opt.T, opt.call)

	private val d1 = (log(S/K) + (r+v*v/2) * T) / (v * sqrt(T))
	private val d2 = (log(S/K) + (r-v*v/2) * T) / (v * sqrt(T)) // = d1 - s*sqrt(T)

	private def N(x:Double) = cdf(x)
	private def Np(x:Double) = pdf(x)

	def V =
		if (call) N(d1)*S - N(d2)*K*exp(-r*T)
		else N(-d2)*K*exp(-r*T) - N(-d1)*S

	def delta =
		if (call) N(d1)
		else -N(-d1)

	def gamma =
		Np(d1) / (S*v*sqrt(T))

	def theta =
		if (call) -(S*Np(d1)*v) / (2*sqrt(T)) - r*K*exp(-r*T)*N(d2)
		else -(S*Np(d1)*v) / (2*sqrt(T)) + r*K*exp(-r*T)*N(-d2)

	def vega =
		S*Np(d1)*sqrt(T)

	def rho =
		if (call) K*T*exp(-r*T)*N(d2)
		else -K*T*exp(-r*T)*N(-d2)

	def reval(S:Double, K:Double, v:Double, r:Double, T:Double, call:Boolean)
    = new EurOptX(S,K,v,r,T,call)

	override def flavour = X.objName(this) + " method:explicit"
}


class EurOptFD(S:Double, K:Double, v:Double, r:Double, T:Double, call:Boolean)
	extends OptFD(S, K, v, r, T, call)
{
	def nodeValue(node:Node):Double
		= node.prev(node.sStep) - node.theta * dt

	def reval(S:Double, K:Double, v:Double, r:Double, T:Double, call:Boolean)
	  = new EurOptFD(S,K,v,r,T,call)
}


// Wilmotts version
class EurOptFD0(S:Double, K:Double, v:Double, r:Double, T:Double, call:Boolean)
	extends OptFD0(S,K,v,r,T,call,false)
{
	def reval(S:Double, K:Double, v:Double, r:Double, T:Double, call:Boolean)
	  = new EurOptFD0(S,K,v,r,T,call)
}

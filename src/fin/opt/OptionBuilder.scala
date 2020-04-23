package fin.opt

import m.RNG

abstract class OptionBuilder[T<:Opt](opt:T)
{
	def S = RNG.rand(30, 70)
	def K = RNG.rand(40, 60)
	def v = RNG.rand(0, 0.5)
	def r = RNG.rand(0.01, 0.5)
	def T = RNG.rand(.1, 6)
	def call = if (RNG.irand(1) > 0) true else false

	def build(S:Double=S, K:Double=K, v:Double=v, r:Double=r, T:Double=T, call:Boolean=call):T
	  = opt.reval(S,K,v,r,T,call).asInstanceOf[T]

	def clone(opt:Opt):T
	  = build(opt.S, opt.K, opt.v, opt.r, opt.T, opt.call)
}

object OptionBuilder
{
	class EurOptMC   extends OptionBuilder[fin.opt.EurOptMC]  (new fin.opt.EurOptMC(1,1,1,1,1,false))
	class EurOptX    extends OptionBuilder[fin.opt.EurOptX]   (new fin.opt.EurOptX(1,1,1,1,1,false))
	class EurOptFD   extends OptionBuilder[fin.opt.EurOptFD]  (new fin.opt.EurOptFD(1,1,1,1,1,false))
	class EurOptFD0  extends OptionBuilder[fin.opt.EurOptFD0] (new fin.opt.EurOptFD0(1,1,1,1,1,false))
	class AmiOptFD   extends OptionBuilder[fin.opt.AmiOptFD]  (new fin.opt.AmiOptFD(1,1,1,1,1,false))
	class AmiOptFD0  extends OptionBuilder[fin.opt.AmiOptFD0] (new fin.opt.AmiOptFD0(1,1,1,1,1,false))
	class AsianOptMC extends OptionBuilder[fin.opt.AsianOptMC](new fin.opt.AsianOptMC(1,1,1,1,1,false))
}


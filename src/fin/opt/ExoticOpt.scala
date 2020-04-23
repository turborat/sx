package fin.opt

import mc.PathStuff
import scala.math._

class AsianOptMC(S:Double, K:Double, v:Double, r:Double, T:Double, call:Boolean)
	extends OptMC(S,K,v,r,T,call)
{
	var	walks = 200*1000l
	var NAS = 100

	def pathStuff = new PathStuff
	{
		var sum = 0d
		override def atEnd(s:Double):Double = sum / NAS
		override def atStep(s:Double, step:Double):Unit = sum += s
	}

	def copy(S:Double, K:Double, v:Double, r:Double, T:Double, call:Boolean)
		= new AsianOptMC(S,K,v,r,T,call)
}


// not sure how exotic america is ....
class AmiOptFD(S:Double, K:Double, v:Double, r:Double, T:Double, call:Boolean)
	extends OptFD(S, K, v, r, T, call)
{
	def nodeValue(node:Node):Double = {
		// bug here:
		val vEur = node.prev(node.sStep) - node.theta * dt
		val finalPayoff = grid(node.sStep,0).V
		max(vEur, finalPayoff)
	}

	def reval(S:Double, K:Double, v:Double, r:Double, T:Double, call:Boolean)
	  = new AmiOptFD(S,K,v,r,T,call)
}


// Wilmotts version (for fun)
class AmiOptFD0(S:Double, K:Double, v:Double, r:Double, T:Double, call:Boolean)
	extends OptFD0(S, K, v, r, T, call, true)
{
	def reval(S:Double, K:Double, v:Double, r:Double, T:Double, call:Boolean)
	  = new AmiOptFD0(S,K,v,r,T,call)
}


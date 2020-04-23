package fin.opt

import m.Math._
import x.X._

trait Opt
{
	val S:Double
	val K:Double
	val v:Double
	val r:Double
	val T:Double
	val call:Boolean

	def V:Double

	def delta():Double
	def gamma():Double
	def theta():Double
	def vega():Double
	def rho():Double

	def flavour:String = objName(this)

	def reval(S:Double=S, K:Double=K, v:Double=v, r:Double=r, T:Double=T, call:Boolean=call):Opt

	final override def toString
	  = "S:%.2f K:%.2f v:%.2f r:%.2f T:%.2f %s - %s" format(S,K,v,r,T,if (call) "CALL" else "PUT", flavour)
}


// calculate the greeks by central-difference
trait CDGreeks extends Opt
{
	val scale = 10*1000d
	val nudge = 1/scale 	// error = O(nudge^2)

	final def delta = D(S, (x:Double) => reval(S=x).V)
	final def gamma = D(S, (x:Double) => reval(S=x).delta)
	final def theta = D(T, (x:Double) => reval(T=x).V) * -1
	final def rho   = D(r, (x:Double) => reval(r=x).V)
	final def vega  = D(v, (x:Double) => reval(v=x).V)
}
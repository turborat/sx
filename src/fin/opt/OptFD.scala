package fin.opt

import scala.math._
import m.Math._
import x.X._

abstract class OptFD(val S:Double, val K:Double, val v:Double, val r:Double, val T:Double, val call:Boolean)
	extends Opt
{
	val NAS = 20
	val NTS = {
		// time step must be small otherwise no convergence
		// normally choose 1 but .9 is smaller ergo better
		val dtTmp = 0.9 / NAS / NAS / v / v
		(T / dtTmp).toInt + 1
	}
	val dS = 2 * K / NAS
	val dt = T / NTS
	var nodes = 0


	protected class Node(val sStep:Int, val tStep:Int)
	{
		nodes += 1
		val s = sStep * dS + S
		val t = tStep * dt

		// todo: cache values
		def prev(sStep:Int) = grid(sStep, tStep - 1).V
		def curr(sStep:Int) = grid(sStep, tStep).V

		// lazy vals don't offer improvements
		def delta = (prev(sStep + 1) - prev(sStep - 1)) / 2 / dS // central difference
		def gamma = (prev(sStep + 1) - 2 * prev(sStep) + prev(sStep - 1)) / dS / dS
		def theta = -0.5 * v * v * s * s * gamma - r * s * delta + r * prev(sStep) // Black-Scholes

		// using a regular function and caching the
		// result is faster in a super-scalar setup
		lazy val V:Double = {
			if (tStep == 0) // payoff at maturity
				max(?(call, 1, -1) * (s - K), 0)
			else if (sStep <= -NAS/2)
				prev(0) * (1 - r * dt) // PV , S=0
			else if (sStep >= NAS/2)
				2 * curr(NAS/2 - 1) - curr(NAS/2 - 2) // linear extrapolation , Infinity
			else
				nodeValue(this)
		}
	}


	def nodeValue(node:Node):Double


	def V:Double = grid(0,NTS).V
	def delta = grid(0,NTS).delta
	def gamma = grid(0,NTS).gamma
	def theta = grid(0,NTS).theta
	def rho   = D(r, (x:Double) => reval(r=x).V)
	def vega  = D(v, (x:Double) => reval(v=x).V)


	protected object grid
	{
		private val _nodes = Array.ofDim[Node](NAS,NTS+1)

		// retrieve nodes with this method
		def apply(s:Int,t:Int):Node =
		{
			assert (s <= NAS/2)
			val logicalS = if (s < 0) s + NAS else s
			var node = _nodes(logicalS)(t)
			if (node == null) {
				node = new Node(s,t)
				_nodes(logicalS)(t) = node
			}
			node
		}
	}
}


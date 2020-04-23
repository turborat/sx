package sim

import scala.math._
import mc.{MC, SimApp}
import m.{Stats, RNG}
import gfx.{Graph, Points}

// 3	Distance Between Two Points in a Sphere
// Imagine a sphere of radius r=1. What is the average
// distance of 2 randomly chosen points within the sphere?
object IMCChallenge extends SimApp
{
	val stats  = new Stats
//	val mat    = new x.Matrix[Double]
	val points = new Points
	its = 1000*1000
	verbose = 1

	def point(rng:RNG):(Double,Double,Double) = {
		var x = rng.rand(-1,1)
		var y = rng.rand(-1,1)
		var z = rng.rand(-1,1)
		while (x*x + y*y + z*z > 1) {
			x = rng.rand(-1,1)
			y = rng.rand(-1,1)
			z = rng.rand(-1,1)
		}
		(x,y,z)
	}

	def go(rng:RNG):Double = {
		val p1 = point(rng)
		val p2 = point(rng)
    val dx = p1._1 - p2._1
    val dy = p1._2 - p2._2
    val dz = p1._3 - p2._3

		val dist = sqrt(dx*dx + dy*dy + dz*dz)

		stats << dist
//		points += (p1._1, p1._2)

		dist
	}

	println(stats)
//	new Graph("points", points)
}


object SanityCheck extends SimApp
{
	its = 10*1000*1000
  def go(rng:RNG) = abs(rng.rand - rng.rand)
}
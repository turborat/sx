package sim

import m.RNG
import scala.math._
import mc.{MC, SimApp}

// What is the average distance between two
// points within a unit square ?
object SusChallenge extends SimApp
{
	its = 10*1000*1000
  def go(rng:RNG):Double = {
    val dX = rng.rand - rng.rand
    val dY = rng.rand - rng.rand
    sqrt (dX*dX+dY*dY)
  }
}

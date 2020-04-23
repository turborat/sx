package sim

import mc.{MC, Sim}
import m.RNG
import x.X._

object Parallelism extends Sim {
  def go(rng:RNG):Double = 1

	def main(args:Array[String]) {
		loop(5) {
			MC(this, 500*1000*1000,1)
		}
	}
}



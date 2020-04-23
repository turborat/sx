package sim

import mc.{MC, SimApp}
import m.RNG

object Pi extends SimApp {
	its = 300*1000*1000
	verbose = 1

  override def go(rng:RNG) :Double = {
    val x = rng.rand
    val y = rng.rand
    if (x*x + y*y < 1) 4 else 0
  }
}

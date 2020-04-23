package sim

import mc.{MC, SimApp}
import m.RNG

object Sleeper extends SimApp {
  def go(rng:RNG) = {
    Thread.sleep(1000)
    2
  }

	its = 1
	jobs = 17
}
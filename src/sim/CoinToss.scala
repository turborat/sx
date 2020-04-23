package sim

import mc.{MC, SimApp}
import m.RNG

// how many coin tosses does it take to roll a six ?
object CoinToss extends SimApp {

	its = 50*1000*1000

  override def go(rng:RNG) :Double = {
		var n=0
		do {
			n += 1
		} while (rng.irand(1,6) != 6)
		n
  }
}

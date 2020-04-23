package sim

import mc.{MC, SimApp}
import m.RNG

/* Chuck-A-Luck is a gambling game often played at carnivals and
   gambling houses. A player may bet on any one of the numbers 1-6.
   Three dice are rolled. If the payer's number appears on one,
   two, or three of the dice, he receives respectively one, two or
   three time his original stake plus his own money back; otherwise
   he loses his stake. What is the player's expected loss per unit
   stake? (Actually the player may distribute stakes on several
   numbers, but each stake can be regarded as a separate bet.
 */
object ChuckALuck extends SimApp {
	its = 10*1000*1000

	def go(rng:RNG):Double = {
		val choice = 3
		var hits=0
		for (n <- 1 to 3)
			if (rng.irand(1,6)==choice)
				hits += 1

		//		return if (hits == 2) 1 else 0

		if (hits == 0)
			0
		else
			hits+1
	}

}
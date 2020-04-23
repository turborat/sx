package sim

import mc._
import m.{Stats, RNG}
import x._

object CardCounting extends SimApp
{
  def go(rng:RNG):Double = {
    val count = new Count
		var score = 0d
    val deck = new Deck(2)
    deck.shuffle(rng)

		while(!deck.isEmpty) {
			score += playRound(deck,count,rng)
		}

		score
 }

	class Natural(val score:Double) extends Exception

	def playRound(deck:Deck, count:Count, rng:RNG):Double = {
		var myHand = 0
	  var house = 0

		try {
//			house += deck.drawRandom(rng).n          // face down
			house += count(deck.draw)
			house += count(deck.draw)

//			myHand += deck.drawRandom(rng).n
			myHand += count(deck.draw)
			myHand += count(deck.draw)

			// check for blackjack aka natural
			//			if (myHand == 21)
			//				throw new Natural(3/2d)

			// todo: allow doubling down and splitting

//			while (house < 17) {
//				house += count(deck.draw)
//			}
//
//			while (myHand < 17) {
//				myHand += count(deck.draw)
//			}
		}
		catch {
			case e:DeckEmpty =>
			case n:Natural => return n.score
		}

		// the houses edge
//		if (myHand > 21)
//			return -1
//
//		if (house > 21)
//			return 1
//
//		if (myHand > house)
//			return 1
//
//		if (house > myHand)
//			return -1

		house - myHand
	}

	class Count
	{
		var count = 0
		def apply(card:Card):Int = {
     if (card.n > 9)
       count -= 1
     else if (card.n < 7)
       count += 1
			card.n
		}
	}
}


object CardCountingRunner {
	def main(args:Array[String]) {
		X.loop(6) {
			MC(CardCounting, 1000*1000, verbose=1)
		}
	}
}


object CardCountingCalibrator {
	def main(args:Array[String]) {
		val c = new Calibrator(CardCounting, seq=new ExpSeq)
		c.targetStdev(0.01)
	}
}


object War1 extends SimApp{
	def go(rng:RNG):Double = {
		var score = 0d
		val deck = new Deck(2)
		deck.shuffle(rng)

		while(!deck.isEmpty) {
			val c1 = deck.draw
			val c2 = deck.draw

			if (c1.n > c2.n)
				score += 1
			else if (c1.n < c2.n)
				score -= 1
		}
		score
	}

	MC.watch(this, 2*1000*1000, 20)
}


object War2 extends SimApp{
	def go(rng:RNG):Double = {
		val deck = new Deck(2)
		val c1 = deck.drawRandom(rng)
		val c2 = deck.drawRandom(rng)

		if (c1.n > c2.n)
			return 1
		else if (c1.n < c2.n)
			return -1
		else
			return 0
	}

	MC.watch(this, 2*1000*1000, 20)
}

package x

import test.TestX
import collection.mutable
import m.{RNG, Stats}
import mc.{MC, Sim}

class CardsTest extends TestX
{
  def testShuffle {
    val m = new mutable.HashMap[String,Double]
    for (card <- new Deck) { m(card.toString) = 0 }

    val its = 10000
    for (i <- 0 to its) {
      val deck = new Deck
      deck.shuffle()

      var i = 0
      for (card <- deck) {
        m(card.toString) += i
        i += 1
      }
    }

    println(m)
    val stats = new Stats
    for ((k,v) <- m) {
      stats << v / its
    }
    println(stats)
    assertTrue(stats.min>20)
    assertTrue(stats.max<32)
    assertEquals(52,stats.num)
  }


	def testShuffle2 {
		val sim = new Sim {
			def go(rng:RNG):Double = {
				val deck = new Deck
				deck.shuffle(rng)
				deck.draw.n - deck.draw.n
			}
		}
		val res = new MC(sim, its=100*1000,verbose=1).run
		assertTrue(-0.1 < res.res && res.res < 0.1)
	}


  def testDraw {
    val deck = new Deck
    for (n <- 0 until 52) {
      assertEquals(52-n, deck.size)
      deck.draw
    }
    assertEquals(0, deck.size)
  }


  def testDraw2 {
    val deck = new Deck(4)
    for (n <- 0 until 52*4) {
      assertEquals(52*4-n, deck.size)
      deck.draw
    }
    assertEquals(0, deck.size)
  }


  def testDeck {
    assertEquals(52, new Deck().size)
    assertEquals(52, new Deck(1).size)
    assertEquals(104, new Deck(2).size)
  }
}

package x

import m.RNG
import collection.mutable._

class Suit(val red:Boolean, val name:String)

class Num(val n:Int, val name:String)

class Card(val num:Num, val suit:Suit)
{
  def n = num.n
  override def toString = num.name + suit.name
}

class DeckEmpty extends Exception {
	// no apparent benefit:
	override def fillInStackTrace = null
}


class Deck(decks:Int=1) extends Iterable[Card]
{
	private val cards = new ArrayBuffer[Card](52*decks)

  for (d <- 0 until decks ; suit <- Deck.SUITS ; num <- Deck.NUMS) {
    cards += new Card(num,suit)
  }

  override def toString = X.join(cards, ", ")

  def draw:Card = {
		if (cards.isEmpty)
			throw new DeckEmpty
		cards.remove(0)
	}

	def drawRandom(rng:RNG=RNG):Card = {
		if (cards.isEmpty)
			throw new DeckEmpty
		cards.remove(rng.irand(cards.size-1).toInt)
	}

  def remaining = cards.length

  def shuffle(rng:RNG=RNG) = rng.shuffle(cards)

	def hasCards = ! cards.isEmpty

  def iterator = cards.iterator
}


object Deck
{
  private val SUITS = new ListBuffer[Suit]
  private val NUMS  = new ListBuffer[Num]

  val HEART   = << (new Suit(true,  "H"), SUITS)
  val SPADE   = << (new Suit(false, "S"), SUITS)
  val DIAMOND = << (new Suit(true,  "D"), SUITS)
  val CLUB    = << (new Suit(false, "C"), SUITS)

  val ACE   = << (new Num(11,"A"),  NUMS)
  val TWO   = << (new Num(2,"2"),   NUMS)
  val THREE = << (new Num(3,"3"),   NUMS)
  val FOUR  = << (new Num(4,"4"),   NUMS)
  val FIVE  = << (new Num(5,"5"),   NUMS)
  val SIX   = << (new Num(6,"6"),   NUMS)
  val SEVEN = << (new Num(7,"7"),   NUMS)
  val EIGHT = << (new Num(8,"8"),   NUMS)
  val NINE  = << (new Num(9,"9"),   NUMS)
  val TEN   = << (new Num(10,"10"), NUMS)
  val JACK  = << (new Num(10,"J"),  NUMS)
  val QUEEN = << (new Num(10,"Q"),  NUMS)
  val KING  = << (new Num(10,"K"),  NUMS)

  val JOKER = new Card(new Num(0,"J"), null)


  private def <<[T](t:T, list:ListBuffer[T]):T = {
    list += t
    t
  }
}




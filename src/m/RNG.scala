package m

import scala.math._
import collection.mutable.{ListBuffer,Seq}
import x.X

trait RNG
{
	def nrand():Double
	def rand():Double

	val rngName:String
	val gaussName:String

	final def rand(n:Double):Double = rand * n
	final def rand(n:Double, m:Double):Double = n + rand(m-n)

	// note: range is n to m INCLUSIVE
	final def irand(n:Long, m:Long):Long = n + rand(m-n+1).longValue
	final def irand(m:Long):Long = irand(0,m)

	final def nextLong = rand(Long.MaxValue).toLong

	final def srand(n:Int):String = {
		val bldr = new StringBuilder
		X.loop(n) {
		  bldr.append(irand('A'.toInt,'z'.toInt).toChar)
		}
		bldr.toString
	}

	// see scala.util.Random.shuffle
	final def shuffle[T](list:Seq[T]) {
		def swap(i:Int, j:Int) {
			val tmp = list(i)
			list(i) = list(j)
			list(j) = tmp
		}

		for (n <- list.length to 2 by -1) {
			val k = irand(n-1).toInt
			swap(n-1, k)
		}
	}

	override def toString = "%s w/%s" format (rngName, gaussName)
}


// factory producing the recommended RNG, or can just be used
object RNG extends DefaultRNG(Seeder.next)
{
	def apply():RNG = new DefaultRNG(Seeder.next)
	def apply(seed:Long):RNG = new DefaultRNG(seed)
}


class DefaultRNG(seed:Long) extends RNGMT(seed) with BoxMullerGaussian


// using SafeRNG instead of that passed in to Sim.go
// results in 14 ns/it as opposed to 10
object SafeRNG extends RNG {
  val cache = new ThreadLocal[RNG]() {
    override def initialValue:RNG = new DefaultRNG(Seeder.next)
  }

  def rand  = cache.get.rand
  def nrand = cache.get.nrand

  val rngName = "Safe[" + cache.get.rngName + "]"
  val gaussName = "Safe[" + cache.get.gaussName + "]"
}


// use this to avoid circularity
object Seeder
{
	def next = (java.lang.Math.random * Long.MaxValue).toLong

	def nextBytes(seed:Long):Array[Byte] = {
		val seeds = new Array[Byte](16)
		new java.util.Random(seed).nextBytes(seeds)
		seeds
	}

	def nextBytes:Array[Byte] = nextBytes(next)
}


// pur scala version - equivalent to default std-lib but unsynchronized
abstract class RNGS(private var seed:Long) extends RNG
{
	val rngName = "Scala"
	private val multiplier:Long = 0x5DEECE66DL
	private val addend:Long = 0xBL
	private val mask:Long = (1L << 48) - 1

	seed = (seed ^ multiplier) & mask

	def next(bits:Int):Int = {
		seed = (seed * multiplier + addend) & mask
		(seed >>> (48 - bits)).asInstanceOf[Int]
  }

	def rand:Double = {
		(((next(26)).toLong << 27) + next(27)) / (1L << 53).toDouble
	}
}


/* Mersenne Twissssster */
abstract class RNGMT(seed:Long) extends RNG
{
	private val mt = new MersenneTwisterFast(Seeder.nextBytes(seed))
	def rand = mt.nextDouble
	val rngName = "Mersenne Twister"
}


/* Box-Müller method for log-normally distributed random numbers. */
trait BoxMullerGaussian extends RNG
{
  private var next = Double.NaN
  private val twoPi = 2*Pi
	val gaussName = "Box Müller"

  def nrand:Double = {
    if (!next.isNaN) {
      val ret = next
      next = Double.NaN
      return ret
    }

    val x2=rand
    val term1=sqrt(-2*log(rand))

    next = term1 * cos(twoPi*x2)
    term1 * sin(twoPi*x2)
  }
}


/* See Knuth, ACP, Section 3.4.1 Algorithm C. */
trait KnuthGaussian extends RNG
{
	private var gaussian:Double = Double.NaN
	val gaussName = "Knuth"

	def nrand:Double = {
		if (gaussian == gaussian /* not NaN */) {
			val ret = gaussian
			gaussian = Double.NaN
			ret
		}
		else {
			var v1 = 0d
			var v2 = 0d
			var s = 0d
			do {
				v1 = 2 * rand - 1
				v2 = 2 * rand - 1
				s = v1 * v1 + v2 * v2
			}
			while (s >= 1 || s == 0)

			var multiplier = StrictMath.sqrt(-2 * StrictMath.log(s) / s)
			gaussian = v2 * multiplier
			v1 * multiplier
		}
	}
}


// antithetical decorator
class AntiRNG(real:RNG) extends RNG
{
	var anti = false
	val nums = new ListBuffer[Double]

	def nrand:Double = {
		if (anti) {
			- nums.remove(0)
		}
		else {
			val num = real.nrand
			nums += num
			num
		}
	}

	def flip {
		if (anti) nums.clear
		anti = ! anti
	}

	def rand:Double = real.rand

	val rngName = "-" + real.rngName
	val gaussName = real.gaussName
}

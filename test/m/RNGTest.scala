package m

import collection._
import x.X._
import collection.mutable.Queue
import test.TestX
import gfx.{XFrame, RenderStyle, Graph, Points}
import org.uncommons.maths.random.MersenneTwisterRNG
import x.Speedathlon
import mc.{Sim, MC}


// simply delegate to std-lib - use for testing
private class RNGJ(seed:Long) extends RNG
{
	def this() = this(RNG.nextLong)
	private val rng = new java.util.Random(seed)
	def nrand = rng.nextGaussian
	def rand = rng.nextDouble
	val rngName = "Default Std-lib"
	val gaussName = "Default Std-lib (Knuth)"
}


// std-lib clone allowing introspection - use for testing
private class RNGF(seed:Long) extends RNG
{
	def this() = this(RNG.nextLong)
	private val rng = new RandomFast(seed)
	def nrand = rng.nextGaussian
	def rand = rng.nextDouble
	val rngName = "Fast Std-lib"
	val gaussName = "Default Std-lib (Knuth)"
}


/* For test purposes */
private class RNGMTOriginal(seed:Array[Byte]) extends RNG
{
	def this(seed:Long) = this(Seeder.nextBytes(seed))
	def this() = this(Seeder.nextBytes(Seeder.next))
	private val mt = new MersenneTwisterRNG(seed)
	def nrand = mt.nextGaussian
	def rand = mt.nextDouble
	val rngName = "Original Mersenne Twister"
	val gaussName = "Default Std-lib (Knuth)"
}

private class RNGSBoxMuller(seed:Long)  extends RNGS(seed)  with BoxMullerGaussian
private class RNGSKnuth(seed:Long)      extends RNGS(seed)  with KnuthGaussian
private class RNGMTBoxMuller(seed:Long) extends RNGMT(seed) with BoxMullerGaussian
private class RNGMTKnuth(seed:Long)     extends RNGMT(seed) with KnuthGaussian


class RNGJTest           extends RNGTestSuite(classOf[RNGJ], tol=0.01)
class RNGFTest           extends RNGTestSuite(classOf[RNGF], tol=0.01)
class RNGSBoxMullerTest  extends RNGTestSuite(classOf[RNGSBoxMuller], tol=0.01)
class RNGSKnuthTest 		 extends RNGTestSuite(classOf[RNGSKnuth], tol=0.01)
class RNGMTBoxMullerTest extends RNGTestSuite(classOf[RNGMTBoxMuller], tol=0.01)
class RNGMTKnuthTest     extends RNGTestSuite(classOf[RNGMTKnuth], tol=0.01)

class RNGFRegression     extends RNGRegressionSuite(classOf[RNGJ], classOf[RNGF])
class RNGSRegression     extends RNGRegressionSuite(classOf[RNGF], classOf[RNGSKnuth])
class RNGMTRegression    extends RNGRegressionSuite(classOf[RNGMTKnuth], classOf[RNGMTOriginal])


// tests, basic, basic functionality
abstract class RNGTestSuite(clz:Class[_<:RNG], tol:Double) extends TestX
{
	val it = 100*1000

	def testNrand {
		val rng = newRNG(Seeder.next)
		val stats = new Stats

		for (i <- 0 to it)
			stats << rng.nrand

		assertEquals(0d, stats.avg, tol)
		assertEquals(1d, stats.stdev, tol)
	}

	def testRand {
		val rng = newRNG(Seeder.next)
		val stats = new Stats

		for (i <- 0 to  it) {
			stats << rng.rand
		}
		assertEquals(.5, stats.avg, tol)
		assertEquals(.28, stats.stdev, tol)
		assertEquals(.0, stats.min, tol)
		assertEquals(1., stats.max, tol)
	}

	def testRandArged {
		val rng = newRNG(Seeder.next)
		val stats = new Stats

		for (i <- 0 to  it) {
			stats << rng.rand(-5,10)
		}
		assertEquals(2.5, stats.avg, tol*10)
		assertEquals(4.3, stats.stdev, tol*10)
		assertEquals(-5d, stats.min, tol)
		assertEquals(10d, stats.max, tol)
	}

	def testRandArgedDistribution {
		val rng = newRNG(Seeder.next)
		val map = mutable.Map.empty[Long,Long]
		for (d <- 0 to  it) {
			val x = rng.rand(-2, 5).floor.toLong
			if (!map.contains(x))
				map += (x->0)
				map(x) += 1
		}
		for (n <- -2 to 4) {
      printf("%2d %d%n", n,  map(n))
      assertTrue(map(n) > 13000)
      assertTrue(map(n) < 15000)
    }
	}

	def testIRand1 {
		val rng = newRNG(Seeder.next)
		val stats = new Stats
		for (d <- 0 to it)
			stats << rng.irand(2,5)
		assertEquals(5, stats.max)
		assertEquals(2, stats.min)
	}

	def testIRand2 {
		val rng = newRNG(Seeder.next)
		val stats = new Stats

		for (d <- 0 to it)
			stats << rng.irand(5)

		assertEquals(5, stats.max)
		assertEquals(0, stats.min)
	}

	def testIRand3 {
		val rng = newRNG(Seeder.next)
		val stats = new Stats
		for (d <- 0 to it)
			stats << rng.irand(-2, 5)
		assertEquals(5, stats.max)
		assertEquals(-2, stats.min)
	}

	def testIRandGetsLower {
		val rng = newRNG(Seeder.next)
		for (d <- 1 to 100) {
			if (rng.irand(0,1) == 0)
				return
		}
		fail()
	}

	def testIRandGetsUpper {
		val rng = newRNG(Seeder.next)
		for (d <- 1 to 100) {
			if (rng.irand(0,1) == 1)
				return
		}
		fail()
	}

	def testIRandGetsZero {
		val rng = newRNG(Seeder.next)
		for (d <- 1 to 100) {
			if (rng.irand(1) == 0)
				return
		}
		fail()
	}

	def testIRandGetsOne {
		val rng = newRNG(Seeder.next)
		for (d <- 1 to 100) {
			if (rng.irand(1) == 1)
				return
		}
		fail()
	}

	def testIRandSilly {
		val rng = newRNG(Seeder.next)
		for (d <- 1 to 100) {
			assertEquals(0, rng.irand(0,0))
			assertEquals(1, rng.irand(1,1))
			assertEquals(-2, rng.irand(-2,-2))
		}
	}

  def testDistribution {
		val rng = newRNG(Seeder.next)
    val stats = new Stats
    for (i <- 0 to it)
      stats << rng.nrand
    assertEqualsV(0d, stats.avg, 0.01)
    assertEqualsV(1d, stats.stdev, 0.01)
  }

	def testSameSeed {
		val seed = Seeder.next
		val r1 = newRNG(seed)
		val r2 = newRNG(seed)

		loop(1000) {
			assertEquals(r1.nrand, r2.nrand, 0.00000000001)
			assertEquals(r1.rand, r2.rand, 0.00000000001)
		}
	}

	def testSRand {
		val rng = newRNG(Seeder.next)
		assertEquals(0, rng.srand(0).size)
		assertEquals(25, rng.srand(25).size)
		val str = rng.srand(10000)
		assertTrue(str.contains("A"))
		assertTrue(str.contains("Z"))
		assertTrue(str.contains("a"))
		assertTrue(str.contains("z"))
	}

	def testShuffle {
		val sim = new Sim {
			def go(rng:RNG):Double = {
				val list = mutable.MutableList(1,2,3,4,5)
				rng.shuffle(list)
				list(0) - list(1)
			}
		}
		val res = MC(sim, 4*1000*1000)
		assertEquals(0, res.res, 0.004)
	}

	private def newRNG(seed:Long) =
		clz.getConstructor(classOf[Long]).newInstance(seed.asInstanceOf[AnyRef])
}


// compare our un-synchronized RNG against the default
abstract class RNGRegressionSuite(rngA:Class[_<:RNG], rngB:Class[_<:RNG]) extends TestX
{
	def res = 0.00001

	assertTrue(rngA != rngB)

	def testSeeding {
		for (i <- 1 to 1000) {
			val seed = RNG.nextLong
			val rng1 = NewRNG(rngA,seed)
			val rng2 = NewRNG(rngB,seed)
			for (j <- 1 to 10000) {
				assertEquals(rng1.rand, rng2.rand, res)
				assertEquals(rng1.nrand, rng2.nrand, res)
			}
		}
	}

	def testRand {
		val seed = RNG.nextLong
		val rng1 = NewRNG(rngA,seed)
		val rng2 = NewRNG(rngB,seed)
		for (i <- 1 to 1000*1000) {
			assertEquals(rng1.rand, rng2.rand, res)
		}
	}

	def testNRand {
		val seed = RNG.nextLong
		val rng1 = NewRNG(rngA,seed)
		val rng2 = NewRNG(rngB,seed)
		for (i <- 1 to 1000*1000) {
			assertEquals(rng1.nrand, rng2.nrand, res)
		}
	}
}


object SpeedRun
{
	def main(args:Array[String]) {
    val speedathlon = new Speedathlon[RNG]

		speedathlon.contenders += new RNGJ
		speedathlon.contenders += new RNGF
		speedathlon.contenders += new RNGSBoxMuller(Seeder.next)
		speedathlon.contenders += new RNGSKnuth(Seeder.next)
		speedathlon.contenders += new RNGMTBoxMuller(Seeder.next)
		speedathlon.contenders += new RNGMTKnuth(Seeder.next)
		speedathlon.contenders += new RNGMTOriginal(Seeder.next)

		// warm up
		loop(5000*1000) {
			for (rng <- speedathlon.contenders) {
				rng.rand
				rng.nrand
			}
		}

		println("======== RAND =========")
		speedathlon.compete() { rng =>
			loop (1000*1000*10) { rng.rand }
		} foreach  println

		println("======== NRAND ========")
		speedathlon.compete() { rng =>
			loop (1000*1000*10) { rng.nrand }
		} foreach  println
	}
}


private object NewRNG
{
	def apply[T<:RNG](clz:Class[T]):T
	  = apply(clz:Class[T], RNG.nextLong)

	def apply[T<:RNG](clz:Class[T], seed:Long):T
		= clz.getConstructor(java.lang.Long.TYPE).newInstance(seed.asInstanceOf[AnyRef])
}


class AntiRNGTest extends TestX
{
	def testSymmetry {
		val q = new Queue[Double]
		val rng = new AntiRNG(RNG())
		loop(1000) {
			loop(100) { q.enqueue(rng.nrand) }
			rng.flip
			loop(100) { assertEquals(-q.dequeue, rng.nrand) }
			rng.flip
		}
	}

	def testSymmetry2 {
		val q = new Queue[Double]
		val rng = new AntiRNG(RNG())
		loop(1000) {
			loop(100) { q.enqueue(rng.nrand) }
			rng.flip
			loop(50) { assertEquals(-q.dequeue, rng.nrand) }
			rng.flip
			q.clear
		}
	}

	def testToString {
		val rng = new AntiRNG(new RNGMT(Seeder.next) with BoxMullerGaussian)
		assertEqualsS("-Mersenne Twister w/Box Müller",rng)
	}

	def XtestSpeed {
		val rng = new AntiRNG(RNG())
		val n = 1000*1000

		loop(5) {
			println("black tetris")
			loop(n) { rng.nrand }
			rng.flip
			loop(n) { rng.nrand }
			rng.flip
		}
	}
}


object RNGVisualizer
{
	def main(args:Array[String]) {
		val points = new Points
		val rng = RNG()
		for (n <- 1 to 100*1000)
			points += (n, rng.rand)
		XFrame.app = "RNGVisualizer"
		new Graph("Distribution for " + rng.toString.replace("ü","ue"), points, RenderStyle.POINTS)
	}
}
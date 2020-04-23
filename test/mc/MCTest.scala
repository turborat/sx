package mc

import test.TestX
import java.util.concurrent.atomic.AtomicInteger
import x.X._
import collection.mutable
import m.{AntiRNG, Seeder, Stats, RNG}
import x.{WaitFor, X, Disaster}
import java.util.concurrent.TimeUnit

class MCTest extends TestX
{
	def testIterations {
		for (jobs <- List(0, 1, 4, 8, 16, 32)) {
			val sim = new Sim {
				val c = new AtomicInteger
				def go(rng: RNG) = c.incrementAndGet
			}

			MC(sim, its=32, jobs=jobs, verbose=1)
			assertEquals(32, sim.c.get)
		}
	}

	def testResults {
		for (jobs <- List(0, 1, 4, 8, 16, 32)) {
			val sim = new Sim {
				def go(rng: RNG) = 123
			}

			val result = MC(sim, its=32, jobs=jobs, verbose=1)
			assertEquals(123, result.res)
			assertEquals(32,  result.its)
			assertEquals(sim, result.sim)
			assertTrue(result.elapsed > 0)
		}
	}

	// ========================================================================= //
	def testSameResults {
		val seed0 = Seeder.next

		class TestSim extends Sim
		{
			override def go(rng:RNG) = rng.rand
		}

		// Note: using different job counts affect output
		for (jobs <- List(0, 1, 4, 8, 16, 32)) {
			val stats = new Stats
 			loop (100) {
				stats << new MC(new TestSim, its=32, seed=seed0, verbose=0) {
					nJobs = jobs
				}.run.res
			}
			assertEquals(0d, stats.stdev, 0.00000000001)
		}
	}


	def testSameRNG {
		loop (100) {
			var bad = false
			val sim = new Sim
			{
				val rngs = mutable.Map[Thread,Long]()

				def go(rng: RNG):Double = {
					if (bad) return 0.

					val myTid = Thread.currentThread
					val myRngId = System.identityHashCode(rng)

					synchronized {
						if (!rngs.contains(myTid)) {
							rngs(myTid) = myRngId
						}
						else {
							val rngId = rngs.get(myTid).get
							if (myRngId != rngId) {
								printf("Thread %s got rng %d instead of %d %n", myTid, myRngId, rngId)
								bad = true
							}
						}

						0
					}
				}
			}

			// we rely on go() not returning immediately
			MC(sim, its=8*20000, jobs=8, verbose=0)
			assertFalse(bad)
		}
	}

	def testUniqRNG {
		loop (100) {
			var bad = false
			val sim = new Sim
			{
				val rngs = mutable.Map[Long,Thread]()

				def go(rng: RNG):Double = {
					synchronized {
						val myTid = Thread.currentThread
						val myRngId:Long = System.identityHashCode(rng)

						if (!rngs.contains(myRngId)) {
							rngs(myRngId) = myTid
						}
						else {
							val tid = rngs.get(myRngId).get
							if (myTid != tid) {
								printf("Rng %d is being used by two threads: %s and $s %n", myRngId, myTid, tid)
								bad = true
							}
						}

						0
					}
				}
			}

			MC(sim, its=8*256, jobs=256, verbose=0)
			assertFalse(bad)
		}
	}

	def testAntiRNG {
		class Stuff {
			var anti = false
			val q = new mutable.Queue[Double]
		}

		val locals = new ThreadLocal[Stuff] {
			override def initialValue = new Stuff
		}

		val sim = new Sim {
			def go(rng: RNG):Double = {
				val stuff = locals.get

				loop(1000) {
					if (stuff.anti)
						assertEquals(-stuff.q.dequeue, rng.nrand)
					else
						stuff.q.enqueue(rng.nrand)
				}

				if (stuff.anti) {
					assertTrue(stuff.q.isEmpty)
					assertTrue(rng.asInstanceOf[AntiRNG].nums.isEmpty)
				}
				else {
					assertEquals(1000, stuff.q.size)
					assertEquals(1000, rng.asInstanceOf[AntiRNG].nums.size)
				}

				stuff.anti = ! stuff.anti
				0
			}
		}

		MC(sim, 8000, jobs=8)
	}

	def testEx {
		try {
			new MC(new Sim { def go(rng:RNG)=0 } , 0)
			fail()
		}
		catch {
			case e:Disaster =>
			case _ => fail()
		}
	}

	def testTimeout {
		val sim = new Sim {
			def go(rng:RNG) = 1
		}
		val done = new WaitFor[Boolean]
		val asserted = new WaitFor[Boolean]
		X.schedule(2000) {
	  	assertTrue(done.get)
			asserted.set(true)
		}
		new MC(sim, Long.MaxValue, 123, 1000, 1)
		done.set(true)
		asserted.get(1000, TimeUnit.MILLISECONDS)
	}
}

class SimTest extends TestX
{
	def testName {
    class MySim extends Sim {
      def go(rng:RNG) = 0
    }

    assertEquals("MySim$1", new MySim().name)
		assertEquals("MySim$1", new MySim(){}.name)
		class MySim2 extends MySim
		assertEquals("MySim2$1", new MySim2().name)
		assertEquals("MySim2$1", new MySim2(){}.name)
	}
}

object MCSpeedTest
{
	def main(args:Array[String]) {
		val sim = new Sim {
      def go(rng:RNG) = 0
    }

		val it = 10l*1000*1000*1000

		loop (5){
			ptime("%,d iterations" format it) {
				MC(sim,it,verbose=0)
			}
		}
	}
}
package mc

import m.Math._
import x.{X, ScatterGather, Disaster}
import x.X._
import m._
import scala.collection.mutable.ListBuffer

trait Sim {
	var name = X.objName(this)
	def go(rng:RNG):Double
}

abstract class SimApp extends Sim {
	var its = 10*1000*1000
	var verbose = 1
	var jobs = MC.JOBS

  def main(args:Array[String]) {
		val mc = new MC(this, its=its, verbose=verbose)
		mc.nJobs = jobs
	  mc.run
	}
}

private class MCJob(sim:Sim, its:Long, verbose:Boolean, rng:RNG) {
	@volatile
	var go = true

	def run:MCResult = {
		val antiRng = new AntiRNG(rng)
		val result = new MCResult1(sim)

	 result.elapsed = X.timeit {
		 var it = 0l
			while (go && it < its) {
				result << sim.go(antiRng)
				antiRng.flip
				it += 1
			}
		}

		if (verbose)
				println(result)

		result
	}

	def stop = go = false
}

class MC(sim:Sim, its:Long, seed:Long=Seeder.next, timeout:Long=10*1000, verbose:Int=1)
{
	var nJobs = MC.JOBS

	Disaster If (its == 0, "Can't have 0 its fool")

	private def rng(seed:Long) = new RNGMT(seed) with BoxMullerGaussian

	def run:MCResult = {
		if (nJobs > 0)
			runN(its)
		else
			new MCJob(sim, its, verbose>0, rng(seed)).run
	}

  private def runN(its:Long):MCResult = {
		Disaster Unless (divisible(its,nJobs),
				"Iterations (%,d) not divisible by jobs (%,d)" format (its, nJobs))

		val rootRNG = rng(seed)
		val results = new MCResultN(sim)
		val jobs = new ListBuffer[MCJob]

		val elapsed = timeit {
      val sg = new ScatterGather[MCResult]
      for (i <- 1 to nJobs) {
				val threadRNG = rng(rootRNG.nextLong)
				val job = new MCJob(sim, its/nJobs, false, threadRNG)
				jobs += job

				sg.submit {
					job.run
        }
      }

			X.schedule(timeout) {
				for (job <- jobs) {
					job.stop
				}
			}

      for (fut <- sg.await) {
				results << fut
      }
    }

		results.elapsed = elapsed

		verbose match {
			case 1 => println(results)
			case 2 => println(results.details)
		  case _ =>
		}

		results
  }
}


object MC
{
  var JOBS = Runtime.getRuntime.availableProcessors
	var TIMEOUT = 2000
	var ITS = Long.MaxValue
	var VERBOSE = 1

  def apply(sim:Sim, its:Long, jobs:Int=JOBS, verbose:Int=VERBOSE):MCResult = {
    new MC(sim, its, verbose=verbose) { nJobs=jobs } run
  }

  def apply(f: => Double):MCResult = {
		new MC(new Sim { def go(rng:RNG):Double = f }, its=ITS, timeout=TIMEOUT).run
  }

	def watch(sim:Sim, its:Long, n:Int=100) {
		val stats = new Stats
		for(i <- 1 to n) {
			val res = new MC(sim, its, verbose=0).run
			stats << res.res
			printf("(%d/%d) %,9.6f  %s%n", i, n, stats.avg, res)
		}
		printf("σ:%,.6f%n", stats.stdev)
	}
}
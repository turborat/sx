package mc

import m.Stats
import x.X._
import collection.mutable.ListBuffer
import x.Disaster

class Calibrator(sim:Sim, maxSecs:Int=10, seq:Iterable[Int]=new LinSeq(50*1000))
{
	var verbose = 0
	var tests = 30

	def targetStdev(stdev:Double):List[Result]
		= target("Target deviation reached") { _.stdev <= stdev }

	def targetTime(time:Int):List[Result] // UNTESTED
		= target("Target time reached") { _.time >= time }

	def targetIter(iter:Long):List[Result]
		= target("Target iterations reached") { _.iter >= iter }

	def target(msg:String)(test:(Result) => Boolean):List[Result] = {
		val results = new ListBuffer[Result]
		for (iter <- seq) {
			val result = testAt(iter)
			println(result)
			results += result

			if (test(result))
				return results.toList
		}

		Disaster("should never happen - make compiler shut-up")
	}

	private def testAt(iter:Int):Result = {
		val resultStats, timeStats = new Stats

		for (n <- 1 to tests) {
			val result = new MC(sim,iter, verbose=verbose).run
			resultStats << result.res
			timeStats << result.elapsed
		}

		new Result(iter, resultStats.stdev, timeStats.avg.toLong)
	}

	class Result(val iter:Long, val stdev:Double, val time:Long)
	{
		override def toString
		  = "%,d iterations - std-dev:%,f time:%s" format (iter, stdev, prettyTime(time))
	}
}

class ExpSeq extends Iterable[Int]
{
	def iterator = new Iterator[Int]
	{
		var pow = 5
		def hasNext = true
		def next = { pow += 1 ; 2<<pow }
	}
}

class LinSeq(step:Int) extends Range(step, Integer.MAX_VALUE, step)

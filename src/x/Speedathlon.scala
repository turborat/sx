package x

import collection.mutable.ListBuffer

// rank runtimes of multiple implementations with like signatures
class Speedathlon[T<:AnyRef]
{
	val contenders = new ListBuffer[T]

	def compete()(f:(T) => Unit):List[Result] = {
		val results = new ListBuffer[Result]
		var best = Long.MaxValue

		for (t <- contenders) {
			val elapsed = X.timeit { f(t) }
		  results += new Result(t.toString, elapsed)
			if (elapsed < best)
				best = elapsed
		}

		for (r <- results)
			r.score = r.elapsed.toDouble / best

		results sortBy { _.score } toList
	}
}

// compare completed different implementations
class SpeedathlonX
{
	private class CompetitionType(name:String, f:() => Unit) {
		override def toString = name
		def fun = f()
	}

	private val real = new Speedathlon[CompetitionType]

	def contend(name:String)(f: => Unit) {
		val f2 = () => f // ha ha
		real.contenders += new CompetitionType(name, f2)
	}

	def compete:List[Result] = {
		val results = real.compete() { _.fun }
    results foreach println
    results
  }

	def warmUp
		= real.compete() { _.fun }
}


class Result(val id:String, val elapsed:Long)
{
	var score = 0d
	override def toString = "%.2f - %6s - %s" format (score, X.prettyTime(elapsed), id)
}

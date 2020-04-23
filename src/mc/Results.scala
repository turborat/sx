package mc

import collection.mutable.ListBuffer


trait MCResult
{
	def res():Double
	def elapsed():Long
	def its():Long
	def sim():Sim

	override def toString
	  = "%s[its:%,d secs:%,.2f ns/it:%,d] -> %,9.6f" format
			(sim.name, its, elapsed/1000d/1000/1000, elapsed/its, res)
}


class MCResult1(val sim:Sim) extends MCResult
{
	def <<(res:Double) {
		sum += res
		its += 1
	}
	def res = sum / its
	var elapsed = 0l
  var its = 0l
	private var sum = 0d
}


class MCResultN(val sim:Sim) extends MCResult
{
	var elapsed:Long = 0
	var its:Long = 0
	var res:Double = 0

	private
	val jobs = ListBuffer[MCResult]()

	def << (result:MCResult) {
		jobs += result
		its += result.its
		res = ((jobs.length-1) * res + result.res) / jobs.length
	}

	override def toString():String = {
		super.toString + "\t(" + jobs.length + " jobs)"
	}

	def details():String = {
		var ret = toString
		for (res <- jobs)
			ret += "\n " + res.toString
		ret
	}
}

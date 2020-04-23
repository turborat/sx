package m

import collection.mutable.ListBuffer
import scala.math._


class Stats
{
	var min = Double.NaN
	var max = Double.NaN
	var sum = 0D
	var nums = new ListBuffer[Double]

	def tally(num:Double):Stats = {
		if (!(min < num)) min = num
		if (!(max > num)) max = num
		nums += num
		sum += num
		this
	}

	def tally(col:Iterable[Double]):Stats = {
	  val it = col.iterator
	  while (it.hasNext) this << it.next
	  this
	}

	def << (col:Iterable[Double]):Stats = tally(col)
	def << (num:Double):Stats = tally(num)

	def avg() = sum / num
	def num() = nums.length

	def stdev():Double = {
		var sqrsum = 0D
		nums.foreach((n:Double) => sqrsum += (n-avg) * (n-avg))
		sqrt(sqrsum/num)
	}

	override def toString:String = {
		"[num:" + num +
    " sum:" + sum +
		" min:" + min +
		" max:" + max +
		" avg:" + avg +
		" stdev:" + stdev +
		"]"
	}

	def print(dp:Int=6):String = {
		val fmt = "%,." + dp + "f"
		val str = "[num:%,d" +
			" sum:" + fmt +
			" min:" + fmt +
			" max:" + fmt +
			" avg:" + fmt +
			" stdev:" + fmt +
			"]" format (num, sum, min, max, avg, stdev)
		println(str)
		str
	}
}

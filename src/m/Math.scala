package m

import collection.mutable.ListBuffer
import gfx.Coord
import scala.math._

object Math
{
	def sum[T](list:List[T], f:(T) => Double):Double = sum(dlist(list,f))
	def sum(list:List[Double]) = {
		var sum = 0d
		for (d <- list)
			sum += d
		sum
	}

	def avg[T](list:List[T])(f:(T) => Double):Double = avg(dlist(list,f))
	def avg(list:List[Double]) = sum(list) / list.length

	// returns a list of doubles
	def dlist[T](list:List[T], f:(T) => Double):List[Double] = {
		var dList = new ListBuffer[Double]
		for (l <- list)
			dList = dList += f(l)
		dList.result
	}

	def divisible(num:Long, den:Long):Boolean = {
		val div = num.toDouble / den
		div == div.toLong
	}

	// derivative by central-difference
	def D(x:Double, scale:Double, f:(Double) => Double):Double = {
		val nudge = 1/scale // error = O(nudge^2)
		(f(x+nudge) - f(x-nudge)) / 2 * scale
	}

	// derivative by central-difference
	def D(x:Double, f:(Double) => Double):Double
		= D(x, 10*1000d, f)

	// linear interpolation
	def interpolate(p1:(Double,Double), p2:(Double,Double), x:Double):(Double,Double) = {
		assert (p1._1 <= x)
		assert (x <= p2._1)
		val dY = p2._2 - p1._2
		val dX = p2._1 - p1._1
		(x, p1._2 + (x - p1._1) * dY / dX)
	}

  def interpolate(p1:Point, p2:Point, x:Double):Point
    = new Point(interpolate((p1.x,p1.y), (p2.x,p2.y), x))

	def cmp(v1:Double, v2:Double, tol:Double):Boolean
	  = abs(v1-v2) <= tol

	//@tailrec
	def factorial(n:Long):Long
		= if (n < 2) 1 else n * factorial(n-1)

	def table(start:Long, end:Long, inc:Long, f:(Long)=>AnyVal)
		= for (n <- start to end by inc) yield (n, f(n))

	def table(start:Double, end:Double, inc:Double, f:(Double)=>AnyVal)
		= for (n <- start to end by inc) yield (n, f(n))
}


class Point(var x:Double, var y:Double) extends Comparable[Point]
{
	def this(coord:Coord) = this(coord.x, coord.y)
	def this(t:(Double,Double)) = this(t._1, t._2)
	override def toString = "("+x+","+y+")"
	override def compareTo(o: Point) = if (x == o.x) 0 else if (x > o.x) 1 else -1
	override def equals(that:Any) = that match {
		case that: Point => this.x == that.x && this.y == that.y
		case _ => false
	}
	override def hashCode = throw new UnsupportedOperationException
}

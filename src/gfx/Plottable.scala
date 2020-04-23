package gfx

import m.Point
import m.Math._
import x.{X, MapX}

// note: iterator must be sequential
trait Plottable extends Iterable[Point]
{
	def x0:Double
	def xN:Double
	def y0:Double
	def yN:Double
	def apply(x:Double):Double
	def xSteps(n:Int) {}
  final override def toString = "{" + X.join(this, ", ") + "}"
}


class Points extends Plottable
{
	// map that performs interpolation, a bit too clever
	private val data = new MapX[Double,Double] {
		override def apply(x:Double):Double = {
    val y:Any = _map get x    // type:Any to avoid auto-boxing a null to 0.0
    if (y == null)
      interpolate(floorEntry(x), ceilEntry(x), x)._2
    else
      y.asInstanceOf[Double]
	  }
	}

	var x0 = Double.NaN
	var xN = Double.NaN
	var y0 = Double.NaN
	var yN = Double.NaN

	def += (x:Double, y:Double) {
		data += (x,y)
		if (!(x0<x)) x0 = x
		if (!(xN>x)) xN = x
		if (!(y0<y)) y0 = y
		if (!(yN>y)) yN = y
	}

	def += (t:(Double,Double)):Unit
		= += (t._1, t._2)

	def iterator = new Iterator[Point] {
		private val it = data.iterator
		def hasNext = it.hasNext
		def next() = toPoint(it.next)
	}

  // used to position cross-hairs
	def apply(x:Double):Double
		= data(x)

	private def toPoint(e:(Double,Double)):Point
		= new Point(e._1, e._2)

	override def size
	  = data.size
}


class Function(val x0:Double, val xN:Double, f:(Double) => Double) extends Plottable
{
	var y0 = Double.NaN
	var yN = Double.NaN

	private var xSteps = 0
	private var dx = 0.

	override def xSteps(pixels:Int) {
		xSteps = pixels
		dx = (xN-x0) / pixels
		for (n <- 0 until pixels) {
			val y = f(x0+dx*n)
			if (!(y0<y)) y0 = y
			if (!(yN>y)) yN = y
		}
	}

	def iterator = new Iterator[Point] {
		assert (xSteps > 0)
		assert (y0 * yN != Double.NaN)
		private var step = 0

		def hasNext = step <= xSteps

		def next():Point = {
			val x = x0+dx*step
			val y = f(x)
			step += 1
			new Point(x,y)
		}
	}

	def apply(x:Double) = f(x)
}


object Plottable
{
  def apply(tm:MapX[Double,Double]) = new Points { tm foreach { += _ } }
  def asFunc(tm:MapX[Double,Double]) = new Function(tm.min._1, tm.max._1, {
    d =>
    0.
  })
}
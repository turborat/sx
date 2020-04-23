package sim

import mc.{MC, Sim}
import m.RNG

abstract class Integrator(x0:Double, xN:Double, steps:Int) extends Sim {
  def this(x0:Double, xN:Double) = this(x0, xN, 10000001)

  var y0 = Double.MaxValue
  var yN = Double.MinValue
  var vol = 0d
  var ltz = false

  {
    for (step <- 0 to steps) {
      val x = x0+step.asInstanceOf[Double]/steps*(xN-x0)
      val y = f(x)
      if (y < y0) y0 = y
      if (y > yN) yN = y
    }

    if (y0 > 0) y0 = 0

    if (yN < 0) {
      yN = 0
      ltz = true
    }

    vol = (xN-x0) * (yN-y0)
    printf("domain:{%f,%f} range:{%f,%f} volume:%f%n", x0, xN, y0, yN, vol)
  }

  def f(x:Double) :Double
  def fname() :String

  override def go(rng:RNG) :Double = {
    val x = rng.rand(x0, xN)
    val y = rng.rand(y0, yN)
    if (y < f(x) || ltz) vol else 0
  }

  name = "Integrate{" + x0 + ":" + xN + " " + fname + "}" ;
}

object Runner {
  def main(args: Array[String]) {
    val sim = new Integrator(0,10) {
      def f(x:Double) = -5
      def fname() = "-5"
    }
    MC(sim, 50*1000*1000,1)
  }
}

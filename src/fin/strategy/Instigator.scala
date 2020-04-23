package fin.strategy
import java.io.File
import scala.collection.mutable.ListBuffer
import x.X
import x.TS
import x.Matrix
import x.ScatterGather
import x.CSV
import x.MatrixBuilder
import gfx.TableData
import m.Optimizer

object Instigator
{
  val file = "SX/src/fin/strategy/bb1/Ticks.csv"
  val signals = new ListBuffer[Tick]

  X.onLines(file,ignoreHeader=true) { line =>
    val toks = line.split(",")
    val ts = TS.fromExcel(java.lang.Double.parseDouble(toks(0)))
    val price = java.lang.Double.parseDouble(toks(1))
    val tick = new Tick(price, ts)
    signals.+=:(tick)
  }

  def main(args: Array[String]) {
//    val model = quickie(31,1.4,2.2)
//    doMat(model)
    doit
  }

  def doit {
    val best = X.ptime("Optimization") {
      optimize
    }
    val model = new BBModel(best(0).intValue, best(1), best(2))
    model.consume(signals)
		model.positions foreach println
//    println(model)
  }

  def quickie(win:Int, zDown:Double, zUp:Double):Model = {
    val model = new BBModel(win, zDown, zUp)
    signals foreach { tick =>
      tick -> model
      println(model.bands)
    }
    println(model.positions.mkString("\n"))
    println(model)
    model
  }

  def doMat(model:Model) {
    var long = false
    var sellAt = 0
    val mb = new MatrixBuilder[String]
    for (tick <- signals) {
      mb += tick.ts.toString
      mb += tick.price.toString
//      if (model.positions(0).ts == tick.ts) {
//        long = true
//      }
      mb.newRow
//      println(tick)
    }

		val mat = mb.toMatrix
		new gfx.Table("Ticks",mat,true)
    println(mat)
//    CSV(mat, "xx.csv").write.open
  }

  def optimize:Array[Double] = {
    val jobs = 8
    val opts = ScatterGather.stripe(0 until jobs) { n =>

      val step = 300d / jobs
      val n0 = n*step
      val n1 = (n+1)*step
      val points = 20d/jobs

      val window = new Optimizer.Parameter(1+n0,1+n1,points)
      println(window)

      val sdDown = new Optimizer.Parameter(1,5,10)
      val sdUp   = new Optimizer.Parameter(1,5,10)

      val opt = new Optimizer(window,sdDown,sdUp) {
        def func(args:Array[Double]):Double = {
          val model = new BBModel(args(0).intValue, args(1), args(2))
          signals foreach { _ -> model }
          model.pl // model.positions.size
        }
      }
      opt.optimize()
      opt
    }

    var best:Optimizer = null

    for (opt <- opts) {
      if (best == null || best.max < opt.max) {
        best = opt
      }
    }

    println(best.optimalParams().mkString(" ") + " => " + best.max)
    best.optimalParams()
  }
}
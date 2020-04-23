package fin.strategy

import scala.collection.mutable.ListBuffer
import x.DoubleWin
import m.Stats

// Simple Bolling-Band model
class BBModel(window:Int, zDown:Double, zUp:Double) extends Model
{
  val tickWin = new DoubleWin(window)
  val positions = new ListBuffer[Position]
  var openPos:Option[Position] = Option(null)
  var lastTime = 0l
  var pl = 0d

  var stats:Stats = null
  var stDev = 0d
  var bandDown, bandUp = 0d

  override def receive (tick:Tick) {
//    println("Received " + tick)
    assert (lastTime <= tick.ts.ms)
    lastTime = tick.ts.ms
    tickWin += tick.price
    stats = new Stats() << tickWin
    stDev = stats.stdev
    bandDown = tickWin.avg - stDev * zDown
    bandUp = tickWin.avg + stDev * zUp

    if (openPos.isEmpty) {
      if (tick.price <= bandDown) {
        openPos = Option(new Position(tick))
      }
    }
    else { // we are long
      if (tick.price >= bandUp) {
        openPos.get.close(tick)
        pl += openPos.get.pl
        positions += openPos.get
        openPos = Option(null)
      }
    }
  }

  def bands =
    "avg:%f stdev:%f bDown:%f bUp:%f" format
    (stats.avg, stDev, bandDown, bandUp)

  override def toString
    = "[win:%d z:%.3f/%.3f trades:%d pl:%.2f%%" format
      (window, zDown, zUp, positions.length, pl)
}


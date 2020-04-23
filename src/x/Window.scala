package x

class Window[T:Manifest](len:Int) extends Iterable[T]
{
  val arr = new Array[T](len)
  var full = false
  var idx = 0

  def += (t:T) {
    arr(idx) = t
    idx += 1

    if (idx == len) {
      full ||= true
      idx = 0
    }
  }

  private def index(i:Int):Int = {
    if (full)
      (i + idx) % len
    else
      i
  }

  def iterator = new Iterator[T] {
    var rangeIt = (0 until {if (full) len else idx}).iterator
    def hasNext = rangeIt.hasNext
    def next:T = arr(index(rangeIt.next))
  }

  override def toString
    = "[" + X.join(this, ", ") + "]"
}


// a window that calculates a moving average
class DoubleWin(len:Int) extends Window[Double](len)
{
  var avg = 0d

  override def += (d:Double) {
    if (full) {
      val old = arr(idx)
      avg += (d-old)/len
    }
    else {
      avg = (idx*avg+d)/(idx+1)
    }

    super.+=(d)
  }
}
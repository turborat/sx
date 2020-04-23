package x

import management.{GarbageCollectorMXBean, ManagementFactory}
import collection.JavaConversions._
import java.util.concurrent.atomic.AtomicLong


class Delta(var prev:Long) {
  var delta = prev
  def tally(curr:Long) {
    delta = curr - prev
    prev = curr
  }
}


class CollectionStats(bean:GarbageCollectorMXBean) {
  val time = new Delta(bean.getCollectionTime)
  val count = new Delta(bean.getCollectionCount)

  def update {
    time.tally(bean.getCollectionTime)
    count.tally(bean.getCollectionCount)
  }

  override def toString =
    "%s %,d/%,d %,d/%,d ms" format (bean.getName, count.delta, count.prev, time.delta, time.prev)
}





object GC extends App {
  val maxPauseAL = new AtomicLong(0)
  var t0 = System.nanoTime
  var maxPause = 0l

  X.thread("pause-calc") {
    val t1 = System.nanoTime
    val delta = t1 - t0
    if (delta > maxPause)
      maxPauseAL.set(delta)
    t0 = t1
  }

  val stats = for (bean <- ManagementFactory.getGarbageCollectorMXBeans)
  yield new CollectionStats(bean)

  X.thread(interval=1000) {
    for (s <- stats) s.update
    printf("%s %s %s %,d %n", TS.now, stats(0), stats(1), maxPauseAL.getAndSet(0))
  }
}

package x

import scala.actors.Futures.future
import scala.actors.Future
import collection.mutable.ListBuffer
import x.X._
import m.Math._
import java.util.concurrent.{TimeUnit, CountDownLatch}


class ScatterGather[Y]
{
	private val futz = ListBuffer[Future[Y]]()

	def submit(f: => (Y)) = synchronized {
		futz += future(f)
	}

	def await:List[Y] = synchronized {
		val results = makeList(futz) { f => f() }
		futz.clear
		results
	}
}

object ScatterGather
{
	def stripe[T,V](list:List[T], strips:Int)(f:(T) => V):List[V] = {
		val sg = new ScatterGather[List[V]]

		for (range <- stripes(list.size, strips)) {
			val sublist = list.slice(range._1, range._2)
			sg.submit {
				printf("Stripe %s on %s %n", range, Thread.currentThread.getName)
				makeList(sublist) { f(_) }
			}
		}

		val ret = new ListBuffer[V]
		for (l <- sg.await)
			ret.appendAll(l)
		ret.toList
	}

	def stripe[T,V](i:Iterable[T], strips:Int=8)(f:(T) => V):List[V]
	  = stripe(toList(i), strips)(f)

	// loop, not over anything
	def sloop[T,V](loops:Int, strips:Int=8)(f: => V) {
		Disaster Unless (divisible(loops,strips),
						"loops (%,d) not divisible by strips (%,d)" format (loops, strips))
		val loopsPerStrip = loops / strips
		stripe(0 until strips, strips) { x => loop(loopsPerStrip) { f } }
	}

	def stripes(items:Int, strips:Int):List[(Int,Int)] = {
		val base = items / strips
		val xtra = items % strips

		val lbr = new ListBuffer[(Int,Int)]
		var n0 = 0
		for (i <- 0 until strips) {
			val n = if (i < xtra) base+1 else base
			val nN = n0 + n
			if (n0 < nN)
				lbr += ((n0, nN))
			n0 = nN
		}
		lbr.toList
	}
}

class WaitFor[T]
{
	private var t:T = _
	private val latch = new CountDownLatch(1)

	def set(t:T) {
		Disaster If latch.getCount == 0
		this.t = t
		latch.countDown
	}

	def get:T = {
		latch.await
		t
	}

	def get(timeout:Long, unit:TimeUnit):T = {
		latch.await(timeout,unit)
		t
	}
}
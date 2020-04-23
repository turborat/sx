package x

import collection.mutable.Queue
import x.X._
import scala.collection.mutable._

object QueueSpeedathlon
{
	def main(args:Array[String]) {

		printf("MEM: %,d%n", Runtime.getRuntime.maxMemory)

		val n = 1000*1000
		val q = new Queue[Double]
		val l = new ListBuffer[Double]
//		val a = new ArrayBuffer[Double]
//		val ll = new LinkedList[Double]
//		val f = new LIFO[Double]

		loop (5) {
			new SpeedathlonX {
				contend("Queue.enqueue") { loop(n) { q.enqueue(1) }}
				contend("ListBuffer+=") { loop(n) { l += 1 }}
//				contend("ArrayBuffer+=") { loop(n) { a += 1 }}
//				contend("LIFO.push") { loop(n) { f push 1}}
//				contend("LinkedList") { loop(n) { ll.add(1) }}
			}.compete foreach println

			Disaster If q.size != n
			Disaster If l.size != n
//			Disaster If a.size != n
//			Disaster If f.size != n
//			Disaster If ll.size != n

			println

			new SpeedathlonX {
				contend("Queue.dequeue") { loop(n) { q.dequeue }}
				contend("ListBuffer.remove(0)") { loop(n) { l.remove(0) }}
//				contend("ArrayBuffer.remove(0)") { loop(n) { a.remove(0) }}
//				contend("LIFO.shift") { loop(n) { f.shift }}
//				contend("LinkedList.take(1)") { loop(n) { ll.take(1) }}
			}.compete foreach println

			Disaster Unless q.isEmpty
			Disaster Unless l.isEmpty
//			Disaster Unless a.isEmpty
//			Disaster Unless f.isEmpty
//			Disaster Unless ll.isEmpty

			println("------------------------")
		}
	}
}

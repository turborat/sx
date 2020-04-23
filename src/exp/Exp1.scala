package exp

import collection.mutable.{ListBuffer, HashSet}
import collection.mutable

object Exp1
{
	def main(args: Array[String]) {
		val m1 = Map("a" -> 1, "b" -> 2, "c" -> 3)
		println(m1)

		val m2 = for ((k,v) <- m1) yield { (k,v*2) }
		println(m2)
	}
}


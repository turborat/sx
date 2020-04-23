package exp

import collection.mutable
import x.{SpeedathlonX}
import m.RNG
import collection.immutable.TreeSet
import java.util.Collections
import java.util
import com.sun.tools.hat.internal.util.Misc


object MapSpeedathlon
{
	class X extends Comparable[X]
	{
		def compareTo(o:X) = hashCode - o.hashCode
	}

	def main(args:Array[String]) {
		val n = 5*1000*1000
		val arr = new java.util.ArrayList[X](n)
		for (i <- 0 until n) arr.add(i,new X)

		val hashMap = new java.util.HashMap[X,X]
		val treeMap = new java.util.TreeMap[X,X]
		val ccMap = new java.util.concurrent.ConcurrentHashMap[X,X]
		val scalaMap = new mutable.HashMap[X,X]

		new SpeedathlonX {
			contend("HashMap.put") {
				for (i <- 0 until n) hashMap.put(arr.get(i), arr.get(i))
			}
			contend("TreeMap.put") {
				for (i <- 0 until n) treeMap.put(arr.get(i), arr.get(i))
			}
			contend("ConcurrentHashMap.put") {
				for (i <- 0 until n) ccMap.put(arr.get(i), arr.get(i))
			}
			contend("ScalaHashMap.put") {
				for (i <- 0 until n) scalaMap.put(arr.get(i), arr.get(i))
			}
			warmUp
			hashMap.clear
			treeMap.clear
			ccMap.clear
			compete
		}

		println

		new SpeedathlonX {
			contend("HashMap.update") {
				for (i <- 0 until n) hashMap.put(arr.get(i), arr.get(i))
			}
			contend("TreeMap.update") {
				for (i <- 0 until n) treeMap.put(arr.get(i), arr.get(i))
			}
			contend("ConcurrentHashMap.update") {
				for (i <- 0 until n) ccMap.put(arr.get(i), arr.get(i))
			}
			contend("ScalaHashMap.update") {
				for (i <- 0 until n) scalaMap.put(arr.get(i), arr.get(i))
			}
			warmUp
			compete
		}

		println

		new SpeedathlonX {
			contend("HashMap.get") {
				for (i <- 0 until n) hashMap.get(arr.get(i))
			}
			contend("TreeMap.get") {
				for (i <- 0 until n) treeMap.get(arr.get(i))
			}
			contend("ConcurrentHashMap.get") {
				for (i <- 0 until n) ccMap.get(arr.get(i))
			}
			contend("ScalaHashMap.get") {
				for (i <- 0 until n) scalaMap.get(arr.get(i))
			}
			warmUp
			compete
		}

		println

		new SpeedathlonX {
			contend("HashMap.iterate") {
				val it = hashMap.entrySet.iterator
				while(it.hasNext) it.next
			}
			contend("TreeMap.iterate") {
				val it = treeMap.entrySet.iterator
				while(it.hasNext) it.next
			}
			contend("ConcurrentHashMap.iterate") {
				val it = ccMap.entrySet.iterator
				while(it.hasNext) it.next
			}
			contend("ScalaHashMap.iterate") {
				val it = scalaMap.iterator
				while(it.hasNext) it.next
			}
			warmUp
			compete
		}

		println

		new SpeedathlonX {
			contend("HashMap.remove") {
				for (i <- 0 until n) hashMap.remove(arr.get(i))
			}
			contend("TreeMap.remove") {
				for (i <- 0 until n) treeMap.remove(arr.get(i))
			}
			contend("ConcurrentHashMap.remove") {
				for (i <- 0 until n) ccMap.remove(arr.get(i))
			}
			contend("ScalaHashMap.remove") {
				for (i <- 0 until n) scalaMap.remove(arr.get(i))
			}
			warmUp
			compete
		}
	}
}


object SmallestElementSpeedathlon
{
	def main(args:Array[String]) {
		printf("%,d%n",Runtime.getRuntime.maxMemory)

		val size = 1000*1000
		val list = new Array[Long](size)

		for (i <- 0 until size) {
			list(i) = RNG.nextLong
		}

		new SpeedathlonX {
			contend("Simple loop") {
				var min = Long.MaxValue
				for (n <- list)
					if (n < min) min = n
				println(min)
			}

			contend("TreeSet") {
				val set = new java.util.TreeSet[Long]
				for (n <- list) set.add(n)
				println(set.first)
			}

			contend("Collections.sort") {
				util.Arrays.sort(list)
				println(list(0))
			}

			warmUp
			compete
		}
	}
}

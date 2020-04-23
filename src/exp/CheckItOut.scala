package exp

import x.X


object PITChecker
{
	def main(args:Array[String]) {
		val n = 2000
		var sum = 0l

		for (i <- 0 to n) {
			val t1 = System.nanoTime
			Thread.sleep(0,1)
			sum += System.nanoTime - t1
		}

		val avg = sum.toDouble / n
		printf("Average sleep time: %,.3f nanos %n", avg)

		printf("I guess your PIT resolution is ")
		if (avg >= 1000*1000)
			printf("%d millisecond%n", (avg/1000/1000).toInt)
		else
			printf("%d microsecond%n", (avg/1000).toInt)

	}
}


object ThreadSanity
{
	def main(args:Array[String]) {
		val mon = new Object
		for (t <- 0 to 1000) {
			X.thread() {
				val t1 = Thread.currentThread
				val t2 = Thread.currentThread
				if (t1 != t2) println(t1,t2)
			}
		}
	}
}


object NanoSanity
{
	def main(args:Array[String]) {
		val mon = new Object
		for (t <- 0 to 1000) {
			X.thread() {
				val t1 = System.nanoTime
				val t2 = System.nanoTime
//				if (t2==t1) println(t1,"=",t2)
				if (t2<t1) println(t2,"<",t1)
			}
		}
	}
}
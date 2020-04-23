package test

import x.X._

object TestPIT
{
	def main(args:Array[String]) {
		loop(20) {
			ptime("sleep(0)") {
				Thread.sleep(0)
			}
		}
		loop(20) {
			printf("%,d %n", System.nanoTime - System.nanoTime)
		}
	}
}
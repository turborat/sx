package sim

import m.RNG
import mc.{MC, SimApp}
import util.Random
import collection.mutable.MutableList

// Because Dimi is sceptical of RNGs
object RNGTester extends SimApp
{
	its = 200*1000*1000
	def go(rng:RNG) = rng.rand
}


object RandomShuffleTester extends SimApp
{
	its = 10*1000*1000
	val list = List(1,2,3,4,5)
	def go(rng:RNG):Double = {
		val list2 = Random.shuffle(list)
		list2(0) - list2(1)
	}
}


object RNGShuffleTester extends SimApp
{
	its = 10*1000*1000
	def go(rng:RNG):Double = {
		val list = MutableList(1,2,3,4,5)
		rng.shuffle(list)
		list(0) - list(1)
	}
}
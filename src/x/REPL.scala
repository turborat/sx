package x

import scala.language.postfixOps
import m.SafeRNG
import m.Math._
import fin.data.FX

class DoubleX(d:Double) {
  def ** (exp:Double) = math.pow(d,exp)
  def ** (exp:Long)   = math.pow(d,exp)
}

class LongX(i:Long) {
  def ** (exp:Double) = math.pow(i,exp)
  def ** (exp:Long)   = math.pow(i,exp).longValue
  def ! = factorial(i)
	def bits = String.format("%64s", java.lang.Long.toBinaryString(i)).replaceAll(" ", "0")
	def hex = String.format("0x%16s", java.lang.Long.toHexString(i).toUpperCase).replaceAll(" ", "0")
}

object REPL {
	val rng = SafeRNG

	def X = SafeRNG.nrand

	def fx(pair:String)
	  = FX(pair.substring(0,3), pair.substring(3,6))

	def help(x:AnyRef)
	  = x.getClass.getMethods foreach println

	def fib1(n:Int):Long = {
	  var n1=0l
	  var n2=1l
	  for (i <- 0 to n) {
	    // printf("%,d %n", n1)
	    val tmp=n1
	    n1=n2
	    n2=tmp+n2
	  }
	  n1
	}

	def mem {
	  val rt = Runtime.getRuntime
	  printf("Memory[used:%,d free:%,d total:%,d max:%,d]%n",
	      rt.totalMemory - rt.freeMemory,
	      rt.freeMemory,
	      rt.totalMemory,
	      rt.maxMemory
	  )
	  rt.gc
	  printf("Memory[used:%,d free:%,d total:%,d max:%,d]%n",
	      rt.totalMemory - rt.freeMemory,
	      rt.freeMemory,
	      rt.totalMemory,
	      rt.maxMemory
	  )
	}

	// hack
	class Ternary(b:Boolean) {
		var v1:Any=null
		def ?[T](v1:T):Ternary = { this.v1 = v1 ; print(this); this }
		def |[T](v2:T):T = if (b) v1.asInstanceOf[T] else v2
	}

	implicit def toTernary(b:Boolean) = new Ternary(b)
	implicit def toDoubleX(d:Double) = new DoubleX(d)
	implicit def toLongX(i:Long) = new LongX(i)
}
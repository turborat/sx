package x

import java.util.Arrays


class NKey(private val keys:Array[AnyRef])
{
	// terribly inefficient - i'm sorry ...
	def this(keys:Any*) = this(keys.map{ _.asInstanceOf[AnyRef] }.toArray)
	// def this(keys:AnyRef*) = this(keys.toArray)

	override def equals(that:Any) = that match {
		case that: NKey => Arrays.equals(this.keys, that.keys)
		case _ => false
	}
	
	override lazy val hashCode
		= Arrays.hashCode(keys)
	
	override lazy val toString
	  = "[" + keys.mkString(", ") + "]"
}
package x

import scala.collection._
import java.lang.reflect.{Member, Method}
import mutable.{ArrayBuffer}
import x.X._
import java.lang.Object

// fly-on-the-wall sees all
class TheFly(obj:AnyRef, clz:Class[_<:Any])
{
	def this(clz:Class[_<:Any]) = this(null,clz)
	def this(obj:AnyRef) = this(obj,obj.getClass)

	private val IGNORES = Set("$outer")
	private val dirt = {
		def ignored(m:Method) = IGNORES exists (suff => m.getName.endsWith(suff))

		val meths = ArrayBuffer[Method]()
		var thisClz = clz

		while (thisClz != null && thisClz != classOf[Object]) {
			var ms = thisClz.getDeclaredMethods
			ms = ms filterNot ignored
			ms = ms filterNot (m => m.getName.contains("$"))
			ms = ms filter (m => m.getParameterTypes.length == 0)
			meths.insertAll(0, ms)
			thisClz = thisClz.getSuperclass
		}

		makeMap (meths) { m => m.getName -> new Dirt(obj,m) }
	}

	// duplicates = trouble ?
	def members:Iterable[Dirt] = dirt.values
	def apply[T](member:String):T = dirt(member).apply[T]()

	override def toString = "TheFly{" + clz.getSimpleName + "} : " + X.join(this.members)
}

// the fly sees all
class Dirt(obj:Any, meth:Method)
{
	def apply[T]() = meth.invoke(obj).asInstanceOf[T]
	def apply(other:AnyRef) = meth.invoke(other)
	def name = meth.getName
	override def toString =
		if (obj != null)
			meth.getName + "=" + apply
		else
			meth.getName
}

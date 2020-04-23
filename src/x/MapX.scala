package x

import java.util.Map.Entry


class MapX[K,V](allowNulls:Boolean=false) extends Iterable[(K,V)]
{
	import scala.collection.JavaConversions._
	protected val _map = new java.util.TreeMap[K,V]

  def += (k:K, v:V) = _map.put(k, v)
	def += (t:(K, V)) = _map.put(t._1, t._2)
	def update(k:K, v:V) = _map.put(k,v)
	def -= (k:K) = _map.remove(k)

	def apply(k:K):V = {
		val ret = _map.get(k)
		Disaster If (!allowNulls && ret == null, "cold ghost")
		// note: depending on the type of K, a null K might be boxed into something
		ret
	}

	def values:Iterable[V] = _map.values
	def contains(k:K) = _map.containsKey(k)

	def floorEntry(k:K) = asTuple(_map.floorEntry(k))
	def ceilEntry(k:K)  = asTuple(_map.ceilingEntry(k))
  def min = asTuple(_map.firstEntry)
  def max = asTuple(_map.lastEntry)

	def iterator = new Iterator[(K,V)] {
		private val it = _map.entrySet.iterator
		def hasNext = it.hasNext
		def next = asTuple(it.next)
	}

	override def toString:String
		= "[" + X.join(this, " ") + "]"

	private def asTuple(e:Entry[K,V]):(K,V) = new Tuple2(e.getKey, e.getValue)
  {
    def k = _1 ; def v = _2
    override def toString = k + ":" + v
  }
}


// map that automatically adds values the first time they are accessed
abstract class AutoMap[K,V] extends MapX[K,V]
{
	def create(k:K):V

	override def apply(k:K):V = {
		var v = _map get k
		if (v == null) {
			v = create(k)
			this += (k,v)
		}
		v
	}
}
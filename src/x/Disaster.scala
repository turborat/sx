package x

class Disaster(fmt:String) extends Exception(fmt)
{
}

object Disaster
{
	def apply(msg:String) = throw new Disaster(msg)
	def If(test:Boolean, msg:String=null) = if (test) Disaster(msg)
	def Unless(test:Boolean, msg:String=null) = If(!test,msg)
}
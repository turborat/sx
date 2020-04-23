package fin.data

import java.util.regex.Pattern
import x.{X, Disaster}

object FX
{
	val req = "http://www.xe.com/ucc/convert.cgi?template=mobile&Amount=1&From=%s&To=%s"
	val rex = "(?i).*%s = ([\\d\\.\\,]+) %s.*"

	def apply(ccy1:String, ccy2:String):Double = {
		val dat = X.httpGet(req format (ccy1, ccy2))
		val regex = Pattern.compile(rex format (ccy1, ccy2))
		val matcher = regex.matcher(X.join(dat))

		Disaster Unless matcher.matches

		matcher.group(1).toDouble
	}

	def main(args:Array[String]) {
		print(FX("usd","gbp"))
	}
}


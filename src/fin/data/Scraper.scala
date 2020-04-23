package fin.data

import x.{X, TS}

//	  # sn = TICKER
//	  # a = fromMonth-1
//	  # b = fromDay (two digits)
//	  # c = fromYear
//	  # d = toMonth-1
//	  # e = toDay (two digits)
//	  # f = toYear
//	  # g = d for day, m for month, y for yearly
//
//    http://ichart.finance.yahoo.com/table.csv?s=${sym}&d=0&e=28&f=2010&g=d&a=3&b=12&c=1996&ignore=.csv
class Scraper(sym:String, start:TS=TS(200,1,1), end:TS=TS.now)
{
	val req = "http://ichart.finance.yahoo.com/table.csv?s=%s a=%d b=%02d c=%d d=%d e=%02d f=%d ignore=.csv".format(
      sym, start.month-1, start.day, start.year, end.month-1, end.day, end.year).replaceAll(" ","&")

	val lines = X.ptime("httpGet:" + sym) { X.httpGet(req) }
	val headers = headerMap(lines(0))

	val elements:List[Map[String,String]]
		= for (line <- lines.drop(1)) yield {
				val es = line.split(",")
				for ((name,idx) <- headers) yield {
					(name, es(idx))
				}
			}

	private def headerMap(line:String):Map[String,Int] = {
		val headers = line.split(",")
		(0 until headers.length) map { i => (headers(i), i) } toMap
	}
}


object ScraperTester
{
	def main(args:Array[String]) {
		val s = new Scraper("SPX")
		s.elements foreach println
	}
}
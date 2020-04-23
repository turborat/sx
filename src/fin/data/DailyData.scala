package fin.data

import db.DB
import x.{X, MapX, TS}

abstract class DailyData(val sym:Sym)
{
	val date:TS
	val open, close, high, low, vol, adj :Double

	override def toString
		= "%s %s open=%,.2f close=%,.2f high=%,.2f low=%,.2f vol=%,.2f adj=%,.2f" format
			(sym, date, open, close, high, low, vol, adj)

	def write = DailyData.write(this)
	def delete = DailyData.delete(this)
}


object DailyData
{
	def apply(sym:Sym, doRefresh:Boolean=true):MapX[TS,DailyData] = {
		val ret = read(sym)

		if (doRefresh) {
			val age = ret.last._1.daysBetween(TS.now)
			if (age > 3) {
				printf("%s data is %d days old - attempting refresh%n", sym, age)
				sym.refresh
			}
		}

		ret
	}


	private def read(sym:Sym):MapX[TS,DailyData] = {
		val ret = new MapX[TS,DailyData]
		val nanos = X.timeit {
			DB.exec("select * from fin_dd where sym_id = " + sym.id, false) {
				rs =>
				val dd = new DailyData(sym) {
					val date = TS(rs.getTimestamp("ts").getTime)
					val open = rs.getDouble("open")
					val close = rs.getDouble("close")
					val high = rs.getDouble("high")
					val low = rs.getDouble("low")
					val adj = rs.getDouble("adj")
					val vol = rs.getDouble("vol")
				}
				ret(dd.date) = dd
			}
		}
		printf("Read %,d entries for %s in %s\n", ret.size, sym, X.prettyTime(nanos))
		ret
	}


	private def write(dd:DailyData) {
		DB.update("insert into fin_dd (sym_id, ts, open, close, high, low, vol, adj) " +
			"values (%d, to_timestamp(%d), %f, %f, %f, %f, %f, %f)"
			format (dd.sym.id, dd.date.ms/1000, dd.open, dd.close, dd.high, dd.low, dd.vol, dd.adj))
	}


	private def delete(dd:DailyData) {
		DB.update("delete fin_dd where sym_id = %d ant ts = to_timestamp(%d)"
			format (dd.sym.id, dd.date.ms/1000))
	}


	def create(sym:Sym, vals:Map[String,String]):DailyData = {
		new DailyData(sym) {
			val open = vals("Open").toDouble
			val close = vals("Close").toDouble
			val high = vals("High").toDouble
			val low = vals("Low").toDouble
			val adj = vals("Adj Close").toDouble
			val vol = vals("Volume").toDouble
			val date = TS.forPattern("YYYY-MM-dd", vals("Date"))
			assert (high >= low)
		}
	}
}

package x

import org.joda.time.{Years, Months, Days, DateTime}
import org.joda.time.format.DateTimeFormat
import scala.math._

// timestamp

class TS private (private val dt:DateTime) extends Comparable[TS]
{
	val ms = dt.getMillis

  // elapsed time in years
  def elapsedT(that:TS)
    = (that.ms - this.ms) / TS.MS_PER_YEAR

  def <= (that:TS)
    = ms <= that.ms

  override def toString:String = {
		if (dt.getMillisOfDay == 0)
			TS.DATE_FMT.print(dt)
		else if (dt.getMillisOfSecond == 0)
			TS.DATE_TIME_FMT.print(dt)
		else
			TS.LONG_FMT.print(dt)
	}

	def year  = dt.getYear
	def month = dt.getMonthOfYear
	def day   = dt.getDayOfMonth
	def dow   = dt.getDayOfWeek
	def hour  = dt.getHourOfDay
	def min   = dt.getMinuteOfHour

	def compareTo(that:TS)
		= dt.compareTo(that.dt)

	def daysBetween(that:TS)
		= abs(Days.daysBetween(this.dt, that.dt).getDays)

	def monthsBetween(that:TS)
		= abs(Months.monthsBetween(this.dt, that.dt).getMonths)

	def yearsBetween(that:TS)
		= abs(Years.yearsBetween(this.dt, that.dt).getYears)
}


object TS
{
	val DATE_STR      = "YYYY.MM.dd"
	val TIME_STR      = "HH:mm:ss"
	val DATE_FMT      = DateTimeFormat.forPattern(DATE_STR)
	val TIME_FMT      = DateTimeFormat.forPattern(TIME_STR)
	val DATE_TIME_FMT = DateTimeFormat.forPattern(DATE_STR + " " + TIME_STR)
	val LONG_FMT      = DateTimeFormat.forPattern(DATE_STR + " " + TIME_STR + ".SSS")

	val MS_PER_DAY  = 24 * 60 * 60 * 1000L
  val MS_PER_YEAR = 365d * MS_PER_DAY

	val XLS_DAYS_SINCE_1900 = 25569 /* empirical */
 	val XLS_HOUR_OFFSET = 1 / 24d   /* hack */

  def apply(year:Int, month:Int, day:Int, hour:Int, min:Int, sec:Int)
    = new TS(new DateTime(year, month, day, hour, min, sec))

	def apply(year:Int, month:Int, day:Int):TS
   = apply(year, month, day, 0, 0, 0)

	def now
		= new TS(new DateTime)

	def apply(ms:Long)
		= new TS(new DateTime(ms))

  def fromExcel(xls:Double)
    = new TS(new DateTime(((xls - XLS_DAYS_SINCE_1900 - XLS_HOUR_OFFSET) * MS_PER_DAY).toLong))

  def toExcel(ts:TS)
    = ts.ms.toDouble / MS_PER_DAY + XLS_HOUR_OFFSET + XLS_DAYS_SINCE_1900

	def forPattern(pat:String, str:String)
		= new TS(DateTimeFormat.forPattern(pat).parseDateTime(str))
}
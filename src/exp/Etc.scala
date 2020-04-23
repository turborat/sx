package exp

import org.joda.time.DateTime
import x.TS

object ChloésAge
{
	def main(args:Array[String]) {
		val birthday = TS(2012,6,28)
		val now = TS.now
		printf("%,d days%n", birthday.daysBetween(now))
		printf("%,d months%n", birthday.monthsBetween(now))
		printf("%,d years%n", birthday.yearsBetween(now))
	}
}

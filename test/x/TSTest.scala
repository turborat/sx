package x

import test.TestX

class TSTest extends TestX
{
	def testToString {
		assertEqualsS("2012.12.31 23:58:00", TS(2012,12,31,23,58,00))
		assertEqualsS("2012.12.31", TS(2012,12,31,00,00,00))
		assertEqualsS("1973.11.03 22:02:01.212", TS(121212121212L))
	}

  def testElapsedT {
    val start = TS(2012,12,31,23,58,00)
    val stop  = TS(2013,12,31,23,58,00)
    assertEquals(1d, start.elapsedT(stop))
    assertEquals(-1d, stop.elapsedT(start))
    assertEquals(.5, start.elapsedT(TS(2013,7,2,19,40,00)),0.001)
  }

  def testToFromExcel {
    val _01_01_1970 = 25569
    val _02_01_2012 = 40910
    val _03_01_2012 = 40911

    assertEqualsS("1970.01.01", TS.fromExcel(_01_01_1970))
    assertEqualsS("2012.01.01 23:00:00", TS.fromExcel(_02_01_2012))
    assertEqualsS("2012.01.02 23:00:00", TS.fromExcel(_03_01_2012))
    assertEqualsS("2012.01.03 11:00:00", TS.fromExcel(_03_01_2012+.5))

    assertEquals(_01_01_1970, TS.toExcel(TS(1970,1,1,0,0,0)))
    assertEquals(_02_01_2012, TS.toExcel(TS(2012,1,2,0,0,0)), 0.1)
    assertEquals(_03_01_2012, TS.toExcel(TS(2012,1,3,0,0,0)), 0.1)
    assertEquals(_03_01_2012+.5, TS.toExcel(TS(2012,1,3,12,0,0)), 0.1)
  }

  def testLessThan {
    val ts1 = TS(2012,12,31,23,58,00)
    val ts2 = TS(2012,12,31,23,58,01)
    assertTrue(ts1 <= ts2)
    assertFalse(ts2 <= ts1)
  }

	def testValues {
		val ts = TS(2011,5, 13, 15, 7, 0)
		assertEquals(2011, ts.year)
		assertEquals(5, ts.month)
		assertEquals(13, ts.day)
		assertEquals(15, ts.hour)
		assertEquals(7, ts.min)
	}

	def testForPattern {
		assertEqualsS("2012.10.06", TS.forPattern("YYYY-MM-dd", "2012-10-06"))
		assertEqualsS("2012.10.06", TS.forPattern("YYYY|MM|dd", "2012|10|06"))
		assertEqualsS("2012.10.06", TS.forPattern("YY|MM|dd", "12|10|06"))
		assertEqualsS("2012.10.06", TS.forPattern("dd^MM^^YY", "06^10^^12"))
		assertEqualsS("2012.10.06 00:00:11", TS.forPattern("dd^MM^^YY.ss", "06^10^^12.11"))
	}

	def testDaysBetween {
		val ts1 = TS(2001,1,1)
		val ts2 = TS(2001,1,5)
		assertEquals(4, ts1.daysBetween(ts2))
		assertEquals(4, ts2.daysBetween(ts1))
		assertEquals(0, ts1.daysBetween(ts1))
	}
}
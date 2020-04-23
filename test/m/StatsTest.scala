package m

import test.TestX

class StatsTest extends TestX
{
	def testMin {
		val stats = new Stats
		assertTrue(stats.min.isNaN)
		stats << 2
		assertEquals(2, stats.min, 0.00000000001)
		stats << -2
		assertEquals(-2, stats.min, 0.00000000001)
	}

	def testMax {
		val stats = new Stats
		assertTrue(stats.max.isNaN)
		stats << -2
		assertEquals(-2, stats.max, 0.00000000001)
		stats << 2
		assertEquals(2, stats.max, 0.00000000001)
	}

	def testStDev {
		val stats = new Stats
		stats << 2
		stats << 4
		stats << 4
		stats << 4
		stats << 5
		stats << 5
		stats << 7
		stats << 9

		assertEquals(5d, stats.avg(), 0.00000000000001)
		assertEquals(2d, stats.stdev(), 0.00000000000001)
	}

	def testStDev2 {
	  val stats = new Stats
	  stats << List(2d,4,4,4,5,5,7,9)
	  assertEquals(5d, stats.avg(), 0.00000000000001)
	  assertEquals(2d, stats.stdev(), 0.00000000000001)
	}

	def testTallyList {
		val stats = new Stats
		val l = List(1d,2d,4d)
		stats << l
		assertEquals("[num:3 sum:7.0 min:1.0 max:4.0 avg:2.3333333333333335 stdev:1.247219128924647]", stats.toString)
	}

	def testTally {
		val stats = new Stats
		stats << 1 << 2 << 4
		assertEquals("[num:3 sum:7.0 min:1.0 max:4.0 avg:2.3333333333333335 stdev:1.247219128924647]", stats.toString)
	}

	def testPrint {
		val stats = new Stats
		stats << 2
		stats << -1
		assertEquals("[num:2 sum:1.000000 min:-1.000000 max:2.000000 avg:0.500000 stdev:1.500000]",stats.print())
		assertEquals("[num:2 sum:1.00 min:-1.00 max:2.00 avg:0.50 stdev:1.50]",stats.print(2))
		assertEquals("[num:2 sum:1 min:-1 max:2 avg:1 stdev:2]",stats.print(0))
	}
}
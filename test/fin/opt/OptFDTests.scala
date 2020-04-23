package fin.opt

import m.Stats
import x.X._
import test.TestX
import x.SpeedathlonX


class OptFDTests extends TestX
{
	def testNodesCreatedAmi = testNodesCreated(new OptionBuilder.AmiOptFD)

	def testNodesCreatedEur = testNodesCreated(new OptionBuilder.EurOptFD)

	private def testNodesCreated(bldr: OptionBuilder[_ <: OptFD])
	{
		loop(100) {
			val opt = bldr.build()
			assertEquals(0, opt.nodes)
			opt.V
			val nodes = opt.nodes
			assertTrue(nodes < (opt.NAS + 1) * (opt.NTS + 1))
			opt.V
			opt.V
			assertEquals(nodes, opt.nodes)
		}
	}
}


object OptFDSpeedTester
{
	val tests = 500
	val loops = 10

	def main(args: Array[String])
	{
		loop(5) {
			val sa = new SpeedathlonX
			sa.contend("FD0") {
				testEm(new OptionBuilder.EurOptFD0)
			}
			sa.contend("FD") {
				testEm(new OptionBuilder.EurOptFD)
			}
			sa.compete
			println
		}
	}

	def testEm(bldr: OptionBuilder[_ <: Opt])
	{
		val stats = new Stats
		loop(tests) {
			val opt = bldr.build()
			loop(loops) {
				stats << timeit {
					val opt2 = opt.reval()
					opt2.V
				}
			}
		}
		println(bldr.build().getClass.getSimpleName)
		stats.print(0)
	}
}


// used for profiling
object OptFDLoopRunner
{
	def main(args: Array[String])
	{
		val bldr = new OptionBuilder.EurOptFD
		while (true) {
			val opt = bldr.build()
			loop(20) {
				opt.reval().V
			}
		}
	}
}

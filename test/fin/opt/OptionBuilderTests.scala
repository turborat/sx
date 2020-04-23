package fin.opt

import test.TestX
import x.X._

class OptionBuilderTests extends TestX
{
	def test1
	{
		val bldr = new OptionBuilder.EurOptMC
		assertTrue(bldr.build().toString != bldr.build().toString)
	}

	def test2
	{
		val bldr = new OptionBuilder.AmiOptFD
		loop(10) {
			println(bldr.build())
		}
	}

	def testOverrides
	{
		val bldr = new OptionBuilder.EurOptMC
		{
			override def v = 100
		}
		assertEquals(100, bldr.build().v)
		assertEquals(50, bldr.build(v = 50).v)
	}

	def testClone
	{
		val bldr0 = new OptionBuilder.EurOptFD0
		val bldr1 = new OptionBuilder.EurOptFD
		val opt0 = bldr0.build()
		println(opt0)
		val opt1 = bldr1.clone(opt0)
		println(opt1)
	}
}

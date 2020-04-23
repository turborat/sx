package x

import test.TestX

class SpeedathlonTest extends TestX
{
	def test1 {
		class X(val i:Int) 
		{
			override def toString = "X["+i+"]"
		}
		
		val sa = new Speedathlon[X]
		
		sa.contenders += new X(3)
		sa.contenders += new X(1)
		sa.contenders += new X(2)

		val r = sa.compete() { x => Thread.sleep(x.i * 100)  }
		r foreach println
		
		assertEquals(3, r.size)
		assertEqualsS("1.00 - 100 ms - X[1]", r(0))
		assertEqualsS("2.00 - 200 ms - X[2]", r(1))
		assertEqualsS("3.00 - 300 ms - X[3]", r(2))
	}
}
package x

import test.TestX

class REPLTest extends TestX
{
	import REPL._

	def test1 {
		assertEquals(100, 10**2)
		assertEquals(100, 10.0**2)
		assertEquals(100, 10**2.0)
//		assertEquals(0.01, 1)
	}

	def ternary: Unit = {
		assertEquals("abc", (1>2) ? "def" | "abc")
		assertEquals("def", (1<2) ? "def" | "abc")
	}
}

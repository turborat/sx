package x

import test.TestX


class TheFlyTest extends TestX
{
	class A
	{
		val i = 1
		val s = "abc"
		def foo() = "f"
		def xx(x:Int) = 0 // should be ignored as a member
	}

	class B extends A
	{
		val d = 22/7.
		val c = new C
		def bar = "b"
	}

	class C
	{
		val x = new Object
	}

	def testMembers {
		val fly = new TheFly(new B)
		println(fly)
		val names = fly.members
		assertEquals(6, names.size)
		assertEquals(1, fly("i"))
		assertEquals("abc", fly("s"))
		assertEquals("f", fly("foo"))
		assertEquals("b", fly("bar"))
		assertEquals(22/7., fly("d"))
		assertEquals(fly("c").asInstanceOf[AnyRef].getClass, classOf[C])
	}


	def testDirt {
		val d1 = new Dirt(new A(), classOf[A].getMethod("i"))
		assertEquals("i=1", d1.toString)
		assertEquals(1, d1())

		val d2 = new Dirt(new A(), classOf[A].getMethod("foo"))
		assertEquals("foo=f", d2.toString)
		assertEquals("f", d2())
	}
}
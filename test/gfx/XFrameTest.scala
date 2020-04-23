package gfx

import test.TestX

class XFrameTest extends TestX 
{
	def testParseSettings {
		val line = "App:Frame=200x100@33,44"
		val settings = Settings parse line
		assertEquals("App", settings.app)
		assertEquals("Frame", settings.frame)
		assertEquals(200, settings.dim.width)
		assertEquals(100, settings.dim.height)
		assertEquals(33, settings.loc.x)
		assertEquals(44, settings.loc.y)
		assertEquals(line, settings.toString)
	}
}
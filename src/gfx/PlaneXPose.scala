package gfx

import m.Point


// transpose a plane between coordinate systems - zbs for drawing plots to a screen
class PlaneXPose(planeA:Plane, planeB:Plane)
{
	val xRatio = (planeB.xN-planeB.x0) / (planeA.xN-planeA.x0)
	val yRatio = (planeB.yN-planeB.y0) / (planeA.yN-planeA.y0)

	def xpose(x:Double, y:Double):Point = {
//		assert(planeA.x0 <= x)
//		assert(x <= planeA.xN)
//		assert(planeA.y0 <= y)
//		assert(y <= planeA.yN)
		new Point((x-planeA.x0) * xRatio + planeB.x0, (y-planeA.y0) * yRatio + planeB.y0)
	}

	override def toString() = "%s -> %s" format (planeA, planeB)
}

class Plane(val x0:Double, val y0:Double, val xN:Double, val yN:Double)
{
	def this(xN:Double, yN:Double) = this(0, 0, xN, yN)
//	assert(x0<xN)
//	assert(y0<yN)
	override def toString() = "(%.2f,%.2f)@(%.2f,%.2f)" format (x0, y0, xN, yN)
}

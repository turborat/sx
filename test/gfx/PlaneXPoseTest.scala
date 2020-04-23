package gfx

import test.TestX

class PlaneXPoseTest extends TestX
{
//	def test1() {
//		assertEquals(0d, new PlaneXPose().xpose(0,10,0,100,0), 0.0000001)
//		assertEquals(25d, new PlaneXPose().xpose(0,10,0,100,2.5), 0.0000001)
//		assertEquals(50d, new PlaneXPose().xpose(0,10,0,100,5), 0.0000001)
//		assertEquals(100d, new PlaneXPose().xpose(0,10,0,100,10), 0.0000001)
//		assertEquals(-10d, new PlaneXPose().xpose(0,10,0,100,-1), 0.0000001)
//		assertEquals(110d, new PlaneXPose().xpose(0,10,0,100,11), 0.0000001)
//	}
//
//	def test2() {
//		assertEquals(-0d, new PlaneXPose().xpose(-10,10,-100,100,0), 0.0000001)
//		assertEquals(-50d, new PlaneXPose().xpose(-10,10,-100,100,-5), 0.0000001)
//		assertEquals(50d, new PlaneXPose().xpose(-10,10,-100,100,5), 0.0000001)
//		assertEquals(-1d, new PlaneXPose().xpose(0,1,-1,0,0), 0.0000001)
//		assertEquals(.5d, new PlaneXPose().xpose(-1,0,0,1,-.5), 0.0000001)
//		assertEquals(-5d, new PlaneXPose().xpose(-1,0,-10,0,-.5), 0.0000001)
//	}

	def test3() {
		val x = new PlaneXPose (new Plane(-5,-5,5,5), new Plane(-50,-50,50,50))

		assertEquals(0d, x.xpose(0,0).x, 0.0000001)
		assertEquals(0d, x.xpose(0,0).y, 0.0000001)

		assertEquals(20d, x.xpose(2,2).x, 0.0000001)
		assertEquals(20d, x.xpose(2,2).y, 0.0000001)

		assertEquals(-20d, x.xpose(-2,-2).x, 0.0000001)
		assertEquals(-20d, x.xpose(-2,-2).y, 0.0000001)
	}

	def test4() {
		var x = new PlaneXPose(new Plane(0,0,1,2),new Plane(0,0,10,20))

		assertEquals(0d, x.xpose(0,0).x, 0.0000001)
		assertEquals(0d, x.xpose(0,0).y, 0.0000001)

		assertEquals(10d, x.xpose(1,1).x, 0.0000001)
		assertEquals(10d, x.xpose(1,1).y, 0.0000001)

		assertEquals(10d, x.xpose(1,2).x, 0.0000001)
		assertEquals(20d, x.xpose(1,2).y, 0.0000001)

		x = new PlaneXPose(new Plane(1,2), new Plane(100,200))

		assertEquals(0d, x.xpose(0,0).x, 0.0000001)
		assertEquals(0d, x.xpose(0,0).y, 0.0000001)

		assertEquals(100d, x.xpose(1,1).x, 0.0000001)
		assertEquals(100d, x.xpose(1,1).y, 0.0000001)

		assertEquals(100d, x.xpose(1,2).x, 0.0000001)
		assertEquals(200d, x.xpose(1,2).y, 0.0000001)

		x = new PlaneXPose(new Plane(10,20), new Plane(100,200))

		assertEquals(10d, x.xpose(1,1).x, 0.0000001)
		assertEquals(10d, x.xpose(1,1).y, 0.0000001)

		assertEquals(20d, x.xpose(2,2).x, 0.0000001)
		assertEquals(20d, x.xpose(2,2).y, 0.0000001)
	}
}
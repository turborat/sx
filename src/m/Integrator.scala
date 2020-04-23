package m


object Integrator {
	def trapezoid(x0:Double, x1:Double, f:(Double)=>Double, steps:Int=1000*1000):Double = {
		val dx = (x1-x0) / steps
		var yLast = f(x0)
		var area = 0.

		for (step <- 1 to steps) {
			val y = f(x0+step*dx)
			area += (y+yLast) / 2 * dx
			yLast = y
		}

	  area
	}

}
package fin.opt

import mc.Calibrator
import gfx._
import x.X

class OptionCalibrator
{
	val opt = new EurOptMCSlow(S=50, K=50, v=0.2, r=0.02, T=2, call=true)

	val calibrator = new Calibrator(opt) {
		tests = 50
		verbose = 0
	}

	val points = new Points

	for (result <- calibrator.targetIter(1000*1000)) {
		println(result)
		points += (result.iter,result.stdev)
	}

	val name = X.objName(opt)
	new Graph(name, points, RenderStyle.JOINTS)
//	new Table(name+".", Matrix(points))
}

object OptionCalibrator
{
	def main(args:Array[String]) {
		XFrame.app = "OptionCalibrator"
		new OptionCalibrator
	}
}

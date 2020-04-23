package gfx

import swing._
import scala.math._
import java.awt.BorderLayout
import x.X._
import java.awt.{Canvas, Graphics, Color}
import javax.swing.JPanel


private class GraphComponent(name:String, val data:Plottable, style:RenderStyle.Style)
  extends Component
{
	val ABSTAND = 3
	val x0 = ABSTAND
	val y0 = ABSTAND
	val canvas:Canvas = new TheCanvas
	val xhairs = if (style!=RenderStyle.POINTS) new Crosshairs(this) else null
  var printed = false

	private class TheCanvas extends Canvas {
		// if 0x0 paint never gets called
		setSize(new Dimension(1,1))

		override def paint(g:Graphics) {
			super.paint(g) // performs clear

			setSize(getParent.getSize)

			val xN = getWidth - ABSTAND -1
			val yN = getHeight - ABSTAND -1

			try {
				data.xSteps(xN-x0+3)

        if(!printed) {
          printf("%s - Domain{ %f , %f } Range:{ %f , %f }%n", name, data.x0, data.xN, data.y0, data.yN)
          printed = true
        }

				val coords = ptime("X-pose:"+name, disabled=true) {
					transpose(data, xN, yN)
				}

				ptime("Render:"+name, disabled=true) {
					new PixelRenderer(this,style).draw(coords)
				}

				if (xhairs != null)
					xhairs.update(g, coords, xN, yN)
			}
			catch {
				case e => e.printStackTrace
			}

			// draw edge
			g.asInstanceOf[java.awt.Graphics2D].setStroke(new java.awt.BasicStroke)
			g.setColor(Color.DARK_GRAY)
			g.drawRect(x0, y0, xN-x0, yN-y0)
		}

		def transpose(data:Plottable, xN:Int, yN:Int):List[Coord] = {
			val planex = new PlaneXPose (
				planeA = new Plane(data.x0, data.y0, data.xN, data.yN),
				planeB = new Plane(x0, y0, xN, yN)
			)

			val yTop = getHeight

			makeList(data) { point =>
				val newPoint = planex.xpose(point.x, point.y)
				new Coord(newPoint.x, yTop-newPoint.y)
			}
		}
	}

	override lazy val peer = new JPanel(new BorderLayout)
	peer.add(canvas)
}


class Graph(name:String, data:Plottable, style:RenderStyle.Style=RenderStyle.POINTS) {
	XFrame(name, new GraphComponent(name, data, style), resizable=true)
}


class Coord(var x:Int, var y:Int) extends Comparable[Point]
{
	def this(x:Double, y:Double) = this(x.toInt, y.toInt)
	override def toString = "("+x+","+y+")"
	override def compareTo(o: Point) = if (x == o.x) 0 else if (x > o.x) 1 else -1
}


object GraphTester
{
	def main(args:Array[String]) {
		val points = new Points {
			+=(-1,1)
			+=(0,0)
			+=(1,1)
			+=(2,-1)
			+=(5,10)
		}

		XFrame.app = "GraphTester"
		new Graph("xx", points, RenderStyle.JOINTS)
		new Graph("sin_", new Function(0,20*Pi,sin), RenderStyle.LINES)

		new Graph("f=x", new Function(-1,1,(x:Double) => x ), RenderStyle.LINES)
		new Graph("f=-x", new Function(-1,1,(x:Double) => -x ), RenderStyle.LINES)
		new Graph("sin", new Function(-2,2,(x:Double) => sin(x*Pi)), RenderStyle.POINTS)

	}
}

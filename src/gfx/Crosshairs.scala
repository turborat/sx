package gfx

import java.awt.event.{KeyEvent, KeyAdapter, MouseEvent, MouseAdapter}
import java.awt.RenderingHints._
import java.awt.BasicStroke._
import m.Point
import m.Math._
import x.X._
import x.Disaster
import java.awt.{BasicStroke, Graphics2D, Font, Graphics, Color, Point => JPoint}

private [gfx] class Crosshairs(graph:GraphComponent)
{
	var cursor:JPoint   = null
	var nudgeAmt        = 1
	var nudges          = 0  // keep track of successive nudges to speed up
	val data            = graph.data
	val ABSTAND         = graph.ABSTAND
	val DASHED_STROKE   = new BasicStroke(1f, CAP_ROUND, JOIN_ROUND, 1f, Array(2f), 0f)
	def fun             = graph.data

	def updateMouse(point:JPoint) {
		cursor = point
		graph.canvas.repaint
		if (!graph.canvas.hasFocus) {
			// for some reason this needs to be invoked here has well as in XFrame (sloppy-focus)
			graph.canvas.requestFocus
		}
	}

	// TODO: this on a worker thread
	def nudgeMouse(dx:Int) {
		if (cursor != null) {
			val newX = cursor.x + dx * nudgeAmt
			if (newX < ABSTAND || graph.canvas.getWidth-ABSTAND <= newX)
				return

			nudges += 1
			if (nudges % 2 == 0)
				nudgeAmt += 1

			cursor = new JPoint(newX, cursor.y)
		}
		graph.canvas.repaint
	}

	val mouseListener = new MouseAdapter
	{
		override def mouseMoved(e:MouseEvent)  = updateMouse(e.getPoint)
	  override def mouseExited(e:MouseEvent) = updateMouse(null)
	}

	graph.canvas addMouseListener(mouseListener)
	graph.canvas addMouseMotionListener(mouseListener)

	GlobalKeyAlert.onPress(graph.canvas, '←') { nudgeMouse(-1) }
	GlobalKeyAlert.onPress(graph.canvas, '→') { nudgeMouse(+1) }

	GlobalKeyAlert.onRelease(graph.canvas, '←') { nudges = 0 ; nudgeAmt = 1	}
	GlobalKeyAlert.onRelease(graph.canvas, '→') { nudges = 0 ; nudgeAmt = 1 }


	def update(g:Graphics, coords:List[Coord], xN:Int, yN:Int) {
		if (cursor == null)
			return

		val g2d = g.asInstanceOf[Graphics2D]
		g2d.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON)
		g2d.setStroke(DASHED_STROKE)

		g.setColor(Color.BLACK)
		val mouseX = cursor.x
		val mouseY = findY(coords, mouseX)
		g.drawLine(graph.x0, mouseY, xN, mouseY)
		g.drawLine(mouseX, graph.y0, mouseX, yN)

		val xpose = new PlaneXPose(
			planeA = new Plane(graph.x0, graph.y0, xN, yN),
			planeB = new Plane(data.x0, data.y0, data.xN, data.yN)
		)

		drawCoords(xpose, mouseX, mouseY, xN, yN, g)
	}

	//slooooooow
	def findY(coords:List[Coord], x:Int):Int = {
		var c1:Coord = null
		for (c <- coords) {
			if (c.x == x)
				return c.y
			if (c.x < x)
				c1 = c
			else
				return interpolate(new Point(c1), new Point(c), x).y.toInt
		}
	  Disaster("ruination")
	}


	def drawCoords(xpose:PlaneXPose, mouseX:Int, mouseY:Int, xN:Int, yN:Int, g:Graphics) {
		val point = xpose.xpose(mouseX, yN-mouseY)
		// get y from the function to avoid numeric errors associated with xpose
		point.y = fun(point.x)

		g.setFont(GFX.MONO_FONT)

		val str = "[%s,%s]" format(fmtCH3(point.x), fmtCH3(point.y))
		val fm = g.getFontMetrics
		val strWidth = fm.stringWidth(str)
		val strHeight = fm.getHeight

		val deriv:Double =
		try {
			m.Math.D(point.x, (x:Double) => fun(x))
		}
		catch {
			// at extremes central difference fails
			case e => println(e +  " (expected)") ; 0
		}

		var right = deriv < 0
		var top = true

		if (right && mouseX + strWidth > xN) {
			top = ! top
			right = ! right
		}
		else if (!right && mouseX - strWidth <= ABSTAND) {
			top = ! top
			right = ! right
		}

		if (top && mouseY - strHeight <= ABSTAND) {
			top = false
		}
		else if (! top && mouseY + strHeight >= yN) {
			top = true
		}

		g.drawString(
			str,
			if (right) mouseX+2 else mouseX-strWidth,
			if (top)   mouseY-4 else mouseY+strHeight
		)
	}
}
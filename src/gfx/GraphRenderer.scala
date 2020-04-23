package gfx

import RenderStyle.Style
import java.awt._

object RenderStyle extends Enumeration
{
	type Style = Value
	val POINTS, JOINTS, LINES, CROSSES = Value
}

class PixelRenderer(c:Component, style:Style)
{
	val g2d = c.getGraphics.asInstanceOf[Graphics2D]

	def draw(coords:Iterable[Coord]) {
		val buf = c.createImage(c.getWidth, c.getHeight)
		val gbg = buf.getGraphics
		GFX.antiAlias(gbg, true)
		GFX.antiAlias(g2d, true)

		val renderer = style match {
			case RenderStyle.POINTS  => new PointRenderer(gbg)
			case RenderStyle.LINES   => new LineRenderer(gbg)
			case RenderStyle.JOINTS  => new JointRenderer(gbg)
			case RenderStyle.CROSSES => new CrossRenderer(gbg)
		}

		coords foreach renderer.render
		g2d.drawImage(buf,0,0,null)
	}
}

private trait Renderer
{
	def render(coord:Coord)
}

private class PointRenderer(g:Graphics) extends Renderer
{
	def render(coord:Coord) {
		g.setColor(Color.BLUE)
		g.drawLine(coord.x, coord.y, coord.x, coord.y)
	} 
}

private class LineRenderer(g:Graphics) extends Renderer
{
	var lastCoord:Coord = null
	def render(coord:Coord) {
		g.setColor(Color.BLACK)
		if (lastCoord != null)
			g.drawLine(lastCoord.x,lastCoord.y,coord.x,coord.y)
		lastCoord = coord
	}
}

private class JointRenderer(g:Graphics) extends Renderer
{
	val lineRenderer = new LineRenderer(g)
	override def render(coord:Coord) {
		lineRenderer.render(coord)
		g.setColor(Color.RED)
		g.drawOval(coord.x-1, coord.y-1, 2, 2)
	}
}

private class CrossRenderer(g:Graphics) extends Renderer
{
	val lineRenderer = new LineRenderer(g)
	override def render(coord:Coord) {
		lineRenderer.render(coord)
		g.setColor(Color.BLUE.darker)
		g.drawLine(coord.x-3, coord.y-3, coord.x+3, coord.y+3)
		g.drawLine(coord.x-3, coord.y+3, coord.x+3, coord.y-3)
	}
}

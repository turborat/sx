package gfx

import java.awt.RenderingHints._
import java.awt._

object GFX
{
	val MONO_FONT = Font.decode(Font.MONOSPACED)
	val NICE_FONT = Font.decode("Verdana").deriveFont(11f)

	def antiAlias(g:Graphics, b:Boolean) {
		val g2d = g.asInstanceOf[Graphics2D]
		val hint = if (b) VALUE_ANTIALIAS_ON else VALUE_ANTIALIAS_OFF
		g2d.setRenderingHint(KEY_ANTIALIASING, hint);
	}
}

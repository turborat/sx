package gfx

import scala.collection._
import java.util.regex.Pattern
import java.io.File
import x.X._
import x.Disaster
import scala.collection.JavaConversions._
import swing.{Component, Frame}
import java.awt.{Dimension, Point => JPoint}
import gfx.GlobalKeyAlert._
import java.awt.event.{MouseAdapter, MouseEvent}
import javax.swing.JComponent

class XFrame(val name:String, contents:Component, resizable:Boolean=true)
{
	if (XFrame.appName == null)
		Console.err.println("Warning: forgot to call XFrame.app =")

	private val settings = Settings(escape(name))

	private[gfx] val frame = new Frame
	{
		resizable = XFrame.this.resizable
		if (resizable)
			preferredSize = settings.dim

		location = settings.loc
		contents = XFrame.this.contents
		title = name
//		peer.setUndecorated(true)

		override def closeOperation {
			settings.loc = location
			settings.dim = size
			XFrame -= XFrame.this
		}
	}

	GlobalKeyAlert.onPress(frame.peer, 'W', APPLE) {
		close
	}

	GlobalKeyAlert.onPress(frame.peer, 'R') {
		println(name + ": repaint triggered")
		contents.peer.getComponents foreach (c => c.repaint)
	}

	// sloppy focus
	contents.peer addMouseMotionListener new MouseAdapter
	{
		override def mouseMoved(e:MouseEvent) {
      if (!contents.hasFocus) {
//        frame.peer.requestFocusInWindow
//        frame.peer.requestFocus
        frame.peer.toFront
      }
    }
	}

	def show {
		frame.pack
		frame.visible = true
		XFrame += this
	}

	def close {
		frame.visible = false
		frame.closeOperation
	}
}

object XFrame
{
	def apply(name:String, contents:Component, resizable:Boolean):XFrame
		= new XFrame(name, contents, resizable) { show }

	def apply(name:String, contents:JComponent, resizable:Boolean):XFrame
		= new XFrame(name, new Component { override lazy val peer = contents }, resizable) { show }

	private def += (frame:XFrame) = synchronized {
		Disaster If (openWindows.contains(frame.name), "duplicate frame name: " + frame.name)
		openWindows += frame.name
	}

	private def -= (frame:XFrame) = synchronized {
		Disaster Unless openWindows.remove(frame.name)
		if (openWindows.isEmpty) {
			Settings.write
			System.exit(0)
		}
	}

	private val openWindows = new mutable.HashSet[String]
	private var appName:String = null

	def app_=(name:String) = synchronized {
		Disaster If appName != null
		appName = name
	}

	def app = appName
}

private object Settings
{
	val regex    = Pattern.compile("^(\\S+):(\\S+)=(\\d+)x(\\d+)@(\\d+),(\\d+)$")
	val file     = new File(System.getProperty("user.home"), ".xframe")
	val settings = new java.util.TreeMap[String,Settings]()

	if (file.exists) {
		for (line <- slurp(file) if ! line.startsWith("#")) {
			val s = parse(line)
			settings.put(s.frame, s)
//			println("Loaded: " + s)
		}
	}

	def write {
		println("Writing " + file)
		val list = new mutable.ListBuffer[Settings]
		for (s <- settings.values)
			list += s
		spew(file,list)
	}

	def apply(name:String):Settings = {
		var ret = settings.get(name)
	  if (ret == null) {
			ret = Settings.default(name)
			settings.put(name,ret)
		}
		ret
	}

	def parse(line:String):Settings = {
		val matcher = regex.matcher(line)
		Disaster Unless(matcher.matches, "Unparseable: '%s'%n" format line)

		new Settings(
		  app = matcher.group(1),
			frame = matcher.group(2),
			width= matcher.group(3).toInt,
			height = matcher.group(4).toInt,
			x = matcher.group(5).toInt,
			y = matcher.group(6).toInt
		)
	}

	def default(name:String)
	  = new Settings(XFrame.app, name, 200, 100, 500, 500)

	def esc(name:String)
		= name.replaceAll(" ", "_")
}

private class Settings(var app:String, var frame:String, var dim:Dimension, var loc:JPoint)
{
	def this(app:String, frame:String, width:Int, height:Int, x:Int, y:Int)
		= this(app, frame, new Dimension(width, height), new JPoint(x,y))
	override def toString = "%s:%s=%dx%d@%d,%d" format (app, frame, dim.width, dim.height, loc.x, loc.y)
}
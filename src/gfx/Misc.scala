package gfx

import java.awt.{Component, KeyboardFocusManager, KeyEventDispatcher}
import java.awt.event.KeyEvent
import x.{NKey, Disaster}
import javax.swing.SwingUtilities
import actors.Actor

object GlobalKeyAlert
{
	var TRACE = 0 // { 0 .. 3 }

	val APPLE = 4
	val CTRL = 2
	val ALT = 8

	private type HashType = java.util.HashMap[NKey,(() => Unit)]

	private val pressCallbacks = new HashType()
	private val releaseCallbacks = new HashType()

	private val dispatcher = new KeyEventDispatcher
	{
		override def dispatchKeyEvent(e:KeyEvent):Boolean = {
			if (TRACE > 2)
				println(e.paramString)

			if (e.getKeyCode == 0x0)
				return false

			val junk = new KeyJunk(e)
			
			val callbacks = if (junk.isPressed)
				pressCallbacks
			else
				releaseCallbacks

			var c = e.getComponent
			var relayed = false 
			
			do {
				val f = callbacks.get(key(c, junk.char, junk.mods))
				if (f != null) {
					if (TRACE > 0)
						printf("Relaying %s to %s:%s%n", junk, c.getName, c.getClass.getName)
					f()		// this is a bit tricky .... must dereference the enclosing function
					relayed = true
				}
				c = c.getParent
			} 
			while(c != null)

			if (!relayed && TRACE > 1) {
				printf("Relaying %s to nobody%n", junk)
			}

			false // take no further action = false ??
		}
	}

	KeyboardFocusManager.getCurrentKeyboardFocusManager.addKeyEventDispatcher(dispatcher)
		
	def onPress(c:Component, char:Char)(f: => Unit):Unit
		= register(pressCallbacks, c, char, 0, f)

	def onPress(c:Component, char:Char, mods:Int)(f: => Unit):Unit
		= register(pressCallbacks, c, char, mods, f)

	def onRelease(c:Component, char:Char)(f: => Unit):Unit
		= register(releaseCallbacks, c, char, 0, f)

	def onRelease(c:Component, char:Char, mods:Int)(f: => Unit):Unit
		= register(releaseCallbacks, c, char, mods, f)

	private def register(hash:HashType, c:Component, char:Char, mods:Int, f: => Unit) {
		Disaster If hash.containsKey(c)
		val f2 = () => f // coerce into map
		hash.put(key(c, char.toString, mods), f2)
	}
	
	private def key(c:Component, char:String, mods:Int)
		= new NKey(System.identityHashCode(c), char, mods)
}

private class KeyJunk(e:KeyEvent)
{
	val char = KeyEvent.getKeyText(e.getKeyCode)
	val mods = e.getModifiers
	val isPressed  = e.getID == KeyEvent.KEY_PRESSED
	val isReleased = e.getID == KeyEvent.KEY_RELEASED
	assert(isPressed || isReleased)

	override def toString
		= "%s%s %s" format (
			if (mods!=0) KeyEvent.getKeyModifiersText(mods) else "",
			char, 
			if (isPressed) "PRESSSED" else "RELEASED"
		)
}

object SwingHelper 
{
//	private var queue = new mutable.Map[Runnable]
	def invokeLater(f: => Unit) {
		SwingUtilities.invokeLater(new Runnable {
			def run() { f }
		})
	}
	
	private val worker = new Actor
	{
		def act {
			while (true) {
				receive {
					case msg:(() => Unit) => msg()
				}
			}
		}
	}
	worker.start

	def invokeAsync(f: => Unit) {
		new javax.swing.SwingWorker {
			def doInBackground():Nothing = {
				f
				null.asInstanceOf[Nothing]
			}
		}.execute
	}
}
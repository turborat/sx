package x

import collection.mutable
import java.io._
import mutable.{LinkedHashMap, ListBuffer, Map}
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import java.net.{URL, HttpURLConnection}
import scala.Console
import java.util.concurrent.{TimeUnit, ThreadFactory, Executors}

object X {
  def timeit(f: => Unit):Long = {
    val start = System.nanoTime
    f
    System.nanoTime - start
  }

	def ptime[T](id:String, disabled:Boolean=false)(f: => T):T = {
		val start = System.nanoTime
		val ret = f
		val elapsed = System.nanoTime - start
		if (!disabled)
			printf("%s took %s%n", id, prettyTime(elapsed))
		ret
	}

	private val pretties = LinkedHashMap(
		1000*1000*1000 -> "s",
		1000*1000 -> "ms",
		1000 -> "us",
		1 -> "ns"
	)
	def prettyTime(ns:Long):String = {
		def fmt(t:Long, unit:String):String = {
			val scaled = ns / t
			if (scaled < 10)
				"%.1f %s" format (ns.toDouble/t, unit)
			else
				"%d %s" format (scaled, unit)
		}

		for ((t,unit) <- pretties)
			if (ns >= t)
				return fmt(t,unit)

		"nothing"
	}

	def nof(n:Int)(f: => Any):List[Any] = {
		val list = new ListBuffer[Any]
		for (m <- 1 to n)
			list += f
		list.toList
	}

	def loop(n:Long)(f: => Unit) {
		var m = 0l
		while (m < n) {
			f
			m += 1
		}
	}

	def join(iterable:Iterable[Any], delim:String=" "):String = {
		val bldr = new StringBuilder
		val it = iterable.iterator
		while (it.hasNext) {
			bldr.append(it.next)
			if (it.hasNext) bldr.append(delim)
		}
		bldr.toString
	}

	def makeMap[I,K,V](col:Iterable[I])(f:(I) => (K,V)):Map[K,V] = {
		val map = Map[K,V]()
		for (i <- col) {
			val tuple = f(i)
			map(tuple._1) = tuple._2
		}
  	map
	}

	def makeList[N,M](it:Iterator[N])(f:(N) => (M)):List[M] = {
		val lb = new mutable.ListBuffer[M]
		while(it.hasNext) {
			val x = f(it.next)
			if (x != null) // doesn't work
				lb += x
		}
		lb.toList
	}

	def makeList[N,M](col:Iterable[N])(f:(N) => (M)):List[M]
	  = makeList(col.iterator)(f)

	def slurp(file:File):List[String] = {
		val reader = new BufferedReader(new FileReader(file))
		val lines = new ListBuffer[String]

		while(true) {
			val line = reader.readLine
			if (line == null) {
				reader.close
				return lines.toList
			}
			lines += line
		}

		Disaster("you fucked up")
	}

	def slurp(fname:String):List[String]
	  = slurp(new File(fname))

	def spew(file:File, dat:Iterable[Any], sep:String="\n") {
		val writer = new FileWriter(file)
		for (line <- dat) {
			writer write line.toString
			if (sep != null)
				writer write sep
		}
		writer.close // finally no finally
	}

	def toList[T](coll:Iterable[T]):List[T] = {
		var fun = new ListBuffer[T]
		coll foreach (fun += _)
		fun.toList
	}

	def oneOf[T](t1:T, ts:T*):Boolean = {
		for (t <- ts if t1 equals t)
			return true
		false
	}

	def objName(obj:AnyRef, warn:Boolean=true):String = {
		def clzName(clz:Class[_]):String = {
			try {
				clz.getSimpleName
			}
			catch {
				case ex : InternalError => {
					val name = clz.toString
					if (warn) Console.err.printf("%s: '%s'%n", ex, name)
					name.replaceAll("[^\\.]*\\.","")
				}
			}
		}

		var clz:Class[_] = obj.getClass
		var name = clzName(clz)

		while (name.contains("$anon$")) {
			clz = clz.getSuperclass
			name = clzName(clz)
		}

		name
	}

	def escape(fname:String)
		= fname.replaceAll("/","_")
			     .replaceAll(" ","_")
			     .replaceAll(":","-")

	private val fmt = NumberFormat.getInstance(new Locale("de","CH"))
	def fmtCH3(d:Double):String = {
		fmt.format(d)
	}

	def ?[T](cond:Boolean, ifTrue:T, ifFalse:T)
	  = if (cond) ifTrue else ifFalse

  def onLines(fname:String, ignoreHeader:Boolean=false)(f:String => Unit) = {
    val reader = new BufferedReader(new FileReader(fname))
    var go = true

    if (ignoreHeader)
      reader.readLine

    while(go) {
      val line = reader.readLine
      if (line != null)
        f(line)
      else
        go = false
    }

    reader.close
  }

  private val threadCount = new AtomicInteger
  def thread(name:String="UserThread"+threadCount.incrementAndGet, interval:Long=0)(f: => Unit) {
		new Thread(name) {
			//setDaemon(true)
			override def run {
				while(true) {
          try {
					  f
          }
          catch {
            case e => e.printStackTrace
          }
					if (interval > 0)
						Thread.sleep(interval)
          else if (interval < 0)
            return
				}
			}
		}.start
	}


	def httpGet(url:String):List[String] = {
		var con:HttpURLConnection = null
		var bis:BufferedReader = null

		try {
			con = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
			con.setDoInput(true)
			con.setRequestMethod("GET")
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

			bis = new BufferedReader(new InputStreamReader(con.getInputStream))
			val ret = new ListBuffer[String]
			var line:String = null

			while ({line = bis.readLine ; line != null}) {
				ret += line
			}

			bis.close
			con.disconnect

			ret.toList
		}
		finally {
			if (bis != null) bis.close
			if (con != null) con.disconnect
		}
	}

	private lazy val executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory {
		def newThread(r:Runnable):Thread = {
			println("Creating background executor thread") ;
			new Thread(r, "BGTHREAD") { setDaemon(true) }
		}
	})

	def schedule(delay:Long, verbose:Int=0)(f: => Unit) {
		val runnable = new Runnable {
			def run = f
		}
		if (verbose > 0) printf("scheduling %s for %s %n", runnable, TS(TS.now.ms+delay))
		executor.schedule(runnable, delay, TimeUnit.MILLISECONDS)
	}
}

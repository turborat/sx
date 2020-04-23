package x

import java.io.File
import java.io.FileWriter

import scala.annotation.elidable

import annotation.elidable.ASSERTION
import gfx.TableData

abstract class CSV(fname:String, delim:String=",")
{
	val file = new File(X.escape(fname))

	def write():CSV

	private def writeRow(writer:FileWriter, it:Iterable[Any]) {
		for (e <- it) {
			writer.write(""+e)
			writer.write(delim)
		}
		writer.write("\n")
	}

	def delete:CSV = {
		if (file.exists) {
			println("Deleting " + this)
			file.delete
			assert(!file.exists)
		}
		this
	}

	def open:CSV = {
		println("open " + this)
		Runtime.getRuntime.exec("open " + this)
		this
	}

	override def toString
	  = file.getAbsolutePath
}



object CSV
{
  def apply(data:TableData, fname:String, delim:String=",")
    = new CSV(fname,delim)
  {
    def write():CSV = {
      var writer:FileWriter = null

      try {
        writer = new FileWriter(file)

        if (data.headers != null) {
          writeRow(writer, data.headers)
        }

        for (row <- 0 until data.rows) {
          for (col <- 0 until data.cols) {
            writer.write(""+data(row,col))
            writer.write(delim)
          }
          writer.write("\n")
        }

      }
      finally {
        if (writer != null)
          writer.close
      }

      this
    }
  }



}


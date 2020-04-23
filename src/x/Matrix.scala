package x

import collection.mutable.ListBuffer
import gfx.{Points, TableData}
import scala.math._

class Matrix[T:Manifest](val rows:Int, val cols:Int) extends TableData with Iterable[MatrixNode[T]]
{
  private [x] val data = new Array[T](rows*cols)

  def apply(row:Int, col:Int) = data(row*cols+col)
  def update(row:Int, col:Int, t:T) = data(row*cols+col) = t

  override def toString = toString("%s")

  def toString(fmt:String):String = {
    val widths = new Array[Int](cols)
    val strs = new Array[String](rows*cols)
    for (node <- this) {
      val str = fmt format (node())
      if (widths(node.col) < str.length) {
        widths(node.col) = str.length
      }
      strs(node.row*cols+node.col) = str
    }

    val bldr = new StringBuilder
    for (idx <- 0 until strs.length) {
      val str = strs(idx)
      bldr.append(str)
      bldr.append(" " * (widths(idx%cols) - str.length))
      bldr.append(if ((idx+1)%cols == 0) "\n" else " ")
    }

    bldr.toString
  }

  def iterator = new Iterator[MatrixNode[T]] {
    val it = (0 until data.length).iterator
    def hasNext = it.hasNext
    def next = new MatrixNode(it.next,Matrix.this)
  }
}


class MatrixNode[T](idx:Int, mat:Matrix[T])
{
  def row = idx / mat.cols
  def col = idx - row * mat.cols
  def apply() = mat(row,col)
  def update(v:T) = mat(row,col) = v
  override def toString = "[%d,%d] = %s" format (row,col,mat(row,col))
}


class MatrixBuilder[T:Manifest]
{
	private val data = new ListBuffer[ListBuffer[T]]
	private var cols = 0

	// append to last row
	def += (e:T) {
	  if (data.isEmpty)
	    newRow
	  data.last += e
	  cols = max(data.last.size, cols)
	}

	// create new row
	def += (e1:T, eN:T*) {
		newRow
		this += e1
		for (e <- eN)
			this += e
	}

	// append new row
	def += (it:Iterable[T]) {
		newRow
		for (e <- it)
			this += e
	}

	def newRow
	  = data += new ListBuffer[T]

	def toMatrix:Matrix[T] = {
	  val mat = new Matrix[T](data.size, cols)
	  for (row <- 0 until data.size) {
	    for (col <- 0 until data(row).size) {
	      mat(row,col) = data(row)(col)
	    }
	  }
	  mat
	}

	override def toString
	  = Disaster("damn dumb")
}


object Matrix
{
	def apply(points:Points):Matrix[Double] = {
		val mb = new MatrixBuilder[Double]
		for (p <- points) {
		  mb += (p.x, p.y)
		}
		mb.toMatrix
	}

//	def fill[T:Manifest](x:Int, y:Int)(f:(Int,Int) => T):Matrix[T] = {
//		val mat = new Matrix[T](x,y)
//		for (xx <- 0 until x ; yy <- 0 until y) {
//				mat += f(xx,yy)
//			}
//		}
//		mat
//	}

//	def apply[T](arr:Array[Array[T]]):Matrix[T] = {
//	  val dim = dimensions(arr)
//		val mat = new Matrix[T]
//		arr foreach { mat += _ }
//		mat
//	}

//	def apply[T:Manifest](map:MapX[T,T]):Matrix[T] = {
//		val mat = new Matrix[T](map.size,2)
//		var row = 0
//		for ((k,v) <- map)
//			mat(row,0) = k
//			mat(row,1) = v
//		mat
//	}
}
package gfx

import x.{CSV, Matrix}
import javax.swing.table.AbstractTableModel
import collection.mutable._
import java.awt.Color
import x.MatrixBuilder

class Table(name:String, data:TableData, resizable:Boolean=false)
{
	val hdr = if (!data.headers.isEmpty)
		data.headers.toArray
	else
		Array.fill(data.cols) { "?" }

	val table = new swing.Table(toArr2, hdr) {
		showGrid = true
		gridColor = Color.BLACK
		autoResizeMode = scala.swing.Table.AutoResizeMode.AllColumns
		font = GFX.NICE_FONT

		model = new AbstractTableModel {
			override def getColumnName(column: Int) = hdr(column).toString
			def getRowCount = data.rows
			def getColumnCount = data.cols
			def getValueAt(row:Int, col:Int):AnyRef = data(row,col).asInstanceOf[AnyRef]
			override def isCellEditable(row:Int, col:Int) = false
//		      override def setValueAt(value: Any, row: Int, col: Int) {
//		        data(row,col) = value
//		        fireTableCellUpdated(row, col)
//		      }
		}

	}

	val comp =
		if (!data.headers.isEmpty) {
			new swing.ScrollPane {
				viewportView = table
				peer.setColumnHeader(null)
			}
		}
		else table

	def toArr2:Array[Array[Any]] = {
		val aa = Array.ofDim[Any](data.rows, data.cols)
		for (col <- 0 until data.cols ; row <- 0 until data.rows)
			aa(row)(col) = data(row,col)
		aa
	}

	GlobalKeyAlert.onPress(comp.peer, 'W') {
		CSV(data, name + ".csv").delete.write.open
	}

	new XFrame(name, comp, resizable).show
}


trait TableData
{
	def apply(row:Int, col:Int):Any
	def rows:Int
	def cols:Int
	val headers = new ListBuffer[String]
}


object TableTester
{
	def main(args:Array[String]) {

		val mb = new MatrixBuilder[Int]
		mb += List(1,2,3)
		mb += List(4,5,6)

		val mat = mb.toMatrix

		new Table("blah", mat)
		new Table("blah2", mat)
	}
}
package gfx

import org.jfree.data.xy.{DefaultOHLCDataset, OHLCDataItem}
import java.util
import org.jfree.chart._
import axis.{NumberAxis, DateAxis}
import org.jfree.chart.renderer.xy.CandlestickRenderer
import plot.XYPlot
import java.awt.{Color}
import fin.data.Sym

object Bloomberg
{
	def main(args:Array[String]) {
		XFrame.app = getClass.getSimpleName

		val sym = Sym("SPX")
		val ohlc = for (dd <- sym.dd.values) yield
			new OHLCDataItem(new util.Date(dd.date.ms), dd.open, dd.high, dd.low, dd.close, dd.vol)

		def plot = new XYPlot(
			new DefaultOHLCDataset(sym.name, ohlc.toArray),
			new DateAxis(null),
			new NumberAxis(null),
			null
		)

		val chart = new JFreeChart(sym.name, JFreeChart.DEFAULT_TITLE_FONT, plot, false/*legend*/) {
			setAntiAlias(false)
			setTextAntiAlias(true)
			getXYPlot.setRenderer(new CandlestickRenderer() {
				setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_SMALLEST)
				setUpPaint(Color.BLACK)
				setDownPaint(Color.BLUE)
				setSeriesOutlinePaint(0, Color.WHITE)
				setUseOutlinePaint(true)
			})
			getXYPlot.setRangeGridlinePaint(Color.GRAY)
			getXYPlot.setDomainGridlinePaint(Color.GRAY)
			getXYPlot.setBackgroundPaint(Color.BLACK)
			getTitle.setBackgroundPaint(Color.ORANGE)
//			setBackgroundPaint(Color.GREEN)
		}

//		new StandardChartTheme("JFree").apply(chart)




		val panel = new ChartPanel(chart) {
			setMouseWheelEnabled(true)
		}

		XFrame("Bloomberg", panel, true)
	}

}

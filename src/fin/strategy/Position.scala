package fin.strategy

import x.TS

class Position (buy:Tick)
{
  private var sell = Option[Tick](null)

  def close(tick:Tick) {
    assert(sell.isEmpty)
    sell = Option(tick)
    assert(buy.ts <= sell.get.ts)
  }

  def t = buy.ts.elapsedT(sell.get.ts)
  def pl = (sell.get.price - buy.price) / buy.price
  def annualPl = pl / t
  def pctMove = pl / buy.price -1

  override def toString
    = "Buy %s / Sell %s : pl:%,.2f T:%,.3f" format (buy, sell.get, pl, t)
}
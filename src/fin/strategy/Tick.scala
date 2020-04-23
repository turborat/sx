package fin.strategy

import x.TS

class Tick(val price:Double, val ts:TS) extends Signal
{
  def -> (model:Model) = model receive this
  override def toString = "%s @ %,.2f" format (ts, price)
}


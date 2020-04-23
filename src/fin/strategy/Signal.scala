package fin.strategy
import x.TS

trait Signal
{
  def ts:TS
  def -> (model:Model)
}
package fin.strategy

trait Model 
{
	final def consume(signals:Iterable[Signal]) {
		val it = signals.iterator
		while (it.hasNext) it.next -> this
	}

  def receive(tick:Tick):Unit = null
}
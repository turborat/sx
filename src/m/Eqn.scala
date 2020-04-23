package m

package misc


class Val() {
  var dep:Op = null
  private var v:Double = Double.NaN
  def apply() = v
  protected def apply(v:Double) {
    this.v = v
    if (dep != null) dep.reval
  }
  override def toString = v.toString
}

class Var(v:Double) extends Val {
  super.apply(v)
  def := (d:Double) = super.apply(d)
}

abstract class Op(a:Val, b:Val) extends Val {
  a.dep = this
  b.dep = this
  reval
  def reval
}

class Sum(a:Val, b:Val) extends Op(a,b) {
  def reval = apply(a() + b())
}

class Prod(a:Val, b:Val) extends Op(a,b) {
  def reval = apply(a() * b())
}

class Exp(a:Val, b:Val) extends Op(a,b) {
  def reval = apply(scala.math.pow(a(),b()))
}

object Eqns {
  def $(d:Double) = new Var(d)
  def +(a:Val, b:Val):Val = new Sum(a, b)
  def ^(a:Val, b:Val):Val = new Exp(a, b)
  def *(a:Val, b:Val):Val = new Prod(a, b)
  def -(a:Val, b:Val):Val = Eqns.+(a, this.-(b))
  def /(a:Val, b:Val):Val = *(a, ^(b,$(-1)))
  def -(a:Val):Val = *($(-1), a)
}
import Eqns._

object EqnTester extends App {
  val a = $(2)
  val b = $(3)
  val c = $(4)

  val x = Eqns.+(a, *(b, c))

  assert (x() == a() + b() * c())

  a := 33
  assert (x() == a() + b() * c())

  a := -1
  assert (x() == a() + b() * c())

  val y = $(2)
  val z = $(3)

  print(^(y,z))
}

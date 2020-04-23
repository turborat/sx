package fin.opt

import gfx.{RenderStyle, Graph}


object OptionExplorer extends App
{
  type OptionType = EurOptX

  def graph2(x0:Double, xN:Double, opt:Opt, name:String)(f:(Double,Opt) => Double) {
    val fCall = f(_:Double, opt.reval(call=true))
    val fPut  = f(_:Double, opt.reval(call=false))
    new Graph(name+":call", new gfx.Function(x0, xN, fCall), RenderStyle.LINES)
    new Graph(name+":put",  new gfx.Function(x0, xN, fPut),  RenderStyle.LINES)
  }

  val S=50
  val K=50
  val v=0.2
  val r=0.05
  val T=1
  val call=true

  def opt = new OptionType(S=S, K=K, v=v, r=r, T=T, call=call)

  println(opt)
  println(opt.rho, opt.reval(call=false).rho)

  graph2(0, 100, opt, "dV/dS") { (x,opt) => opt.reval(S=x).V }
  graph2(0, 100, opt, "delta") { (x,opt) => opt.reval(S=x).delta }
  graph2(0, .5, opt, "dV/dr") { (x,opt) => opt.reval(r=x).V }
  graph2(0, .5, opt, "rho") { (x,opt) => opt.reval(r=x).rho }
}

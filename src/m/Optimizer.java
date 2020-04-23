package m;

import static java.lang.String.format;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;

public abstract class Optimizer
{
  private final Parameter[] params ;
  private double max = - Double.MAX_VALUE ;
  private double[] best = null ;


  public Optimizer(Parameter...params) {
    this.params = params ;
  }


  // lus = low upper step
  public Optimizer(double...lus) {
    params = new Parameter[lus.length / 3] ;
    for(int i=0 ; i<lus.length/3 ; i++) {
      params[i] = new Parameter(lus[i*3], lus[i*3+1], lus[i*3+2]) ;
    }
  }


  protected abstract double func(double[] args) ;
  public double max() { return max ; }
  public double[] optimalParams() { return best ; }


  public double[] optimize() {
    optimize(params, new double[0]) ;
    return best ;
  }


  private void optimize(Parameter[] params, double[] args) {
    if (params.length == 0) {
      double y = func(args) ;
      if (y > max) {
        max = y ;
        best = copyOf(args, args.length);
      }
      return ;
    }

    Parameter mine = params[0] ;
    Parameter[] rest = copyOfRange(params, 1, params.length) ;   // hate this ..
    double[] newArgs = copyOf(args, args.length+1) ;

    for(int i=0 ; ; i++) {
      double x = mine.lower + i * mine.inc ;
      if (x > mine.upper) break ;
      newArgs[newArgs.length-1] = x ;
      optimize(rest, newArgs) ;
    }
  }


  public static class Parameter {
    final double lower, upper, steps, inc ;

    public Parameter(double lower, double upper, double steps) {
      assert (lower < upper) ;
      this.lower = lower ;
      this.upper = upper ;
      this.steps = steps ;
      this.inc = (upper - lower) / steps ;
    }

    @Override
    public String toString() {
      return format("Parameter(%.3f-%.3f, steps:%.3f inc:%.3f)", lower, upper, steps, inc) ;
    }
  }


  public static String asString(double[] ds) {
    StringBuilder bldr = new StringBuilder("{") ;
    for (int i=0 ; i<ds.length ; ++i) {
      bldr.append(ds[i]) ;
      if (i+1<ds.length) bldr.append(", ") ;
    }
    return bldr.append("}").toString() ;
  }
}





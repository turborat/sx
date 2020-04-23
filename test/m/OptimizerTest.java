package m;

import junit.framework.TestCase;

import java.util.concurrent.atomic.AtomicInteger;


public class OptimizerTest extends TestCase {

  public void test1() {
    Optimizer o = new Optimizer(0, 10, 10) {
      @Override
      protected double func(double[] d ) {
        return d[0] ;
      }
    } ;

    System.out.println(o.optimize()[0]) ;
    assertTrue(o.optimize()[0] == 10);
  }

  public void test2() {
    Optimizer o = new Optimizer(0, 2* java.lang.Math.PI, 100) {
      @Override
      protected double func(double[] d) {
        System.out.println(d[0] + "\t" + java.lang.Math.sin(d[0]));
        return java.lang.Math.sin(d[0]) ;
      }
    } ;

    double o1 = o.optimize()[0] ;
    System.out.println("=>" + o1);
    assertTrue(o1 == 1.5707963267948968);
  }


  public void test3() {
    Optimizer o = new Optimizer(0, 5, 100, 0, 20, 100) {
      @Override
      protected double func(double[] d) {
        return d[0] * d[1] ;
      }
    } ;

    double[] o1 = o.optimize() ;
    System.out.println(o1[0] + " " + o1[1]);
    assertTrue(o1[0] == 5);
    assertTrue(o1[1] == 20);
  }


  public void test5() {
    Optimizer o = new Optimizer(0, 6, 6, 0, 6, 6, 0, 6, 6) {
      @Override
      protected double func(double[] d) {
        return (d[0] % 4) * (d[1] % 5) * (d[2] % 6) ;
      }
    } ;

    double[] o1 = o.optimize() ;
    assertTrue(o1[0] == 3);
    assertTrue(o1[1] == 4);
    assertTrue(o1[2] == 5);
  }


  public void test6() {
    Optimizer o = new Optimizer(-22, 6, 6, -4, 6, 6, 2, 6, 6) {
      @Override
      protected double func(double[] d) {
        return (d[0] % 4) * (d[1] % 5) * (d[2] % 6) ;
      }
    } ;

    double[] o1 = o.optimize() ;

    System.out.println(o1[0]) ;
    System.out.println(o1[1]) ;
    System.out.println(o1[2]) ;

    assertTrue(o1[0] == -3.333333333333332) ;
    assertTrue(o1[1] == -4);
    assertTrue(o1[2] == 5.333333333333333);
  }


  public void test7() {

    final AtomicInteger nIter = new AtomicInteger() ;

    Optimizer o = new Optimizer(-5, 5, 10, -5, 5, 5, -5, 5, 20) {
      @Override
      protected double func(double[] d) {
        nIter.incrementAndGet();
        return 1 ;
      }
    } ;

    double[] o1 = o.optimize() ;

    assertTrue(o1[0] == -5) ;
    assertTrue(o1[1] == -5) ;
    assertTrue(o1[2] == -5) ;

    // range is inclusive hence steps+1
    assertTrue(nIter.get() == 11 * 6 * 21) ;
  }

}
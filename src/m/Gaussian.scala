package m

import scala.math._


/* Stolen from http://introcs.cs.princeton.edu/java/22library/Gaussian.java.html */

object Gaussian {
		private val ROOT_TWO_PI = sqrt(2 * Pi)

	  // standard Gaussian pdf
    def pdf(x:Double)
			= exp(-x*x / 2) / ROOT_TWO_PI

    // Gaussian pdf with mean mu and std-dev sigma
    def pdf(x:Double, mu:Double, sigma:Double):Double
			= pdf((x - mu) / sigma) / sigma

    // standard Gaussian cdf using Taylor approximation
    def cdf(z:Double):Double = {
        if (z < -8.0) return 0.0
        if (z >  8.0) return 1.0

        var sum = 0.0
			  var term = z
				var i = 3

				while(sum + term != sum) {
					sum  = sum + term
					term = term * z * z / i
      		i += 2
				}

        0.5 + sum * pdf(z)
    }

    // Gaussian cdf with mean mu and std-dev sigma
    def cdf(z:Double, mu:Double, sigma:Double):Double
			= cdf((z - mu) / sigma)

    // Compute z such that Phi(z) = y via bisection search
    private def phiInverse(y:Double):Double
			= phiInverse(y, .00000001, -8, 8)

    // bisection search
    private def phiInverse(y:Double, delta:Double, lo:Double, hi:Double):Double = {
        val mid = lo + (hi - lo) / 2;
        if (hi - lo < delta)
					return mid;
        if (cdf(mid) > y)
					phiInverse(y, delta, lo, mid);
        else
					phiInverse(y, delta, mid, hi);
    }
}
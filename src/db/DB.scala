package db

import java.sql._
import collection.mutable.ListBuffer
import scala.Array
import org.postgresql.util.PSQLException
import fin.data.Sym
import x.X


object DB
{
  val host = "dd"
  val port = 5432
  val schema = "w"
	val URL = "jdbc:postgresql://%s:%d/%s" format (host, port, schema)

  private val cons = new ThreadLocal[Connection] {
    override def initialValue = {
      val ret = DriverManager.getConnection(URL)
      printf("%s -> %s %n", ret, URL)
      ret
    }
  }

	def exec[T](sql:String, verbose:Boolean=true)(f:ResultSet => T):List[T] = {
		val st = cons.get.createStatement
		val rs = st.executeQuery(sql)
		val ret = new ListBuffer[T]
		while (rs.next) {
			ret += f(rs)
		}
		if (verbose)
			printf("%s :: returned %d rows%n", sql, ret.length)
		rs.close
		st.close
		ret.toList
	}

	def update[T](sql:String, verbose:Boolean=true) {
		try {
			if (verbose) println(sql)
			val ps = cons.get.createStatement
			if (verbose)
				printf("Updated %d rows%n", ps.executeUpdate(sql))
			ps.close
		}
	  catch {
			case e:PSQLException => Console.err.println(e.getMessage)
			System.exit(-1)
		}
	}

	def dump(q:String) {
		println (exec(q, verbose=false) { _.getString(1) }(0))
	}

	def version
		= dump("SELECT VERSION()")

	def quote(str:String):String
		= if (str == null) "null" else "'" + str + "'"

	def main(args:Array[String]) {
		version
	}
}


object DBSpeedTester
{
	def main(args:Array[String]) {
		var n = 0
		val ns = X.timeit {
			for (sym <- Sym.all ; dd <- sym.dd) {
				n += 1
			}
		}
		printf("%,d rows in %,d nanos (%,d each)%n", n, ns, ns/n)
	}
}
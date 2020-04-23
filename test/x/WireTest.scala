package misc

import m.{Stats, RNG}
import java.nio.ByteBuffer
import test.TestX
import x.{X, TheFly, WireSlzr}


abstract class TestObject {
  val i1, i2, i3: Long
  val d1, d2, d3: Double
  val s1, s2, s3: String
}


class RandomTestObject extends TestObject with Serializable
{
  val i1 = RNG.irand(1000)
  val i2 = RNG.irand(1000)
  val i3 = RNG.irand(1000)
  val d1 = RNG.rand
  val d2 = RNG.rand
  val d3 = RNG.rand
  val s1 = RNG.srand(10)
  val s2 = RNG.srand(10)
  val s3 = RNG.srand(10)
}


object TestObjectWireSlzr extends WireSlzr[TestObject]
{
  val len = 90

  def read(buf: ByteBuffer)
  = new TestObject
  {
    val i1 = buf.getLong
    val i2 = buf.getLong
    val i3 = buf.getLong
    val d1 = buf.getDouble
    val d2 = buf.getDouble
    val d3 = buf.getDouble
    val s1 = getStr(buf)
    val s2 = getStr(buf)
    val s3 = getStr(buf)
  }

  def write(buf: ByteBuffer, obj: TestObject) {
    buf.putLong(obj.i1)
    buf.putLong(obj.i2)
    buf.putLong(obj.i3)
    buf.putDouble(obj.d1)
    buf.putDouble(obj.d2)
    buf.putDouble(obj.d3)
    putStr(buf, obj.s1)
    putStr(buf, obj.s2)
    putStr(buf, obj.s3)
  }
}


class WireTest extends TestX {
  def testReflexive {
    val o1 = new RandomTestObject
    val bytes = TestObjectWireSlzr.serialize(o1)
    val o2 = TestObjectWireSlzr.deserialize(bytes)
    assertEqualsS(new TheFly(o1).members, new TheFly(o2).members)
  }
}


object WireSlzrTester extends App {
  val its = 1000 * 1000
  val slzr = TestObjectWireSlzr
  val o1 = new RandomTestObject

  {
    val stats = new Stats
    for (n <- 1 to 10) {
      var it = 0

      val start = System.nanoTime
      while (it < its) {
        slzr.serialize(o1)
        it += 1
      }
      stats << (System.nanoTime - start) / its.toDouble
    }
    println("serialization: " + stats)
  }

  {
    val stats = new Stats
    val bytes = slzr.serialize(o1)

    for (n <- 1 to 10) {
      var it = 0
      val start = System.nanoTime
      while (it < its) {
        slzr.deserialize(bytes)
        it += 1
      }
      stats << (System.nanoTime - start) / its.toDouble
    }

    println("deserialization: " + stats)
  }
}


class Ping(val n:Int) {
  override def toString = "ping(%d)" format n
}


private object PingSlzr extends WireSlzr[Ping] {
  val len = 4
  protected def write(buf:ByteBuffer, obj:Ping) = buf.putInt(obj.n)
  protected def read(buf:ByteBuffer) = new Ping(buf.getInt)
}


//object ObjWireClientTest extends App {
//  new ObjWireClient("localhost", 2222, PingSlzr) >> {
//    ping => println(ping)
//  }
//}
//
//
//object ObjWireServerTest extends App {
//  var n = 1 ;
//  val wire2 = new ObjWireServer(2222, PingSlzr)
//  while (true) {
//    wire2 << new Ping(n)
//    n += 1
//    Thread.sleep(1000)
//  }
//}
//
//
//object WireSpeedTest extends App {
//  val bufLen = 1024*48
//  var nRecv = 0l
//  var nSent = 0l
//
//  val win = new WireServer(2222)
//  X.thread(interval= -1) {
//    val buf = new Array[Byte](bufLen)
//    while (true) {
//      win << buf
//      nSent +=1
//      Thread.sleep(1000)
//    }
//  }
//
//  X.thread(interval= -1) {
//    val wos = new WireClient("localhost",2222)
//    val buf = new Array[Byte](bufLen)
//    while (true) {
//      wos >> buf
//      nRecv +=1
//    }
//  }
//
//  var lastRecv = 0l
//  var lastSent = 0l
//  X.thread(interval=1000) {
//    val totalSent = nSent
//    val totalRecv = nRecv
//    val thisSent = totalSent-lastSent
//    val thisRecv = totalRecv-lastRecv
//
//    printf("dgram[sent:%,d recv:%,d loss:%.2f%%] bytes/s:%,d %n",
//      thisSent, thisRecv, 100d*thisSent/thisRecv-100, thisRecv*bufLen) ;
//
//    lastSent = totalSent
//    lastRecv = totalRecv
//  }
//}
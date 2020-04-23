package x

import java.nio.{Buffer, ByteBuffer}
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import java.io.{ByteArrayInputStream, ObjectInputStream, ObjectOutputStream}
import java.net.{DatagramPacket, InetSocketAddress, DatagramSocket}


trait WireSlzr[T]
{
  val len: Int
  protected def write(buf:ByteBuffer, obj: T)
  protected def read(buf:ByteBuffer): T

  private def assertCapacity(buf: Buffer) {
    if (buf.hasRemaining) {
      Disaster("buffer should be " + (len - buf.remaining) + " bytes")
    }
  }

  protected final def getStr(buf: ByteBuffer): String = {
    val len = buf.getInt
    val bytes = new Array[Byte](len)
    buf.get(bytes, buf.arrayOffset, len)
    new String(bytes)
  }

  protected final def putStr(buf: ByteBuffer, str: String) {
    buf.putInt(str.length)
    buf.put(str.getBytes)
  }

  final def serialize(obj: T): Array[Byte] = {
    val buf = ByteBuffer.allocate(len)
    write(buf,obj)
    assertCapacity(buf)
    buf.array
  }

  final def deserialize(bytes: Array[Byte]): T = {
    val buf = ByteBuffer.wrap(bytes)
    val ret = read(buf)
    assertCapacity(buf)
    ret
  }
}


class Wire
{
  private var incoming:DatagramSocket = null
  private var outgoing:DatagramSocket = null

  def bind(port:Int):Wire = {
    val addr = new InetSocketAddress(port)
    printf("bind(%s)%n",addr)
    incoming = new DatagramSocket(null)
    incoming.bind(addr)
    this
  }

  def connect(host:String,port:Int):Wire = {
    val addr = new InetSocketAddress(host,port)
    printf("connect(%s)%n",addr)
    outgoing = new DatagramSocket(null)
    outgoing.connect(addr)
    this
  }

  def << (bytes:Array[Byte]) {
    val packet = new DatagramPacket(bytes, bytes.length)
    outgoing.send(packet)
  }

  def >> (bytes:Array[Byte]) {
    val packet = new DatagramPacket(bytes, bytes.length)
    incoming.receive(packet)
  }

  def destroy {
    if (incoming != null) incoming.close
    if (outgoing != null) outgoing.close
  }
}


class ObjWire[T](slzr:WireSlzr[T])
{
  private val wire = new Wire

  def bind(port:Int):ObjWire[T] = {
    wire.bind(port)
    this
  }

  def connect(host:String,port:Int):ObjWire[T] = {
    wire.connect(host, port)
    this
  }

  def destroy = wire.destroy

  def << (obj:T) = wire << slzr.serialize(obj)

  def >> (f:T => Unit) {
    val buf = new Array[Byte](slzr.len)
    X.thread("ObjWire>>") {
      wire >> buf
      f(slzr.deserialize(buf))
    }
  }
}


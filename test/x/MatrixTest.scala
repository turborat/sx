package x

import test.TestX
import gfx.Points

class MatrixTest extends TestX
{
  def test1 {
    val m = new Matrix[Int](2,3)
    m(1,1) = 22
    assertEquals(22, m(1,1))
  }

  def testDim {
		val m = new Matrix[Double](4,7)
		assertEqualsS(4, m.rows)
		assertEqualsS(7, m.cols)
		val m2 = new Matrix[String](17,42)
		assertEqualsS(17, m2.rows)
		assertEqualsS(42, m2.cols)
	}

	def testIterator {
		val mb = new MatrixBuilder[Int]
		mb += List(1,2,3)
		mb += Array(4,5)

		val it = mb.toMatrix.iterator
		assertEqualsS("[0,0] = 1", it.next)
		assertEqualsS("[0,1] = 2", it.next)
		assertEqualsS("[0,2] = 3", it.next)
		assertEqualsS("[1,0] = 4", it.next)
		assertEqualsS("[1,1] = 5", it.next)
		assertEqualsS("[1,2] = 0", it.next)
		assertFalse(it.hasNext)
	}

	def testNode {
	  val mat = new Matrix[Int](2,3)
	  val it = mat.iterator
	  assertEqualsS("[0,0] = 0", it.next)
	  assertEqualsS("[0,1] = 0", it.next)
    assertEqualsS("[0,2] = 0", it.next)
    assertEqualsS("[1,0] = 0", it.next)
    assertEqualsS("[1,1] = 0", it.next)
    assertEqualsS("[1,2] = 0", it.next)
    assertFalse(it.hasNext)

    mat(1,1) = -3
    for (node <- mat) {
      if (node.row == 1 && node.col == 1)
        assertEqualsS(-3, node())
      else
        assertEquals(0, node())
    }
	}

	def testToString {
    val mat = new Matrix[Int](2,3)
    assertEqualsS("0 0 0\n0 0 0\n", mat)
    mat(0,1) = 666
    println(mat)
    assertEqualsS("0 666 0\n0 0   0\n", mat)
	}

	def testToString2 {
    val mat = new Matrix[Double](3,2)
    assertEqualsS("0.0 0.0\n0.0 0.0\n0.0 0.0\n", mat)

    mat(1,0) = 3.333
    assertEqualsS("0.0   0.0\n3.333 0.0\n0.0   0.0\n", mat)
    assertEqualsS("0.00 0.00\n3.33 0.00\n0.00 0.00\n", mat.toString("%.2f"))
    assertEqualsS("x x\nx x\nx x\n", mat.toString("x"))
	}

  def testPoints {
    val points = new Points
    points += (1,2)
    points += (2,4)
    points += (3,9)
    val mat = Matrix(points)
    assertEqualsS("1.0 2.0\n2.0 4.0\n3.0 9.0\n", mat)
  }
}

class MatrixBuilderTest extends TestX
{
  def test1 {
    val mb = new MatrixBuilder[Int]
    mb += 1
    mb += 2
    mb.newRow
    mb += 3

    val m = mb.toMatrix
    assertEquals(1, m(0,0))
    assertEquals(2, m(0,1))
    assertEquals(3, m(1,0))
    assertEqualsS("1 2\n3 0\n", mb.toMatrix)
  }

  def testAppendRow {
    val mb = new MatrixBuilder[Int]
    mb += List(1,2,3)
    mb += Array(4,5,6)

    val mat = mb.toMatrix
    assertEqualsS(2, mat.rows)
    assertEqualsS(3, mat.cols)
    assertEquals(1, mat(0,0))
    assertEquals(2, mat(0,1))
    assertEquals(3, mat(0,2))
    assertEquals(4, mat(1,0))
    assertEquals(5, mat(1,1))
    assertEquals(6, mat(1,2))
  }

  def test00 {
    val m = new MatrixBuilder[Int]().toMatrix
    assertEqualsS("", m)
    assertEquals(0, m.rows)
    assertEquals(0, m.cols)
  }

  def test11 {
    val m = new MatrixBuilder[Int]{ this += 11 }.toMatrix
    assertEquals(1, m.rows)
    assertEquals(1, m.cols)
    assertEqualsS("11\n", m)
  }

  def testNewRow {
    val mb = new MatrixBuilder[Int]

    mb += (3, 4, 5)
    assertEqualsS("3 4 5\n", mb.toMatrix)

    mb += (3, 4, 5)
    assertEqualsS("3 4 5\n3 4 5\n", mb.toMatrix)
  }
}

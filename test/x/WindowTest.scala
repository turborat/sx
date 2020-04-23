package x
import test.TestX

class WindowTest extends TestX
{
  def test1 {
    val win = new Window[Double](3)
    assertEqualsS("[]", win)
    win += 1
    assertEqualsS("[1.0]", win)
    assertFalse(win.full)
    win += 2
    assertEqualsS("[1.0, 2.0]", win)
    assertFalse(win.full)
    win += 3
    assertEqualsS("[1.0, 2.0, 3.0]", win)
    assertTrue(win.full)
    win += 4
    assertEqualsS("[2.0, 3.0, 4.0]", win)
    assertTrue(win.full)
    win += 5
    assertEqualsS("[3.0, 4.0, 5.0]", win)
    assertTrue(win.full)
    win += 6
    assertEqualsS("[4.0, 5.0, 6.0]", win)
    assertTrue(win.full)
    win += 7
    assertEqualsS("[5.0, 6.0, 7.0]", win)
    assertTrue(win.full)
  }
}

class DoubleWinTest extends TestX
{
  def test1 {
    val win = new DoubleWin(3)
    assertEqualsS("[]", win)
    assertEquals(0, win.avg)
    win += 1
    assertEqualsS("[1.0]", win)
    assertEquals(1, win.avg)
    win += 2
    assertEqualsS("[1.0, 2.0]", win)
    assertEquals(1.5, win.avg)
    win += 3
    assertEqualsS("[1.0, 2.0, 3.0]", win)
    assertEquals(2, win.avg)
    win += 4
    assertEqualsS("[2.0, 3.0, 4.0]", win)
    assertEquals(3, win.avg)
    win += 5
    assertEqualsS("[3.0, 4.0, 5.0]", win)
    assertEquals(4, win.avg)
    win += 6
    assertEqualsS("[4.0, 5.0, 6.0]", win)
    assertEquals(5, win.avg)
    win += 7
    assertEqualsS("[5.0, 6.0, 7.0]", win)
    assertEquals(6, win.avg)
  }
}
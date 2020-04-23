package test

import junit.framework.{AssertionFailedError, TestCase, Assert}


class TestJUnit extends TestCase
{
  def test1() {
    Assert.assertTrue(true)
  }

  def testFail {
		try {
    	Assert.fail
		}
		catch {
			case e:AssertionFailedError =>
		}
  }
}
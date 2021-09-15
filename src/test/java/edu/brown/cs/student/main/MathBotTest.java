package edu.brown.cs.student.main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathBotTest {

  @Test
  public void testAddition() {
    MathBot matherator9000 = new MathBot();
    double output = matherator9000.add(10.5, 3);
    assertEquals(13.5, output, 0.01);
  }

  @Test
  public void testLargerNumbers() {
    MathBot matherator9001 = new MathBot();
    double output = matherator9001.add(100000, 200303);
    assertEquals(300303, output, 0.01);
  }

  @Test
  public void testSubtraction() {
    MathBot matherator9002 = new MathBot();
    double output = matherator9002.subtract(18, 17);
    assertEquals(1, output, 0.01);
  }

  @Test
  public void testNegativeAddition() {
    MathBot matherator9003 = new MathBot();
    double output = matherator9003.add(4, -1);
    assertEquals(3.0, output, 0.01);
  }

  @Test
  public void testAllNegativeAddition() {
    MathBot matherator9008 = new MathBot();
    double output = matherator9008.add(-4, -1);
    assertEquals(-5.0, output, 0.01);
  }

  @Test
  public void testZeroAddition() {
    MathBot matherator9004 = new MathBot();
    double output = matherator9004.add(0, 0);
    assertEquals(0.0, output, 0.01);
  }

  @Test
  public void testAdditionWithZero() {
    MathBot matherator9005 = new MathBot();
    double output = matherator9005.add(4, 0);
    assertEquals(4.0, output, 0.01);
  }

  @Test
  public void testNegativeSubtraction() {
    MathBot matherator9006 = new MathBot();
    double output = matherator9006.subtract(4, -1);
    assertEquals(5.0, output, 0.01);
  }

  @Test
  public void testAllNegativeSubtraction() {
    MathBot matherator9007 = new MathBot();
    double output = matherator9007.subtract(-4, -1);
    assertEquals(-3.0, output, 0.01);
  }

  @Test
  public void testAllNegativeSubtractionFlipped() {
    MathBot matherator9011 = new MathBot();
    double output = matherator9011.subtract(-1, -4);
    assertEquals(3.0, output, 0.01);
  }

  @Test
  public void testZeroSubtraction() {
    MathBot matherator9009 = new MathBot();
    double output = matherator9009.subtract(0, 4);
    assertEquals(-4.0, output, 0.01);
  }

  @Test
  public void testZeroSubtractionFlipped() {
    MathBot matherator9010 = new MathBot();
    double output = matherator9010.subtract(4, 0);
    assertEquals(4.0, output, 0.01);
  }

  @Test
  public void testSubtractionWithDecimals() {
    MathBot matherator9011 = new MathBot();
    double output = matherator9011.subtract(4.0, 1.0);
    assertEquals(3.0, output, 0.01);
  }
}

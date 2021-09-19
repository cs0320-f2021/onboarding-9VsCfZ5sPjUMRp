package edu.brown.cs.student.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StarHandlerTest {

  @Test
  public void testLoadValidStarInfo() {
    StarHandler starHandler1 = new StarHandler();
    try {
      starHandler1.loadStarInfo("data/stars/ten-star.csv");
    } catch (Exception e) {
      System.out.println("ERROR");
    }

    assertEquals(10, starHandler1.getNumListOfStars());
  }

  @Test
  public void testInvalidStarInfoHeader() {
    StarHandler starHandler2 = new StarHandler();
    Exception exception = assertThrows(Exception.class, () -> {
      starHandler2.loadStarInfo("data/stars/invalid-star-header.csv");
    });

    String message = "Invalid header; needs to have StarID, ProperName, X, Y, Z.";

    assertEquals(message, exception.getMessage());
  }

//  @Test
//  public void testInvalidStarData() {
//    StarHandler
//  }
//
//  @Test
//  public void testNoStars() {
//
//  }
}

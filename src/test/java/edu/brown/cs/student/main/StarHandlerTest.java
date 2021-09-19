package edu.brown.cs.student.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StarHandlerTest {

  @Test
  public void testLoadValidStarInfo10() {
    StarHandler starHandler1 = new StarHandler();
    try {
      starHandler1.loadStarInfo("data/stars/ten-star.csv");
    } catch (Exception e) {
      System.out.println("ERROR");
    }

    assertEquals(10, starHandler1.getNumListOfStars());
  }

  @Test
  public void testLoadValidStarInfo1() {
    StarHandler starHandler6 = new StarHandler();
    try {
      starHandler6.loadStarInfo("data/stars/one-star.csv");
    } catch (Exception e) {
      System.out.println("ERROR");
    }

    assertEquals(1, starHandler6.getNumListOfStars());
  }

  @Test
  public void testLoadValidStarInfo3() {
    StarHandler starHandler7 = new StarHandler();
    try {
      starHandler7.loadStarInfo("data/stars/three-star.csv");
    } catch (Exception e) {
      System.out.println("ERROR");
    }

    assertEquals(3, starHandler7.getNumListOfStars());
  }

  @Test
  public void testLoadValidStarInfoBig() {
    StarHandler starHandler8 = new StarHandler();
    try {
      starHandler8.loadStarInfo("data/stars/stardata.csv");
    } catch (Exception e) {
      System.out.println("ERROR");
    }

    assertEquals(119617, starHandler8.getNumListOfStars());
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

  @Test
  public void testIncorrectNumberStarInfo() {
    StarHandler starHandler3 = new StarHandler();
    Exception exception = assertThrows(Exception.class, () -> {
      starHandler3.loadStarInfo("data/stars/invalid-star-data.csv");
    });

    String message = "Incorrect number of inputs for star: 1,Lonely Star,5,-2.24,";

    assertEquals(message, exception.getMessage());
  }

  @Test
  public void testNoStars() {
    StarHandler starHandler4 = new StarHandler();
    try {
      starHandler4.loadStarInfo("data/stars/no-star.csv");
    } catch (Exception e) {
      System.out.println("ERROR");
    }

    assertEquals(0, starHandler4.getNumListOfStars());
  }

  @Test
  public void testInvalidStarInfoType() {
    StarHandler starHandler5 = new StarHandler();
    Exception exception = assertThrows(Exception.class, () -> {
      starHandler5.loadStarInfo("data/stars/invalid-star-data-type.csv");
    });

    String message = "Incorrect format: 1,Lonely Star,5,-2.24,no";

    assertEquals(message, exception.getMessage());
  }

  @Test
  public void testKNNCoordValid() {
    StarHandler starHandler1 = new StarHandler();
    try {
      starHandler1.loadStarInfo("data/stars/ten-star.csv");
    } catch (Exception e) {
      System.out.println("ERROR");
    }

    List<Star> list = starHandler1.naiveNeighbors(5, 0, 0, 0);
    List<Star> listActual = new ArrayList<>(Arrays.asList(70667
        71454
        71457
        87666));

    listActual.addAll()

  }
}

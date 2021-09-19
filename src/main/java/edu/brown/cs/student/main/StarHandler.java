package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * class that handles all of the operations with Stars
 */
class StarHandler {
  /** list to hold star data */
  private List<Star> listOfStars = null;

  /**
   * method to get number of stars in listOfStars
   * @return int
   */
  public int getNumListOfStars() {
    if (listOfStars == null) {
      return 0;
    } else {
      return listOfStars.size();
    }
  }

  /**
   * Method to read CSV and load star info into a list of stars
   * @param filename
   * @throws Exception
   */
  public void loadStarInfo(String filename) throws Exception {
    listOfStars = new ArrayList<>();

    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String line = reader.readLine();

    // make sure first line is a header
    if (!validateHeader(line)) {
      String message = "Invalid header; needs to have StarID, ProperName, X, Y, Z.";
      System.out.println("ERROR: " + message);
      throw new Exception(message);
    }

    line = reader.readLine();

    // create stars out of lines and add to returnList
    while (line != null) {
      listOfStars.add(createStar(line));
      line = reader.readLine();
    }
    reader.close();

    System.out.println("Read " + listOfStars.size() + " stars from " + filename);
  }

  /**
   * Method to determine if a line is a valid header or not
   * @param header String
   * @return boolean
   */
  private boolean validateHeader(String header) {
    String[] headerComponents = header.split(",");

    // checks if header is correct length and has correct components
    return headerComponents.length == 5 && headerComponents[0].equals("StarID") &&
        headerComponents[1].equals("ProperName") && headerComponents[2].equals("X") &&
        headerComponents[3].equals("Y") && headerComponents[4].equals("Z");
  }

  /**
   * Method to create a star from a line
   * @param line String
   * @return Star
   * @throws Exception
   */
  private Star createStar(String line) throws Exception {
    String[] lineComponents = line.split(",");

    // checks if line has the correct number of components for a star
    if (lineComponents.length != 5) {
      String message = "Incorrect number of inputs for star: " + line;
      System.out.println("ERROR: " + message);
      throw new Exception(message);
    }

    Star star = null;
    try {
      star = new Star(Integer.parseInt(lineComponents[0]), lineComponents[1],
          Float.parseFloat(lineComponents[2]), Float.parseFloat(lineComponents[3]),
          Float.parseFloat(lineComponents[4]));
    } catch (Exception e) {
      String message = "Incorrect format: " + line;
      System.out.println("ERROR: " + message);
      throw new Exception(message);
    }

    return star;
  }

  /**
   * method that returns list of closest stars to given coordinates
   * @param k number of stars to return
   * @param x x coordinate
   * @param y y coordinate
   * @param z z coordinate
   * @return list of stars
   */
  public List<Star> naiveNeighbors(int k, float x, float y, float z) {
    // if there hasn't been any file read in, print error
    if (listOfStars == null) {
      System.out.println("ERROR: Load in star data first");
      return null;
    }

    // if there are fewer stars than the number of stars to return, return all the stars
    if (listOfStars.size() <= k) {
      return listOfStars;
    }

    // create a TreeMap to hold distance to list of stars
    Map<Float, List<Star>> distanceToStar = new TreeMap<>();

    // loop through stars, calculate distance from the coordinates, add into TreeMap with key
    // distance and value list of stars
    for (Star star : this.listOfStars) {
      float distance = star.calculateStarDistanceCoord(x, y, z);

      if (distanceToStar.containsKey(distance)) {
        List<Star> list = distanceToStar.get(distance);
        list.add(star);
      } else {
        List<Star> list = new ArrayList<>();
        list.add(star);
        distanceToStar.put(distance, list);
      }
    }

    List<Star> returnList = new ArrayList<>();

    // while loop to add k stars to returnList
    while (returnList.size() < k) {
      // iterate through keys (which are sorted since it's in a TreeMap)
      for (float distance : distanceToStar.keySet()) {
        List<Star> stars = distanceToStar.get(distance);

        // if adding all stars in list makes returnList's new size less than or equal to k,
        // add all stars in the list
        if (k >= (returnList.size() + stars.size())) {
          returnList.addAll(stars);
        } else {
          List<Star> listCopy = new ArrayList<>(stars);
          int size = returnList.size();
          // if adding all the stars in this list makes returnList exceed k, find number of stars
          // that need to be added for returnList to equal k
          int neededStars = k - size;

          // get random stars from list, then delete star from copy and repeat until you get
          // neededStars
          for (int i = 0; i < neededStars; i++) {
            Random rand = new Random();

            int randIndex = rand.nextInt(listCopy.size());
            returnList.add(listCopy.get(randIndex));
            listCopy.remove(randIndex);
          }
        }
      }
    }

    return returnList;
  }

  /**
   * method that returns list of length k of closest stars to given star
   * @param k number of stars to return
   * @param starName given star name
   * @return list of stars
   */
  public List<Star> naiveNeighbors(int k, String starName) {
    Star inputStar = null;

    // find the star with starName in listOfStars
    for (Star star : listOfStars) {
      if (star.getProperName().equals(starName)) {
        inputStar = star;
      }
    }

    // if there is a star with given name, call the other naiveNeighbors with star coordinates,
    // get k + 1 stars, and remove the first one (since this is just the input star)
    if (inputStar != null) {
      List<Star> list = naiveNeighbors(k + 1, inputStar.getX(), inputStar.getY(), inputStar.getZ());
      list.remove(0);
      return list;
    } else {
      return null;
    }
  }
}

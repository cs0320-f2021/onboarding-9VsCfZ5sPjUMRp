package edu.brown.cs.student.main;

/**
 * class for Star
 */
class Star {
  /** StarID */
  private final int starID;
  /** {ProperName} */
  private final String properName;
  /** X coord */
  private final float x;
  /** Y coord */
  private final float y;
  /** Z coord */
  private final float z;

  /**
   * constructor for Star
   * @param starID
   * @param properName
   * @param x
   * @param y
   * @param z
   */
  protected Star(int starID, String properName, float x, float y, float z) {
    this.starID = starID;
    this.properName = properName;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * getter for properName
   * @return name
   */
  public String getProperName() {
    return properName;
  }

  /**
   * getter for starID
   * @return id
   */
  public int getStarID() {
    return starID;
  }

  /**
   * getter for x coordinate
   * @return x
   */
  public float getX() {
    return x;
  }

  /**
   * getter for y coordinate
   * @return y
   */
  public float getY() {
    return y;
  }

  /**
   * getter for z coordinate
   * @return z
   */
  public float getZ() {
    return z;
  }

  /**
   * Method to calculate Euclidean distance between star and given coordinates
   * @param xCoord - x coordinate
   * @param yCoord - y coordinate
   * @param zCoord - z coordinate
   * @return float, euclidean distance between this star and the coordinates
   */
  public float calculateStarDistanceCoord(float xCoord, float yCoord, float zCoord) {
    return (float) Math.sqrt((xCoord - this.x) * (xCoord - this.x)
        + (yCoord - this.y) * (yCoord - this.y) + (zCoord - this.z) * (zCoord - this.z));
  }
}

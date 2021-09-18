package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Random;

import com.google.common.collect.ImmutableMap;

import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  // use port 4567 by default when running server
  private static final int DEFAULT_PORT = 4567;

  /** list to hold star data */
  private List<Star> listOfStars = null;

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    // set up parsing of command line flags
    OptionParser parser = new OptionParser();

    // "./run --gui" will start a web server
    parser.accepts("gui");

    // use "--port <n>" to specify what port on which the server runs
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);

    OptionSet options = parser.parse(args);
    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
      String input;
      while ((input = br.readLine()) != null) {
        try {
          input = input.trim();
          String[] arguments = input.split(" ");
          // declare new MathBot
          MathBot mathBot = new MathBot();

          if (arguments[0].equals("add")) {
            // if first argument is "add," use mathbot to add the next 2 arguments
            double sum =
                  mathBot.add(Double.parseDouble(arguments[1]), Double.parseDouble(arguments[2]));

            System.out.println(sum);
          } else if (arguments[0].equals("subtract")) {
            // if first argument is "subtract," use mathbot to subtract the next 2 arguments
            double difference = mathBot.subtract(Double.parseDouble(arguments[1]),
                  Double.parseDouble(arguments[2]));

            System.out.println(difference);
          } else if (arguments[0].equals("stars")) {
            // if first argument is "stars," use loadStarInfo to read csv file into list
            listOfStars = loadStarInfo(arguments[1]);

            System.out.println("Read " + listOfStars.size() + " stars from " + arguments[1]);
          } else if (arguments[0].equals("naive_neighbors")) {
            // if there hasn't been any file read in, print error
            if (listOfStars == null) {
              System.out.println("ERROR: Load in star data first");
            } else if (arguments.length == 5) {
              // if arguments length is 5, then use the naiveNeighbors with 4 arguments
              List<Star> list = naiveNeighbors(Integer.parseInt(arguments[1]),
                  Float.parseFloat(arguments[2]),
                  Float.parseFloat(arguments[3]),
                  Float.parseFloat(arguments[4]));

              // print out star IDs
              for (Star star : list) {
                System.out.println(star.getStarID());
              }
            } else if (arguments.length == 3) {
              // if arguments length is 3, check that name has quotations and then get the
              // name out
              String name = arguments[2];

              if (name.charAt(0) == '"' && name.charAt(name.length() - 1) == '"') {
                // get the name from the quotations and call naiveNeighbors
                List<Star> list = naiveNeighbors(Integer.parseInt(arguments[1]),
                    name.substring(1, name.length() - 1));

                // print out star IDs if list is null, otherwise star does not exist in database
                if (list != null) {
                  for (Star star : list) {
                    System.out.println(star.getStarID());
                  }
                } else {
                  System.out.println("ERROR: Star with name " + name + " does not exist in the database");
                }
              } else {
                System.out.println("ERROR: Name must have quotations around it");
              }
            }
          } else {
            // invalid input
            System.out.println("ERROR: We couldn't process your input");
          }
        } catch (Exception e) {
//          e.printStackTrace();
          System.out.println("ERROR: We couldn't process your input");
        }
      }
    } catch (Exception e) {
//      e.printStackTrace();
      System.out.println("ERROR: Invalid input for REPL");
    }

  }

  /**
   * Method to read CSV and load star info into a list of stars
   * @param filename
   * @return List of stars
   * @throws Exception
   */
  private static List<Star> loadStarInfo(String filename) throws Exception {
    List<Star> returnList = new ArrayList<>();

    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String line = reader.readLine();

    // make sure first line is a header
    if (!validateHeader(line)) {
      throw new Exception("Invalid header; needs to have StarID, ProperName, X, Y, Z.");
    }

    line = reader.readLine();

    // create stars out of lines and add to returnList
    while (line != null) {
      returnList.add(createStar(line));
      line = reader.readLine();
    }
    reader.close();

    return returnList;
  }

  /**
   * Method to determine if a line is a valid header or not
   * @param header String
   * @return boolean
   */
  private static boolean validateHeader(String header) {
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
  private static Star createStar(String line) throws Exception {
    String[] lineComponents = line.split(",");

    // checks if line has the correct number of components for a star
    if (lineComponents.length != 5) {
      throw new Exception("Incorrect number of inputs for star: " + line);
    }

    Star star = null;
    try {
      star = new Star(Integer.parseInt(lineComponents[0]), lineComponents[1],
          Float.parseFloat(lineComponents[2]), Float.parseFloat(lineComponents[3]),
          Float.parseFloat(lineComponents[4]));
    } catch (Exception e) {
      throw new Exception("Incorrect format: " + line);
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
  private List<Star> naiveNeighbors(int k, float x, float y, float z) {
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
  private List<Star> naiveNeighbors(int k, String starName) {
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

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration(Configuration.VERSION_2_3_0);

    // this is the directory where FreeMarker templates are placed
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    // set port to run the server on
    Spark.port(port);

    // specify location of static resources (HTML, CSS, JS, images, etc.)
    Spark.externalStaticFileLocation("src/main/resources/static");

    // when there's a server error, use ExceptionPrinter to display error on GUI
    Spark.exception(Exception.class, new ExceptionPrinter());

    // initialize FreeMarker template engine (converts .ftl templates to HTML)
    FreeMarkerEngine freeMarker = createEngine();

    // setup Spark Routes
    Spark.get("/", new MainHandler(), freeMarker);
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler<Exception> {
    @Override
    public void handle(Exception e, Request req, Response res) {
      // status 500 generally means there was an internal server error
      res.status(500);

      // write stack trace to GUI
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * A handler to serve the site's main page.
   *
   * @return ModelAndView to render.
   * (main.ftl).
   */
  private static class MainHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      // this is a map of variables that are used in the FreeMarker template
      Map<String, Object> variables = ImmutableMap.of("title",
          "Go go GUI");

      return new ModelAndView(variables, "main.ftl");
    }
  }

  /**
   * A helper class to store star information.
   */
  private static class Star {
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
}

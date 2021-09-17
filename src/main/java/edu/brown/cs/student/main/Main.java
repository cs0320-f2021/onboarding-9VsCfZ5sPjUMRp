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

    //  Add your REPL here!
    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
      String input;
      while ((input = br.readLine()) != null) {
        try {
          input = input.trim();
          String[] arguments = input.split(" ");
          // declare new MathBot
          MathBot mathBot = new MathBot();

          // parse the 2nd and 3rd values in argument into Doubles, if first is "add" or "subtract,"
          // do the appropriate operation with MathBot and print result
          if (arguments[0].equals("add")) {
            double sum =
                  mathBot.add(Double.parseDouble(arguments[1]), Double.parseDouble(arguments[2]));
            System.out.println(sum);
          } else if (arguments[0].equals("subtract")) {
            double difference = mathBot.subtract(Double.parseDouble(arguments[1]),
                  Double.parseDouble(arguments[2]));
            System.out.println(difference);
          } else if (arguments[0].equals("stars")) {
            listOfStars = loadStarInfo(arguments[1]);
            System.out.println("Read " + listOfStars.size() + " stars from " + arguments[1]);
          } else if (arguments[0].equals("naive_neighbors")) {
            if (listOfStars == null) {
              System.out.println("ERROR: Load in star data first");
            }

            if (arguments.length == 5) {
              naiveNeighbors(Integer.parseInt(arguments[1]),
                  Float.parseFloat(arguments[2]),
                  Float.parseFloat(arguments[3]),
                  Float.parseFloat(arguments[4]));
            } else if (arguments.length == 3) {
              String name = arguments[2];
              if (name.charAt(0) == '"' && name.charAt(name.length() - 1) == '"') {
                naiveNeighbors(Integer.parseInt(arguments[1]),
                    name.substring(1, name.length() - 1));
              } else {
                System.out.println("ERROR: Name must have quotations around it");
              }
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
          System.out.println("ERROR: We couldn't process your input");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("ERROR: Invalid input for REPL");
    }

  }

  private static List<Star> loadStarInfo(String filename) throws Exception {
    List<Star> returnList = new ArrayList<>();

    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String line = reader.readLine();

    if (!validateHeader(line)) {
      throw new Exception("Invalid header; needs to have StarID, ProperName, X, Y, Z.");
    }

    line = reader.readLine();

    while (line != null) {
      returnList.add(createStar(line));
      line = reader.readLine();
    }
    reader.close();

    return returnList;
  }

  private static boolean validateHeader(String header) {
    String[] headerComponents = header.split(",");

    return headerComponents.length == 5 && headerComponents[0].equals("StarID") &&
        headerComponents[1].equals("ProperName") && headerComponents[2].equals("X") &&
        headerComponents[3].equals("Y") && headerComponents[4].equals("Z");
  }

  private static Star createStar(String line) throws Exception {
    String[] lineComponents = line.split(",");

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
  private void naiveNeighbors(int k, float x, float y, float z) {
    if (listOfStars.size() <= k) {
      for (Star star : listOfStars) {
        System.out.println(star.getStarID());
      }
      return;
    }

    Map<Float, List<Star>> distanceToStar = new TreeMap<>();

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

    while (returnList.size() < k) {
      for (float distance : distanceToStar.keySet()) {
        List<Star> stars = distanceToStar.get(distance);

        if (k >= (returnList.size() + stars.size())) {
          returnList.addAll(stars);
        } else {
          List<Star> listCopy = new ArrayList<>(stars);
          int size = returnList.size();
          int neededStars = k - size;

          for (int i = 0; i < neededStars; i++) {
            Random rand = new Random();

            int randIndex = rand.nextInt(listCopy.size());
            returnList.add(listCopy.get(randIndex));
            listCopy.remove(randIndex);
          }
        }
      }
    }

    for (Star star : returnList) {
      System.out.println(star.getStarID());
    }
  }

  /**
   * method that returns list of length k of closest stars to given star
   * @param k number of stars to return
   * @param starName given star name
   * @return list of stars
   */
  private void naiveNeighbors(int k, String starName) {
    Star inputStar = null;
    for (Star star : listOfStars) {
      if (star.getProperName().equals(starName)) {
        inputStar = star;
      }
    }

    if (inputStar != null) {
      naiveNeighbors(k, inputStar.getX(), inputStar.getY(), inputStar.getZ());
    } else {
      System.out.println("The star with name " + starName + " does not exist in the data");
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

# README

## Onboarding Project Details

---

This project implements a simple REPL that can take in several inputs and perform
the given functions:

1. `add <num1> <num2>`: prints the sum of the two numbers
2. `subtract <num1> <num2>`: prints the difference of the two numbers
3. `stars <filepath>`: loads in a CSV file of star data, prints a message saying how many stars were read in
4. `naive_neighbors <k> <x> <y> <z>`: returns the `k` closest stars to the given `xyz`-coordinates
5. `naive_neighbors <k> <"Star name">`: returns the `k` closest stars to the given star name, as long as star name is in quotations

If there are invalid inputs (e.g. incorrectly spelled commands, invalid CSVs, incorrectly formatted CSVs, etc.), the REPL will print an informative
error message but will not terminate.

## Design Choices

---
The REPL is implemented in the `Main.java` class, which uses `MathBot.java`, `StarHandler.java` and `Star.java` 
to process the input commands. 

`Main.java` processes the inputs, and depending on the first argument, will call the 
appropriate method. 

`Star.java` is a class that stores the information for a star, including its starID, proper name, and xyz-coordinates.
It includes a constructor, getters for each of its fields, and a method that calculates the star's Euclidean
distance from a given set of coordinates.

`StarHandler.java` implements the methods for the REPL: it reads in the CSV files and performs
the naiveNeighbors operations. It includes helper functions for these methods, such as one that determines whether
a header is valid for a CSV, and a helper method that creates a star. Stars from the CSV are added to a `listOfStars`
in `StarHandler`.

The two naiveNeighbors methods
work in virtually the same way, so to avoid duplicate code, the method `naiveNeighbors(k, x, y, z)` 
had the full implementation, while `naiveNeighbors(k, starName)` called the former. In order
to avoid the latter returning the input star as one of its closest neighbors, it calls the
other `naiveNeighbors` with `k + 1` neighbors, and then removes the first star in the list (which
is itself, since the stars are sorted by distance).

`naiveNeighbors` uses a `TreeMap`, where the key-value pairs are distance to a list of stars. 
A TreeMap was used because the keys would be sorted, which meant that finding the `k` closest
stars after all the star distances had been calculated and inputted into the `TreeMap` was
simpler since we could just iterate through the `TreeMap` keys and add stars to the return list in that order.

## Running the REPL

---
To build use:
`mvn package`

To run use:
`./run`

This will give you a barebones REPL, where you can enter text and you will be output at most 5 suggestions sorted
from decreasing to increasing order.

To start the server use:
`./run --gui [--port=<port>]`

## Running the System Tests

---
Run: 
`./cs32-test <test suite>`

where `<test suite>` is the directory of all of the tests you want to run. If you want to run
all of the tests in some directory, include `*.test` at the end (e.g. `./cs32-test src/test/system/stars/*.test`)
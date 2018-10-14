# CardyGAn Integer Linear Programming (ILP) - Library

CardyGAn ILP provides a Java interface to integer linear programming solvers (ilp) with a rich API supporting propositional combination of (integer) linear arithmetic constraints.

## Compiling the project
We use gradle for building the project (https://gradle.org). Take the following steps to build the project:
1. Create a file `gradle.properties` in the project root.
2. As content of `gradle.properties` add the following line and adapt the path according to your local setup:
```
cplexJarPath=/Users/users1/Applications/IBM/ILOG/CPLEX_Studio1263/cplex/lib/cplex.jar
gurobiJarPath=/Library/gurobi800/mac64/lib/gurobi.jar
```
3. In the project root execute the command `gradle jar` from the command line to build the library.

## CPLEX-specific Setup
To use cplex solver you can either pass the native library path using the constructor or via the environment variable ```CPLEX_LIB_PATH```. Example for setting the environment variable in Unix:
```
export CPLEX_LIB_PATH=/Users/user1/Applications/IBM/ILOG/CPLEX_Studio1263/cplex/bin/x86-64_osx/
```

Additionally you need to put the solver specific JNI jar (`cplex.jar`) on the classpath.

## Usage
See the class `src/test/java/org/cardygan/ilp/SampleApiUsage.java` for sample usage.

# Introduction
This is a RISC-V Instruction Set Simulator built in Java for course 02155 Computer Archicture and Engineering at DTU

# How to run
The simulator is built on Java 11. It should also work on Java 8, but no guarantees are made.

To compile the code, run  `make comp`

To run the code, run `make run`

To compile an executable .jar file, run `make jar`. To run this jar file, use `java -jar App.jar`.

# How to test
To execute the built-in tests, run `make test TESTNAME={TestNameGoesHere}`. 
The valid testnames are the classes located in `src/test`.

All tests have been executed on Ubuntu 18.04 under WSL on Windows 10. 
They should work on any *nix installation.
A work stealing parallel schedule implemented in Java that has hyperobject support. Overall this project is meant to accomplish/implement a few features:
  - A way to begin parallel computation
  - Support fork and join operations
  - Create a Java object that manages a concurrent deque of tasks. 
  - A worker look that supports work stealing
  - A scheduler that creates hyperobjects

In summary, this project is meant to demonstrate the concurrent and parallel capabilities of Java, with the ability to steal work from other worker threads when they 
become available. 

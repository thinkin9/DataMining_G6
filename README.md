1. Since all compiled class files(From javac A2_G6_t1.java and javac A2_G6_t2.java) are stored in this directory, you need to set $CLASSPATH to include the current directory
   * export CLASSPATH=$CLASSPATH";."
2. Repository
   * README.md (this file)
   * A2_G6_t1.java (K-means++)
   * A2_G6_t2.java (DBSCAN)
   * A2_G6_report.pdf (Report)
   * ./database (for K-means++)
      * artd-31.csv
      * artset1.csv
      * boxes.csv
      * banana.csv
      * lines2.csv
   * ./database (for K-means++)
      * Data1.csv
      * Data2.csv
      * Data3.csv
      * Data4.csv
      * Data5.csv
3. Our Github Repository: [link](https://github.com/thinkin9/DataMining_G6)
4. Command
   * javac A2_G6_t1.java
   * javac A2_G6_t2.java
   * java A2_G6_t1 ./database/artd-31.csv 15 (with k)
   * java A2_G6_t1 ./database/artd-31.csv (without k)
   * java A2_G6_t2 ./database/artd-31.csv 5 0.5 (with minPts and eps)
   * java A2_G6_t2 ./database/artd-31.csv 0.5 (with eps & Estimated minPts)
   * java A2_G6_t2 ./database/artd-31.csv 4 (with minPts & Estimated eps)
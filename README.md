# Soumyajit-Kuila_2341011097_DIIJ-Project

# JDBC Library Loan Management System

## Technologies Used

* Java
* JDBC
* Apache Derby Embedded Database
* Eclipse IDE

# Features

* Member registration
* Book management
* Loan processing
* Transaction management
* Savepoints & rollback
* Performance benchmarking
* Query optimization testing
* Transaction granularity benchmarking
* Graceful database shutdown


# Project Structure

connection/
business/
benchmark/
ui/

# Requirements

* JDK 17+
* Apache Derby (derby.jar)
* Eclipse IDE

# How To Run

1. Import project into Eclipse
2. Add derby.jar to Build Path
3. Run MainApp.java

# JDBC URL

jdbc:derby:libraryDB;create=true

# Sample CLI Session
text
1
Soumyajit

2
Java Programming
12345

3
1
1

5

6

7

# Benchmarking Features

* Single Insert vs Batch Insert
* Statement vs PreparedStatement
* Full Table Scan vs Indexed Lookup
* Per-operation Commit vs Batch Commit

# Performance Metrics

* Execution Time
* Throughput
* Average Runtime
* JVM Warm-Up
* Outlier Detection
  
# Shutdown

Use menu option:
7
to gracefully shutdown Derby database.

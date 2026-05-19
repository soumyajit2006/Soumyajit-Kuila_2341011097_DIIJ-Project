package benchmark;

import connection.ConnectionManager;

import java.sql.*;

public class PerformanceEvaluator {
			private void warmUpJVM() {

			    for (int i = 0; i < 10000; i++) {

			        Math.sqrt(i);
			    }

			    System.gc();

			    System.out.println(
			            "\nJVM Warm-Up Completed."
			    );
	}

    public void runFullBenchmark() {
    	
    	warmUpJVM();
    	
        int records = 10000;

        System.out.println(
                "\n================ PERFORMANCE REPORT ================"
        );

        benchmarkSingleInsert(records);

        benchmarkBatchInsert(records);

        benchmarkStatement(records);

        benchmarkPreparedStatement(records);
        
        benchmarkQueryStrategy();

        benchmarkTransactionGranularity();

        System.out.println(
                "===================================================="
        );
    }
    
    		private void benchmarkSingleInsert(int records) {

    		    String sql =
    		            "INSERT INTO BenchmarkMembers(name) VALUES(?)";

    		    int runs = 5;

    		    double[] times =
    		            new double[runs];

    		    for (int r = 0; r < runs; r++) {

    		        long start = System.nanoTime();

    		        try (
    		                Connection conn =
    		                        ConnectionManager.getConnection();

    		                PreparedStatement ps =
    		                        conn.prepareStatement(sql)
    		        ) {

    		            for (int i = 0; i < records; i++) {

    		                ps.setString(
    		                        1,
    		                        "Single_" + r + "_" + i
    		                );

    		                ps.executeUpdate();
    		            }

    		        } catch (SQLException e) {

    		            e.printStackTrace();
    		        }

    		        long end = System.nanoTime();

    		        times[r] =
    		                (end - start) / 1_000_000.0;

    		        System.out.println(
    		                "Run "
    		                + (r + 1)
    		                + " : "
    		                + times[r]
    		                + " ms"
    		        );
    		    }

    		    // Remove highest and lowest outliers

    		    double max = times[0];
    		    double min = times[0];
    		    double sum = 0;

    		    for (double t : times) {

    		        if (t > max) max = t;

    		        if (t < min) min = t;

    		        sum += t;
    		    }

    		    sum = sum - max - min;

    		    double average =
    		            sum / (runs - 2);

    		    // Standard deviation

    		    double variance = 0;

    		    for (double t : times) {

    		        variance +=
    		                Math.pow(
    		                        t - average,
    		                        2
    		                );
    		    }

    		    variance =
    		            variance / runs;

    		    double stdDeviation =
    		            Math.sqrt(variance);

    		    double throughput =
    		            records / (average / 1000);

    		    System.out.println(
    		            "\n===== SINGLE INSERT REPORT ====="
    		    );

    		    System.out.println(
    		            "Record Count : "
    		            + records
    		    );

    		    System.out.println(
    		            "Average Time : "
    		            + average
    		            + " ms"
    		    );

    		    System.out.println(
    		            "Std Deviation : "
    		            + stdDeviation
    		            + " ms"
    		    );

    		    System.out.println(
    		            "Throughput : "
    		            + throughput
    		            + " ops/sec"
    		    );

    		    System.out.println(
    		            "Outliers Removed : "
    		            + min
    		            + " ms, "
    		            + max
    		            + " ms"
    		    );

    		    System.out.println(
    		            "Observation : Slower due to " +
    		            "individual DB operations."
    		    );
    		}

    private void benchmarkBatchInsert(int records) {

        String sql =
                "INSERT INTO BenchmarkMembers(name) VALUES(?)";

        long start = System.nanoTime();

        try (
                Connection conn =
                        ConnectionManager.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            for (int i = 0; i < records; i++) {

                ps.setString(
                        1,
                        "Batch_" + i
                );

                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e) {

            e.printStackTrace();
        }

        long end = System.nanoTime();

        double timeMs =
                (end - start) / 1_000_000.0;

        double throughput =
                records / (timeMs / 1000);

        System.out.println(
                "\nOperation Type : Batch Insert"
        );

        System.out.println(
                "Record Count   : " + records
        );

        System.out.println(
                "Execution Time : "
                + timeMs + " ms"
        );

        System.out.println(
                "Throughput     : "
                + throughput + " ops/sec"
        );

        System.out.println(
                "Observation    : " +
                "Faster due to reduced DB round trips."
        );
    }

    private void benchmarkStatement(int records) {

        long start = System.nanoTime();

        try (
                Connection conn =
                        ConnectionManager.getConnection()
        ) {

            Statement stmt =
                    conn.createStatement();

            for (int i = 0; i < records; i++) {

                stmt.executeUpdate(
                        "INSERT INTO BenchmarkMembers(name) " +
                        "VALUES('S" + i + "')"
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        long end = System.nanoTime();

        double timeMs =
                (end - start) / 1_000_000.0;

        double throughput =
                records / (timeMs / 1000);

        System.out.println(
                "\nOperation Type : Statement"
        );

        System.out.println(
                "Query Type     : String Concatenation"
        );

        System.out.println(
                "Execution Time : "
                + timeMs + " ms"
        );

        System.out.println(
                "Throughput     : "
                + throughput + " ops/sec"
        );

        System.out.println(
                "Observation    : " +
                "Slower because SQL is compiled repeatedly."
        );
    }

    private void benchmarkPreparedStatement(int records) {

        long start = System.nanoTime();

        try (
                Connection conn =
                        ConnectionManager.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(
                                "INSERT INTO BenchmarkMembers(name) VALUES(?)"
                        )
        ) {

            for (int i = 0; i < records; i++) {

                ps.setString(
                        1,
                        "P" + i
                );

                ps.executeUpdate();
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        long end = System.nanoTime();

        double timeMs =
                (end - start) / 1_000_000.0;

        double throughput =
                records / (timeMs / 1000);

        System.out.println(
                "\nOperation Type : PreparedStatement"
        );

        System.out.println(
                "Query Type     : Precompiled Query"
        );

        System.out.println(
                "Execution Time : "
                + timeMs + " ms"
        );

        System.out.println(
                "Throughput     : "
                + throughput + " ops/sec"
        );

        System.out.println(
                "Observation    : " +
                "Faster because query plan is reused."
        );
    }
    
    		public void benchmarkQueryStrategy() {

    		    try (
    		            Connection conn =
    		                    ConnectionManager.getConnection()
    		    ) {
    		    			Statement runtimeStmt =
    		    			        conn.createStatement();

    		    			runtimeStmt.execute(
    		    			        "CALL SYSCS_UTIL." +
    		    			        "SYSCS_SET_RUNTIMESTATISTICS(1)"
    		    			);

    		        Statement stmt =
    		                conn.createStatement();

    		        long start1 =
    		                System.nanoTime();

    		        stmt.executeQuery(
    		                "SELECT * FROM Loans"
    		        );

    		        long end1 =
    		                System.nanoTime();

    		        long start2 =
    		                System.nanoTime();

    		        PreparedStatement ps =
    		                conn.prepareStatement(
    		                        "SELECT * FROM Loans " +
    		                        "WHERE member_id=?"
    		                );

    		        ps.setInt(1, 1);

    		        ps.executeQuery();

    		        long end2 =
    		                System.nanoTime();

    		        System.out.println(
    		                "\n===== QUERY STRATEGY REPORT ====="
    		        );

    		        System.out.println(
    		                "Full Table Scan Time : "
    		                + ((end1 - start1) / 1_000_000.0)
    		                + " ms"
    		        );

    		        System.out.println(
    		                "Indexed Lookup Time : "
    		                + ((end2 - start2) / 1_000_000.0)
    		                + " ms"
    		        );

    		        System.out.println(
    		                "Observation : Indexed lookup is faster."
    		        );

    		    } catch (SQLException e) {

    		        e.printStackTrace();
    		    }
    		}
    		
    				public void benchmarkTransactionGranularity() {

    				    try (
    				            Connection conn =
    				                    ConnectionManager.getConnection()
    				    ) {

    				        String sql =
    				                "INSERT INTO BenchmarkMembers(name) VALUES(?)";

    				        PreparedStatement ps =
    				                conn.prepareStatement(sql);

    				        long start1 =
    				                System.nanoTime();

    				        for (int i = 0; i < 100; i++) {

    				            conn.setAutoCommit(false);

    				            ps.setString(
    				                    1,
    				                    "Commit_" + i
    				            );

    				            ps.executeUpdate();

    				            conn.commit();
    				        }

    				        long end1 =
    				                System.nanoTime();

    				        conn.setAutoCommit(false);

    				        long start2 =
    				                System.nanoTime();

    				        for (int i = 0; i < 100; i++) {

    				            ps.setString(
    				                    1,
    				                    "BatchCommit_" + i
    				            );

    				            ps.executeUpdate();
    				        }

    				        conn.commit();

    				        long end2 =
    				                System.nanoTime();

    				        System.out.println(
    				                "\n===== TRANSACTION REPORT ====="
    				        );

    				        System.out.println(
    				                "Per-operation Commit : "
    				                + ((end1 - start1) / 1_000_000.0)
    				                + " ms"
    				        );

    				        System.out.println(
    				                "Batch Commit : "
    				                + ((end2 - start2) / 1_000_000.0)
    				                + " ms"
    				        );

    				        System.out.println(
    				                "Observation : Batch commit is faster."
    				        );

    				    } catch (SQLException e) {

    				        e.printStackTrace();
    				    }
    				}
}

package connection;

import java.sql.*;

import java.io.File;

public class ConnectionManager {

    private static final String URL =
            "jdbc:derby:libraryDB;create=true";

    static {
        try {
            Class.forName(
                    "org.apache.derby.jdbc.EmbeddedDriver"
            );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {

        try (
                Connection conn = getConnection();
                Statement stmt = conn.createStatement()
        ) {

            stmt.executeUpdate(
                    "CREATE TABLE Members(" +
                    "member_id INT PRIMARY KEY " +
                    "GENERATED ALWAYS AS IDENTITY, " +
                    "name VARCHAR(100), " +
                    "active_loans INT DEFAULT 0)"
            );

        } catch (SQLException ignored) {}

        try (
                Connection conn = getConnection();
                Statement stmt = conn.createStatement()
        ) {

            stmt.executeUpdate(
                    "CREATE TABLE Books(" +
                    "book_id INT PRIMARY KEY " +
                    "GENERATED ALWAYS AS IDENTITY, " +
                    "title VARCHAR(100), " +
                    "isbn VARCHAR(30) UNIQUE, " +
                    "available BOOLEAN DEFAULT TRUE)"
            );

        } catch (SQLException ignored) {}

        try (
                Connection conn = getConnection();
                Statement stmt = conn.createStatement()
        ) {

            stmt.executeUpdate(
                    "CREATE TABLE Loans(" +
                    "loan_id INT PRIMARY KEY " +
                    "GENERATED ALWAYS AS IDENTITY, " +
                    "member_id INT, " +
                    "book_id INT, " +
                    "loan_date DATE, " +
                    "return_date DATE, " +
                    "FOREIGN KEY(member_id) " +
                    "REFERENCES Members(member_id), " +
                    "FOREIGN KEY(book_id) " +
                    "REFERENCES Books(book_id))"
            );

        } catch (SQLException ignored) {}
        
        		try (
        		        Connection conn = getConnection();
        		        Statement stmt = conn.createStatement()
        		) {

        		    stmt.executeUpdate(
        		            "CREATE TABLE BenchmarkMembers(" +
        		            "id INT PRIMARY KEY " +
        		            "GENERATED ALWAYS AS IDENTITY, " +
        		            "name VARCHAR(100))"
        		    );

        		} catch (SQLException ignored) {}

        try (
                Connection conn = getConnection();
                Statement stmt = conn.createStatement()
        ) {

            stmt.executeUpdate(
                    "CREATE INDEX idx_isbn ON Books(isbn)"
            );

            stmt.executeUpdate(
                    "CREATE INDEX idx_member ON Loans(member_id)"
            );

            stmt.executeUpdate(
                    "CREATE INDEX idx_return ON Loans(return_date)"
            );

        } catch (SQLException ignored) {}

        System.out.println(
                "Database initialized successfully."
        );
        
    
        		try (
        		        Connection conn = getConnection();
        		        Statement stmt = conn.createStatement()
        		) {

        		    ResultSet rs1 =
        		            stmt.executeQuery(
        		                    "SELECT COUNT(*) FROM Members"
        		            );

        		    rs1.next();

        		    if (rs1.getInt(1) == 0) {

        		        stmt.executeUpdate(
        		                "INSERT INTO Members(name) " +
        		                "VALUES('AdminUser')"
        		        );
        		    }

        		    ResultSet rs2 =
        		            stmt.executeQuery(
        		                    "SELECT COUNT(*) FROM Books"
        		            );

        		    rs2.next();

        		    if (rs2.getInt(1) == 0) {

        		        stmt.executeUpdate(
        		                "INSERT INTO Books(title, isbn) " +
        		                "VALUES('Java Basics', 'ISBN001')"
        		        );
        		    }

        		} catch (SQLException e) {

        		    e.printStackTrace();
        		}
    }
    
    public static void shutdown() {

        try {

            DriverManager.getConnection(
                    "jdbc:derby:libraryDB;shutdown=true"
            );

        } catch (SQLException e) {

            System.out.println(
                    "Database shutdown completed."
            );
        }
    }

    		public static void dropDatabase() {

    		    File dbFolder =
    		            new File("libraryDB");

    		    deleteFolder(dbFolder);

    		    System.out.println(
    		            "Database deleted successfully."
    		    );
    		}

    		private static void deleteFolder(File folder) {

    		    File[] files =
    		            folder.listFiles();

    		    if (files != null) {

    		        for (File file : files) {

    		            if (file.isDirectory()) {

    		                deleteFolder(file);

    		            } else {

    		                file.delete();
    		            }
    		        }
    		    }

    		    folder.delete();
    		}
}


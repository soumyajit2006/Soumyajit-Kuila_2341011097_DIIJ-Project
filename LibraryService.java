package business;

import connection.ConnectionManager;

import java.sql.*;

public class LibraryService {

    public void registerMember(String name) {

        String sql =
                "INSERT INTO Members(name) VALUES(?)";

        try (
                Connection conn =
                        ConnectionManager.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1, name);

            ps.executeUpdate();

            System.out.println(
                    "Member registered successfully."
            );

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void addBook(
            String title,
            String isbn
    ) {

        String sql =
                "INSERT INTO Books(title, isbn) VALUES(?, ?)";

        try (
                Connection conn =
                        ConnectionManager.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1, title);
            ps.setString(2, isbn);

            ps.executeUpdate();

            System.out.println(
                    "Book added successfully."
            );

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void processLoan(
            int memberId,
            int bookId
    ) {

        String checkBook =
                "SELECT available FROM Books WHERE book_id=?";

        String updateBook =
                "UPDATE Books SET available=false " +
                "WHERE book_id=?";

        String insertLoan =
                "INSERT INTO Loans(member_id, book_id, loan_date) " +
                "VALUES(?,?,CURRENT_DATE)";

        String updateMember =
                "UPDATE Members SET active_loans=" +
                "active_loans+1 WHERE member_id=?";

        try (
                Connection conn =
                        ConnectionManager.getConnection()
        ) {

            conn.setAutoCommit(false);

            try (
                    PreparedStatement checkStmt =
                            conn.prepareStatement(checkBook)
            ) {

                checkStmt.setInt(1, bookId);

                ResultSet rs =
                        checkStmt.executeQuery();

                if (
                        rs.next() &&
                        !rs.getBoolean("available")
                ) {

                    System.out.println(
                            "Book not available."
                    );

                    return;
                }
            }

            Savepoint savepoint=null;

            try (
                    PreparedStatement ps1 =
                            conn.prepareStatement(updateBook);

                    PreparedStatement ps2 =
                            conn.prepareStatement(insertLoan);

                    PreparedStatement ps3 =
                            conn.prepareStatement(updateMember)
            ) {

                ps1.setInt(1, bookId);
                ps1.executeUpdate();
                		if (memberId == -1) {

                		    throw new SQLException(
                		            "Forced transaction failure"
                		    );
                		}

                savepoint = conn.setSavepoint();

                ps2.setInt(1, memberId);
                ps2.setInt(2, bookId);

                ps2.executeUpdate();

                ps3.setInt(1, memberId);

                ps3.executeUpdate();

                conn.commit();

                System.out.println(
                        "Loan processed successfully."
                );

            } catch (SQLException e) {

                conn.rollback(savepoint);

                conn.rollback();

                System.out.println(
                        "Transaction rolled back."
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void returnBook(int bookId) {

        String updateBook =
                "UPDATE Books SET available=true " +
                "WHERE book_id=?";

        String updateLoan =
                "UPDATE Loans SET return_date=CURRENT_DATE " +
                "WHERE book_id=? AND return_date IS NULL";

        try (
                Connection conn =
                        ConnectionManager.getConnection()
        ) {

            conn.setAutoCommit(false);

            try (
                    PreparedStatement ps1 =
                            conn.prepareStatement(updateBook);

                    PreparedStatement ps2 =
                            conn.prepareStatement(updateLoan)
            ) {

                ps1.setInt(1, bookId);
                ps1.executeUpdate();

                ps2.setInt(1, bookId);
                ps2.executeUpdate();

                conn.commit();

                System.out.println(
                        "Book returned successfully."
                );

            } catch (SQLException e) {

                conn.rollback();

                System.out.println(
                        "Rollback completed."
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void viewActiveLoans() {

        String sql =
                "SELECT m.name, b.title, l.loan_date " +
                "FROM Loans l " +
                "JOIN Members m " +
                "ON l.member_id=m.member_id " +
                "JOIN Books b " +
                "ON l.book_id=b.book_id " +
                "WHERE l.return_date IS NULL";

        try (
                Connection conn =
                        ConnectionManager.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                System.out.println(
                        rs.getString(1)
                        + " | " +
                        rs.getString(2)
                        + " | " +
                        rs.getDate(3)
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    		public void showBooksAndMembers() {

    		    try (
    		            Connection conn =
    		                    ConnectionManager.getConnection();

    		            Statement stmt =
    		                    conn.createStatement()
    		    ) {

    		        System.out.println(
    		                "\n========== MEMBERS =========="
    		        );

    		        ResultSet rs1 =
    		                stmt.executeQuery(
    		                        "SELECT * FROM Members"
    		                );

    		        while (rs1.next()) {

    		            System.out.println(
    		                    "Member ID : "
    		                    + rs1.getInt("member_id")
    		                    + " | Member Name : "
    		                    + rs1.getString("name")
    		            );
    		        }

    		        System.out.println(
    		                "\n=========== BOOKS ==========="
    		        );

    		        ResultSet rs2 =
    		                stmt.executeQuery(
    		                        "SELECT * FROM Books"
    		                );

    		        while (rs2.next()) {

    		            System.out.println(
    		                    "Book ID : "
    		                    + rs2.getInt("book_id")
    		                    + " | Title : "
    		                    + rs2.getString("title")
    		                    + " | Available : "
    		                    + rs2.getBoolean("available")
    		            );
    		        }

    		        System.out.println(
    		                "\n============================="
    		        );

    		    } catch (SQLException e) {

    		        e.printStackTrace();
    		    }
    		}
    		
    				public void searchLoansByMember(int memberId) {

    				    String sql =
    				            "SELECT b.title, l.loan_date " +
    				            "FROM Loans l " +
    				            "JOIN Books b " +
    				            "ON l.book_id=b.book_id " +
    				            "WHERE l.member_id=? " +
    				            "AND l.return_date IS NULL";

    				    try (
    				            Connection conn =
    				                    ConnectionManager.getConnection();

    				            PreparedStatement ps =
    				                    conn.prepareStatement(sql)
    				    ) {

    				        ps.setInt(1, memberId);

    				        ResultSet rs =
    				                ps.executeQuery();

    				        while (rs.next()) {

    				            System.out.println(
    				                    "Book: "
    				                    + rs.getString(1)
    				                    + " | Loan Date: "
    				                    + rs.getDate(2)
    				            );
    				        }

    				    } catch (SQLException e) {

    				        e.printStackTrace();
    				    }
    				}

}


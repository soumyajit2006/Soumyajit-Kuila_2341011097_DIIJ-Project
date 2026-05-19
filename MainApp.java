package ui;

import benchmark.PerformanceEvaluator;
import business.LibraryService;
import connection.ConnectionManager;

import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        LibraryService service =
                new LibraryService();

        PerformanceEvaluator evaluator =
                new PerformanceEvaluator();

        ConnectionManager.initializeDatabase();
        
        service.showBooksAndMembers();

        while (true) {

            System.out.println(
                    "\n===== LIBRARY MANAGEMENT SYSTEM ====="
            );

            System.out.println(
                    "1. Register Member"
            );

            System.out.println(
                    "2. Add Book"
            );

            System.out.println(
                    "3. Process Loan"
            );

            System.out.println(
                    "4. Return Book"
            );

            System.out.println(
                    "5. View Active Loans"
            );

            System.out.println(
                    "6. Run Benchmark"
            );

            System.out.println(
                    "7. Exit"
            );

            System.out.print(
                    "Enter choice: "
            );

            int choice = sc.nextInt();

            sc.nextLine();

            switch (choice) {

                case 1 -> {

                    System.out.print(
                            "Enter member name: "
                    );

                    String name =
                            sc.nextLine();

                    service.registerMember(name);
                }

                case 2 -> {

                    System.out.print(
                            "Enter book title: "
                    );

                    String title =
                            sc.nextLine();

                    System.out.print(
                            "Enter ISBN: "
                    );

                    String isbn =
                            sc.nextLine();

                    service.addBook(
                            title,
                            isbn
                    );
                }

                case 3 -> {

                    System.out.print(
                            "Enter Member ID: "
                    );

                    int memberId =
                            sc.nextInt();

                    System.out.print(
                            "Enter Book ID: "
                    );

                    int bookId =
                            sc.nextInt();

                    service.processLoan(
                            memberId,
                            bookId
                    );
                }

                case 4 -> {

                    System.out.print(
                            "Enter Book ID: "
                    );

                    int bookId =
                            sc.nextInt();

                    service.returnBook(bookId);
                }

                case 5 ->
                        service.viewActiveLoans();

                case 6 -> {

                	evaluator.runFullBenchmark();
                }

                case 7 -> {

                    try {

                        System.out.println(
                                "Shutting down database..."
                        );

                        ConnectionManager.shutdown();

                    } catch (Exception e) {

                        System.out.println(
                                "Error during shutdown."
                        );

                        e.printStackTrace();

                    } 
                        sc.close();
                        System.exit(0);
                    }

                case 8 -> {

                    System.out.print(
                            "Enter Member ID: "
                    );

                    int memberId =
                            sc.nextInt();

                    service.searchLoansByMember(memberId);
                }

                case 9 -> {

                    System.out.println(
                            "\n===== VALIDATION TESTS ====="
                    );

                    System.out.println(
                            "1. Duplicate ISBN Test"
                    );

                    System.out.println(
                            "2. Forced Rollback Test"
                    );

                    System.out.println(
                            "3. Derby Lock Conflict Already Demonstrated"
                    );

                    try {

                        service.addBook(
                                "DuplicateBook",
                                "ISBN001"
                        );

                    } catch (Exception e) {

                        System.out.println(
                                "Duplicate constraint validated."
                        );
                    }

                    try {

                        service.processLoan(-1, 1);

                    } catch (Exception e) {

                        System.out.println(
                                "Rollback validation completed."
                        );
                    }
                }

                default ->
                        System.out.println(
                                "Invalid choice.");
            }
        }
    }
}




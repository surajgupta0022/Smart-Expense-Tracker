package app;

import service.ExpenseService;
import view.ExpenseTrackerGUI;
import javax.swing.SwingUtilities;
import java.util.Scanner;

public class main{
    public static void main(String[] args){

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ExpenseTrackerGUI().setVisible(true);
            }
        });

        Scanner sc = new Scanner(System.in);
        ExpenseService service = new ExpenseService();
        boolean running = true;

        System.out.println("==============================================");
        System.out.println("    WELCOME TO SMART CLI EXPENSE TRACKER      ");
        System.out.println("==============================================");

        while(running){
            System.out.println("\n1. Add Income/Expense");
            System.out.println("2. Set Category Budget");
            System.out.println("3. View History");
            System.out.println("4. View Balance Summary");
            System.out.println("5. Exit");
            System.out.println("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice){
                case 1:
                    System.out.println("Enter Type (INCOME / EXPENSE): ");
                    String type = sc.nextLine();
                    System.out.println("Enter category (e.g. Food, Rent, Salary): ");
                    String category = sc.nextLine();
                    System.out.println("Enter Amount: ");
                    double amount = sc.nextDouble();
                    service.addTransaction(type,category,amount);
                    break;

                case 2:
                    System.out.println("Enter category: ");
                    String budgetCategory = sc.nextLine();
                    System.out.println("Enter Monthly Budget Limit: ");
                    double limit = sc.nextDouble();
                    service.setBudget(budgetCategory, limit);
                    break;

                case 3:
                    service.viewAllTransactions();
                    break;

                case 4:
                    service.showSummary();
                    break;

                case 5:
                    running = false;
                    System.out.println("👋 Thank you for using Expense Tracker!");
                    break;

                default:
                    System.out.println("❌ Invalid choice! Try again.");
            }
        }
        sc.close();
    }
}
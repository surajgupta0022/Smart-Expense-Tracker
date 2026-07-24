package service;

import model.Transaction;
import model.CategoryBudget; // <--- Import CategoryBudget
import repository.DatabaseHandler; // ya FileHandler agar SQL nahi use kar rahe
import java.util.*;

public class ExpenseService {
    private List<Transaction> transactions;

    // PEHLE: private Map<String, Double> categoryBudgets;
    // AB UPDATE: CategoryBudget class ka Object Map me store karenge
    private Map<String, CategoryBudget> categoryBudgets;

    private int nextId = 1;

    public ExpenseService() {
        this.transactions = DatabaseHandler.loadTransactions(); // ya FileHandler
        this.categoryBudgets = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.getId() >= nextId) {
                nextId = t.getId() + 1;
            }
        }
    }

    // --- BUDGET SET KARNE KA METHOD UPDATE ---
    public void setBudget(String category, double limit) {
        // CategoryBudget ka naya object banakar Map me put kar rahe hain
        CategoryBudget budget = new CategoryBudget(category, limit);
        categoryBudgets.put(category.toLowerCase(), budget);

        System.out.println("🎯 Budget set for " + category + ": ₹" + limit);
    }

    // --- BUDGET CHECK & ALERT METHOD UPDATE ---
    private void checkBudgetAlert(String category) {
        CategoryBudget budget = categoryBudgets.get(category.toLowerCase());
        if (budget == null) return; // Agar is category ka budget set nahi hai toh skip

        // Is category me kitna kharch hua calculate karein
        double totalSpent = 0;
        for (Transaction t : transactions) {
            if (t.getType().equalsIgnoreCase("EXPENSE") && t.getCategory().equalsIgnoreCase(category)) {
                totalSpent += t.getAmount();
            }
        }

        // CategoryBudget class ke isExceeded() method ko call karke check kar rahe hain
        if (budget.isExceeded(totalSpent)) {
            System.out.println("\n⚠️ WARNING: You have exceeded your budget for " + category + "!");
            System.out.println("Spent: ₹" + totalSpent + " | Budget Limit: ₹" + budget.getBudgetLimit() + "\n");
        }
    }

    public void addTransaction(String type, String category, double amount) {
        Transaction t = new Transaction(nextId++, type, category, amount);
        transactions.add(t);
        DatabaseHandler.saveTransaction(t);

        if (type.equalsIgnoreCase("EXPENSE")) {
            checkBudgetAlert(category);
        }
    }

    public List<Transaction> getTransactionsList() {
        return transactions;
    }

    // Other existing methods (viewAllTransactions, showSummary, etc.)
    // Main.java (Case 3) ke liye
    public void viewAllTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("❌ No transactions found!");
            return;
        }
        System.out.println("\n--- TRANSACTION HISTORY ---");
        for (Transaction t : transactions) {
            System.out.println(t);
        }
    }

    // Main.java (Case 4) ke liye
    public void showSummary() {
        double totalIncome = 0;
        double totalExpense = 0;

        for (Transaction t : transactions) {
            if (t.getType().equalsIgnoreCase("INCOME")) {
                totalIncome += t.getAmount();
            } else if (t.getType().equalsIgnoreCase("EXPENSE")) {
                totalExpense += t.getAmount();
            }
        }

        System.out.println("\n================ SUMMARY ================");
        System.out.println("Total Income  : ₹" + totalIncome);
        System.out.println("Total Expense : ₹" + totalExpense);
        System.out.println("Net Balance   : ₹" + (totalIncome - totalExpense));
        System.out.println("=========================================");
    }
}
package service;

import model.Transaction;
import model.CategoryBudget;
import repository.DatabaseHandler;
import java.util.*;

public class ExpenseService {
    private List<Transaction> transactions;

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

    public void setBudget(String category, double limit) {
        // CategoryBudget ka naya object banakar Map me put kar rahe hain
        CategoryBudget budget = new CategoryBudget(category, limit);
        categoryBudgets.put(category.toLowerCase(), budget);

        System.out.println("🎯 Budget set for " + category + ": ₹" + limit);
    }

    private void checkBudgetAlert(String category) {
        CategoryBudget budget = categoryBudgets.get(category.toLowerCase());
        if (budget == null) return; // Agar is category ka budget set nahi hai toh skip

        double totalSpent = 0;
        for (Transaction t : transactions) {
            if (t.getType().equalsIgnoreCase("EXPENSE") && t.getCategory().equalsIgnoreCase(category)) {
                totalSpent += t.getAmount();
            }
        }

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
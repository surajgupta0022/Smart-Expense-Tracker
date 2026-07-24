package model;

public class CategoryBudget {
    private String category;
    private double budgetLimit;

    // Constructor
    public CategoryBudget(String category, double budgetLimit) {
        this.category = category;
        this.budgetLimit = budgetLimit;
    }

    // Getters and Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getBudgetLimit() {
        return budgetLimit;
    }

    public void setBudgetLimit(double budgetLimit) {
        this.budgetLimit = budgetLimit;
    }

    // Dynamic Check Method: Kya current spending limit se baahar gayi hai?
    public boolean isExceeded(double totalSpent) {
        return totalSpent > this.budgetLimit;
    }
}
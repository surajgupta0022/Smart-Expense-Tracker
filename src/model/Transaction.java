package model;

import java.time.LocalDate;

public class Transaction {
    private int id;
    private String type;
    private String category;
    private double amount;
    private LocalDate date;

    public Transaction(int id, String type, String category, double amount){
        this.id = id;
        this.type = type;
        this.category= category;
        this.amount = amount;
        this.date = LocalDate.now();
    }

    // File me save karne ke liye format : ID, Type, Category, Amount, Date
    public String toCSV(){
        return id + "," + type + "," + category + "," + amount + "," + date;
    }

    public int getId() {return id;}
    public String getType() {return type;}
    public String getCategory() {return category;}
    public double getAmount() {return amount;}
    public LocalDate getDate() {return date;}

    @Override
    public String toString(){
        return String.format("[%d] %s | %-10s | %-10s | ₹%.2f",id,date,type,category,amount);
    }
}
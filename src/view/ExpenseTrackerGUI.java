package view;

import model.Transaction;
import service.ExpenseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class ExpenseTrackerGUI extends JFrame {
    private ExpenseService service;

    // UI Input Fields
    private JTextField txtCategory, txtAmount;
    private JComboBox<String> comboType;

    // Budget Inputs
    private JTextField txtBudgetCategory, txtBudgetLimit;

    // Table & Summary Labels
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotalIncome, lblTotalExpense, lblBalance;

    public ExpenseTrackerGUI() {
        service = new ExpenseService();

        // 1. Frame Setup
        setTitle("Smart Expense & Budget Tracker Dashboard");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Screen ke center me kholega
        setLayout(new BorderLayout(15, 15));

        // ----------------------------------------------------
        // TOP PANEL: Summary Cards (Income, Expense, Balance)
        // ----------------------------------------------------
        JPanel topSummaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        topSummaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        lblTotalIncome = createCardLabel("Total Income", "₹0.0", new Color(39, 174, 96));
        lblTotalExpense = createCardLabel("Total Expense", "₹0.0", new Color(192, 57, 43));
        lblBalance = createCardLabel("Net Balance", "₹0.0", new Color(41, 128, 185));

        topSummaryPanel.add(lblTotalIncome);
        topSummaryPanel.add(lblTotalExpense);
        topSummaryPanel.add(lblBalance);
        add(topSummaryPanel, BorderLayout.NORTH);

        // ----------------------------------------------------
        // WEST PANEL: Sidebar Forms (Add Transaction & Set Budget)
        // ----------------------------------------------------
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 10));

        // Form 1: Add Income / Expense
        JPanel addPanel = new JPanel(new GridLayout(7, 1, 5, 5));
        addPanel.setBorder(BorderFactory.createTitledBorder("➕ Add New Transaction"));

        comboType = new JComboBox<>(new String[]{"EXPENSE", "INCOME"});
        txtCategory = new JTextField();
        txtAmount = new JTextField();
        JButton btnAdd = new JButton("Save Transaction");
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);

        addPanel.add(new JLabel("Type:"));
        addPanel.add(comboType);
        addPanel.add(new JLabel("Category (e.g. Food, Rent):"));
        addPanel.add(txtCategory);
        addPanel.add(new JLabel("Amount (₹):"));
        addPanel.add(txtAmount);
        addPanel.add(btnAdd);

        // Form 2: Set Category Budget
        JPanel budgetPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        budgetPanel.setBorder(BorderFactory.createTitledBorder("🎯 Set Category Budget"));

        txtBudgetCategory = new JTextField();
        txtBudgetLimit = new JTextField();
        JButton btnSetBudget = new JButton("Set Budget Limit");
        btnSetBudget.setBackground(new Color(52, 152, 219));
        btnSetBudget.setForeground(Color.WHITE);

        budgetPanel.add(new JLabel("Category:"));
        budgetPanel.add(txtBudgetCategory);
        budgetPanel.add(new JLabel("Monthly Limit (₹):"));
        budgetPanel.add(txtBudgetLimit);
        budgetPanel.add(btnSetBudget);

        sidebarPanel.add(addPanel);
        sidebarPanel.add(Box.createVerticalStrut(15)); // Gap
        sidebarPanel.add(budgetPanel);

        add(sidebarPanel, BorderLayout.WEST);

        // ----------------------------------------------------
        // CENTER PANEL: Transaction History Table
        // ----------------------------------------------------
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("📜 Transaction History"));

        String[] columns = {"ID", "Date", "Type", "Category", "Amount (₹)"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // ----------------------------------------------------
        // BUTTON ACTIONS (Listeners)
        // ----------------------------------------------------
        btnAdd.addActionListener(e -> addTransactionAction());
        btnSetBudget.addActionListener(e -> setBudgetAction());

        // Initial Data Load
        refreshTable();
        updateSummary();
    }

    // Custom UI Card Generator
    private JLabel createCardLabel(String title, String value, Color color) {
        JLabel label = new JLabel("<html><center><b>" + title + "</b><br><font size='5'>" + value + "</font></center></html>", SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(color);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createLineBorder(color.darker(), 1));
        return label;
    }

    // Action: Save Transaction
    private void addTransactionAction() {
        String type = (String) comboType.getSelectedItem();
        String category = txtCategory.getText().trim();
        String amountText = txtAmount.getText().trim();

        if (category.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            // Call Service
            service.addTransaction(type, category, amount);

            // Clear Input
            txtCategory.setText("");
            txtAmount.setText("");

            // Refresh Screen
            refreshTable();
            updateSummary();

            JOptionPane.showMessageDialog(this, "Transaction Added Successfully!");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Action: Set Budget
    private void setBudgetAction() {
        String category = txtBudgetCategory.getText().trim();
        String limitText = txtBudgetLimit.getText().trim();

        if (category.isEmpty() || limitText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Category and Limit!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double limit = Double.parseDouble(limitText);

            // Call Service
            service.setBudget(category, limit);

            txtBudgetCategory.setText("");
            txtBudgetLimit.setText("");

            JOptionPane.showMessageDialog(this, "🎯 Budget Limit Set for " + category + ": ₹" + limit);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid limit amount!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Refresh Table View
    private void refreshTable() {
        tableModel.setRowCount(0); // Clear Table
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (Transaction t : service.getTransactionsList()) {
            String formattedDate = t.getDate() != null ? t.getDate().format(formatter) : "N/A";
            Object[] row = {t.getId(), formattedDate, t.getType(), t.getCategory(), "₹" + t.getAmount()};
            tableModel.addRow(row);
        }
    }

    // Update Top Summary Cards
    private void updateSummary() {
        double income = 0;
        double expense = 0;

        for (Transaction t : service.getTransactionsList()) {
            if (t.getType().equalsIgnoreCase("INCOME")) income += t.getAmount();
            else if (t.getType().equalsIgnoreCase("EXPENSE")) expense += t.getAmount();
        }

        double balance = income - expense;

        lblTotalIncome.setText("<html><center><b>Total Income</b><br><font size='5'>₹" + income + "</font></center></html>");
        lblTotalExpense.setText("<html><center><b>Total Expense</b><br><font size='5'>₹" + expense + "</font></center></html>");
        lblBalance.setText("<html><center><b>Net Balance</b><br><font size='5'>₹" + balance + "</font></center></html>");
    }
}
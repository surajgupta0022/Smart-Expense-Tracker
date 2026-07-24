package repository;

import model.Transaction;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/expensetracker";
    private static final String USER = "root";
    private static final String PASSWORD = "Suraj@123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void saveTransaction(Transaction t) {
        String query = "INSERT INTO transactions (type, category, amount, date) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, t.getType());
            stmt.setString(2, t.getCategory());
            stmt.setDouble(3, t.getAmount());
            stmt.setDate(4, Date.valueOf(t.getDate()));

            stmt.executeUpdate();
            System.out.println("✅ Saved to MySQL Database!");

        } catch (SQLException e) {
            System.out.println("⚠️ Database Error (Save): " + e.getMessage());
        }
    }

    public static List<Transaction> loadTransactions() {
        List<Transaction> list = new ArrayList<>();
        String query = "SELECT * FROM transactions";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                String category = rs.getString("category");
                double amount = rs.getDouble("amount");

                Date sqlDate = rs.getDate("date");
                LocalDate date = sqlDate!= null ? sqlDate.toLocalDate() : LocalDate.now();

                Transaction t = new Transaction(id, type, category, amount);
                list.add(t);
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Database Error (Load): " + e.getMessage());
        }
        return list;
    }
}
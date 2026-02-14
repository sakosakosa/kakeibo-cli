package dao;

import model.Expense;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseDao {

    private static final String URL = "jdbc:sqlite:data/expenses.db";

    public void insert(Expense expense) {

        String sql = "INSERT INTO expenses (date, category, amount, memo) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, expense.getDate().toString());
            pstmt.setString(2, expense.getCategory());
            pstmt.setInt(3, expense.getAmount());
            pstmt.setString(4, expense.getMemo());

            int rows = pstmt.executeUpdate();

            if (rows != 1) {
                throw new RuntimeException("Insert failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Expense> findAll() {

        List<Expense> list = new ArrayList<>();
        String sql = "SELECT id, date, category, amount, memo FROM expenses";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Expense e = new Expense(
                        rs.getInt("id"),
                        LocalDate.parse(rs.getString("date")),
                        rs.getString("category"),
                        rs.getInt("amount"),
                        rs.getString("memo")
                );

                list.add(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean update(Expense expense) {

        String sql = """
                UPDATE expenses
                SET date = ?, category = ?, amount = ?, memo = ?
                WHERE id = ?
                """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, expense.getDate().toString());
            pstmt.setString(2, expense.getCategory());
            pstmt.setInt(3, expense.getAmount());
            pstmt.setString(4, expense.getMemo());
            pstmt.setInt(5, expense.getId());

            int rows = pstmt.executeUpdate();

            return rows == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteById(int id) {

        String sql = "DELETE FROM expenses WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            return rows == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public Map<String, Integer> findMonthlySummary(String ym) {

        Map<String, Integer> result = new HashMap<>();

        String sql = """
                SELECT category, SUM(amount) AS total
                FROM expenses
                WHERE strftime('%Y-%m', date) = ?
                GROUP BY category
                """;

        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ym);

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                result.put(
                        rs.getString("category"),
                        rs.getInt("total")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}

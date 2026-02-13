package com.expensetracker.dao;

import com.expensetracker.model.Expense;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data access object for expense-related operations.
 */
public class ExpenseDAO {

    private static final String INSERT_EXPENSE_SQL =
            "INSERT INTO expenses (user_id, amount, category, description, expense_date, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, NOW())";

    private static final String UPDATE_EXPENSE_SQL =
            "UPDATE expenses SET amount = ?, category = ?, description = ?, expense_date = ? " +
                    "WHERE id = ? AND user_id = ?";

    private static final String DELETE_EXPENSE_SQL =
            "DELETE FROM expenses WHERE id = ? AND user_id = ?";

    private static final String SELECT_EXPENSE_BY_ID_SQL =
            "SELECT id, user_id, amount, category, description, expense_date, created_at " +
                    "FROM expenses WHERE id = ? AND user_id = ?";

    private static final String BASE_SELECT_BY_USER_SQL =
            "SELECT id, user_id, amount, category, description, expense_date, created_at " +
                    "FROM expenses WHERE user_id = ?";

    private static final String RECENT_EXPENSES_SQL =
            "SELECT id, user_id, amount, category, description, expense_date, created_at " +
                    "FROM expenses WHERE user_id = ? ORDER BY expense_date DESC, created_at DESC LIMIT ?";

    private static final String TOTAL_EXPENSES_SQL =
            "SELECT COALESCE(SUM(amount), 0) AS total FROM expenses WHERE user_id = ?";

    private static final String CATEGORY_BREAKDOWN_SQL =
            "SELECT category, COALESCE(SUM(amount), 0) AS total " +
                    "FROM expenses WHERE user_id = ? GROUP BY category ORDER BY total DESC";

    private static final String MONTHLY_SUMMARY_SQL =
            "SELECT DATE_FORMAT(expense_date, '%Y-%m') AS month, COALESCE(SUM(amount), 0) AS total " +
                    "FROM expenses WHERE user_id = ? GROUP BY month ORDER BY month DESC LIMIT 12";

    private static final String ALL_CATEGORIES_SQL =
            "SELECT name FROM categories ORDER BY name ASC";

    /**
     * Inserts a new expense.
     */
    public boolean addExpense(Expense expense) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_EXPENSE_SQL)) {
            ps.setInt(1, expense.getUserId());
            ps.setBigDecimal(2, expense.getAmount());
            ps.setString(3, expense.getCategory());
            ps.setString(4, expense.getDescription());
            ps.setDate(5, Date.valueOf(expense.getExpenseDate()));
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Updates an existing expense.
     */
    public boolean updateExpense(Expense expense) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_EXPENSE_SQL)) {
            ps.setBigDecimal(1, expense.getAmount());
            ps.setString(2, expense.getCategory());
            ps.setString(3, expense.getDescription());
            ps.setDate(4, Date.valueOf(expense.getExpenseDate()));
            ps.setInt(5, expense.getId());
            ps.setInt(6, expense.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes an expense by id for a specific user.
     */
    public boolean deleteExpense(int id, int userId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_EXPENSE_SQL)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Finds an expense by id for a specific user.
     */
    public Expense findById(int id, int userId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_EXPENSE_BY_ID_SQL)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToExpense(rs);
                }
            }
        }
        return null;
    }

    /**
     * Returns all expenses for a user with optional filters.
     *
     * @param userId    user id
     * @param fromDate  optional start date filter
     * @param toDate    optional end date filter
     * @param category  optional category filter
     */
    public List<Expense> getExpensesForUser(int userId, LocalDate fromDate, LocalDate toDate, String category) throws SQLException {
        StringBuilder sql = new StringBuilder(BASE_SELECT_BY_USER_SQL);
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (fromDate != null) {
            sql.append(" AND expense_date >= ?");
            params.add(Date.valueOf(fromDate));
        }
        if (toDate != null) {
            sql.append(" AND expense_date <= ?");
            params.add(Date.valueOf(toDate));
        }
        if (category != null && !category.isEmpty() && !"All".equalsIgnoreCase(category)) {
            sql.append(" AND category = ?");
            params.add(category);
        }

        sql.append(" ORDER BY expense_date DESC, created_at DESC");

        List<Expense> expenses = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else if (param instanceof Date) {
                    ps.setDate(i + 1, (Date) param);
                } else if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapRowToExpense(rs));
                }
            }
        }
        return expenses;
    }

    /**
     * Returns recent expenses for dashboard.
     */
    public List<Expense> getRecentExpenses(int userId, int limit) throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(RECENT_EXPENSES_SQL)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapRowToExpense(rs));
                }
            }
        }
        return expenses;
    }

    /**
     * Returns total expenses for a user.
     */
    public BigDecimal getTotalExpenses(int userId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(TOTAL_EXPENSES_SQL)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Returns category-wise total amounts for the user.
     */
    public Map<String, BigDecimal> getCategoryBreakdown(int userId) throws SQLException {
        Map<String, BigDecimal> breakdown = new LinkedHashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(CATEGORY_BREAKDOWN_SQL)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    breakdown.put(rs.getString("category"), rs.getBigDecimal("total"));
                }
            }
        }
        return breakdown;
    }

    /**
     * Returns monthly spending summary for the last 12 months.
     */
    public Map<String, BigDecimal> getMonthlySummary(int userId) throws SQLException {
        Map<String, BigDecimal> summary = new LinkedHashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(MONTHLY_SUMMARY_SQL)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    summary.put(rs.getString("month"), rs.getBigDecimal("total"));
                }
            }
        }
        return summary;
    }

    /**
     * Returns the list of all category names from categories table.
     */
    public List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(ALL_CATEGORIES_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        }
        return categories;
    }

    private Expense mapRowToExpense(ResultSet rs) throws SQLException {
        Expense expense = new Expense();
        expense.setId(rs.getInt("id"));
        expense.setUserId(rs.getInt("user_id"));
        expense.setAmount(rs.getBigDecimal("amount"));
        expense.setCategory(rs.getString("category"));
        expense.setDescription(rs.getString("description"));
        Date date = rs.getDate("expense_date");
        if (date != null) {
            expense.setExpenseDate(date.toLocalDate());
        } else {
            expense.setExpenseDate(LocalDate.now());
        }
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            expense.setCreatedAt(ts.toLocalDateTime());
        } else {
            expense.setCreatedAt(LocalDateTime.now());
        }
        return expense;
    }
}


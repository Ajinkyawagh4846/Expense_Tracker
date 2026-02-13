package com.expensetracker.servlet;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Displays dashboard statistics for the logged-in user.
 */
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ExpenseDAO expenseDAO = new ExpenseDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("currentUser");

        try {
            BigDecimal totalExpenses = expenseDAO.getTotalExpenses(user.getId());
            Map<String, BigDecimal> categoryBreakdown = expenseDAO.getCategoryBreakdown(user.getId());
            Map<String, BigDecimal> monthlySummary = expenseDAO.getMonthlySummary(user.getId());
            List<Expense> recentExpenses = expenseDAO.getRecentExpenses(user.getId(), 5);

            request.setAttribute("totalExpenses", totalExpenses);
            request.setAttribute("categoryBreakdown", categoryBreakdown);
            request.setAttribute("monthlySummary", monthlySummary);
            request.setAttribute("recentExpenses", recentExpenses);

            RequestDispatcher dispatcher = request.getRequestDispatcher("dashboard.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Unable to load dashboard data.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("dashboard.jsp");
            dispatcher.forward(request, response);
        } finally {
            // DAO uses try-with-resources, no explicit cleanup required.
        }
    }
}


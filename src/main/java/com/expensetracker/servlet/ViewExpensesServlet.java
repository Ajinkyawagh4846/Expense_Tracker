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
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Displays all expenses with filtering options.
 */
public class ViewExpensesServlet extends HttpServlet {

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

        String fromDateStr = request.getParameter("fromDate");
        String toDateStr = request.getParameter("toDate");
        String category = request.getParameter("category");

        LocalDate fromDate = null;
        LocalDate toDate = null;

        try {
            if (fromDateStr != null && !fromDateStr.isEmpty()) {
                fromDate = LocalDate.parse(fromDateStr);
            }
            if (toDateStr != null && !toDateStr.isEmpty()) {
                toDate = LocalDate.parse(toDateStr);
            }
        } catch (DateTimeParseException e) {
            request.setAttribute("errorMessage", "Invalid date range.");
        }

        try {
            List<String> categories = expenseDAO.getAllCategories();
            List<Expense> expenses = expenseDAO.getExpensesForUser(user.getId(), fromDate, toDate, category);

            request.setAttribute("categories", categories);
            request.setAttribute("expenses", expenses);
            request.setAttribute("selectedCategory", category);
            request.setAttribute("fromDate", fromDateStr);
            request.setAttribute("toDate", toDateStr);

            RequestDispatcher dispatcher = request.getRequestDispatcher("view-expenses.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Unable to load expenses.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("view-expenses.jsp");
            dispatcher.forward(request, response);
        } finally {
            // no-op
        }
    }
}


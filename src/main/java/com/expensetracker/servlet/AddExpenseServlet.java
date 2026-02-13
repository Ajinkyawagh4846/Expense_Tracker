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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Handles adding new expenses.
 */
public class AddExpenseServlet extends HttpServlet {

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

        try {
            List<String> categories = expenseDAO.getAllCategories();
            request.setAttribute("categories", categories);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Unable to load categories.");
        } finally {
            // no-op
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("add-expense.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("currentUser");

        String amountStr = request.getParameter("amount");
        String category = request.getParameter("category");
        String description = request.getParameter("description");
        String expenseDateStr = request.getParameter("expenseDate");

        BigDecimal amount;
        LocalDate expenseDate;

        try {
            amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException("Amount must be positive");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Please enter a valid positive amount.");
            reloadCategoriesAndForward(request, response);
            return;
        }

        try {
            expenseDate = LocalDate.parse(expenseDateStr);
        } catch (DateTimeParseException e) {
            request.setAttribute("errorMessage", "Please enter a valid date.");
            reloadCategoriesAndForward(request, response);
            return;
        }

        if (category == null || category.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Please select a category.");
            reloadCategoriesAndForward(request, response);
            return;
        }

        Expense expense = new Expense();
        expense.setUserId(user.getId());
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setDescription(description);
        expense.setExpenseDate(expenseDate);

        try {
            boolean added = expenseDAO.addExpense(expense);
            if (added) {
                session.setAttribute("successMessage", "Expense added successfully.");
                response.sendRedirect(request.getContextPath() + "/view-expenses");
            } else {
                request.setAttribute("errorMessage", "Failed to add expense. Please try again.");
                reloadCategoriesAndForward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An internal error occurred while adding expense.");
            reloadCategoriesAndForward(request, response);
        } finally {
            // DAO uses try-with-resources.
        }
    }

    private void reloadCategoriesAndForward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<String> categories = expenseDAO.getAllCategories();
            request.setAttribute("categories", categories);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Unable to load categories.");
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("add-expense.jsp");
        dispatcher.forward(request, response);
    }
}


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
 * Handles editing existing expenses.
 */
public class EditExpenseServlet extends HttpServlet {

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
        String idStr = request.getParameter("id");

        try {
            int id = Integer.parseInt(idStr);
            Expense expense = expenseDAO.findById(id, user.getId());
            if (expense == null) {
                session.setAttribute("errorMessage", "Expense not found.");
                response.sendRedirect(request.getContextPath() + "/view-expenses");
                return;
            }
            List<String> categories = expenseDAO.getAllCategories();
            request.setAttribute("categories", categories);
            request.setAttribute("expense", expense);
            RequestDispatcher dispatcher = request.getRequestDispatcher("edit-expense.jsp");
            dispatcher.forward(request, response);
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Invalid expense id.");
            response.sendRedirect(request.getContextPath() + "/view-expenses");
        } finally {
            // no-op
        }
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
        String idStr = request.getParameter("id");
        String amountStr = request.getParameter("amount");
        String category = request.getParameter("category");
        String description = request.getParameter("description");
        String expenseDateStr = request.getParameter("expenseDate");

        int id;
        BigDecimal amount;
        LocalDate expenseDate;

        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid expense id.");
            response.sendRedirect(request.getContextPath() + "/view-expenses");
            return;
        }

        try {
            amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException("Amount must be positive");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Please enter a valid positive amount.");
            reloadForEdit(request, response, user, id);
            return;
        }

        try {
            expenseDate = LocalDate.parse(expenseDateStr);
        } catch (DateTimeParseException e) {
            request.setAttribute("errorMessage", "Please enter a valid date.");
            reloadForEdit(request, response, user, id);
            return;
        }

        if (category == null || category.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Please select a category.");
            reloadForEdit(request, response, user, id);
            return;
        }

        Expense expense = new Expense();
        expense.setId(id);
        expense.setUserId(user.getId());
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setDescription(description);
        expense.setExpenseDate(expenseDate);

        try {
            boolean updated = expenseDAO.updateExpense(expense);
            if (updated) {
                session.setAttribute("successMessage", "Expense updated successfully.");
                response.sendRedirect(request.getContextPath() + "/view-expenses");
            } else {
                request.setAttribute("errorMessage", "Failed to update expense. Please try again.");
                reloadForEdit(request, response, user, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An internal error occurred while updating expense.");
            reloadForEdit(request, response, user, id);
        } finally {
            // DAO uses try-with-resources.
        }
    }

    private void reloadForEdit(HttpServletRequest request, HttpServletResponse response, User user, int id)
            throws ServletException, IOException {
        try {
            Expense expense = expenseDAO.findById(id, user.getId());
            List<String> categories = expenseDAO.getAllCategories();
            request.setAttribute("expense", expense);
            request.setAttribute("categories", categories);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("edit-expense.jsp");
        dispatcher.forward(request, response);
    }
}


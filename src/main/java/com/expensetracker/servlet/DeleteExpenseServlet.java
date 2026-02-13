package com.expensetracker.servlet;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Handles deleting expenses.
 */
public class DeleteExpenseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ExpenseDAO expenseDAO = new ExpenseDAO();

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

        try {
            int id = Integer.parseInt(idStr);
            boolean deleted = expenseDAO.deleteExpense(id, user.getId());
            if (deleted) {
                session.setAttribute("successMessage", "Expense deleted successfully.");
            } else {
                session.setAttribute("errorMessage", "Unable to delete expense.");
            }
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Invalid expense id.");
        } finally {
            response.sendRedirect(request.getContextPath() + "/view-expenses");
        }
    }
}


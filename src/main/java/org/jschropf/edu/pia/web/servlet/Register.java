package org.jschropf.edu.pia.web.servlet;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jschropf.edu.pia.domain.User;
import org.jschropf.edu.pia.domain.UserValidationException;
import org.jschropf.edu.pia.manager.UserManager;

/**
 * Servlet handling user registration requests.
 *
 * Date: 26.11.15
 *
 * @author Jakub Danek
 */
public class Register extends HttpServlet {

    private static final String USERNAME_PARAMETER = "username";
    private static final String PASSWORD_PARAMETER = "password";
    private static final String CONFIRM_PWD_PARAMETER = "confirmPwd";

    private static final String ERROR_ATTRIBUTE = "err";

    private UserManager userManager;

    public Register(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("WEB-INF/pages/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter(USERNAME_PARAMETER);
        String password = req.getParameter(PASSWORD_PARAMETER);
        String confirmPwd = req.getParameter(CONFIRM_PWD_PARAMETER);

        if(!Objects.equals(password, confirmPwd)) {
            errorDispatch("The password and confirm password fields do not match!", req, resp);
            return;
        }

        try {
            userManager.register(new User(username, password));
            resp.sendRedirect("/");  //not perfect, user should get a message registration was successful!
        } catch (UserValidationException e) {
            errorDispatch(e.getMessage(), req, resp);
        }
    }

    private void errorDispatch(String err, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute(ERROR_ATTRIBUTE, err);
        req.getRequestDispatcher("WEB-INF/pages/register.jsp").forward(req, resp);
    }
}

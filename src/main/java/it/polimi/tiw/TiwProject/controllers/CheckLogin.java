package it.polimi.tiw.TiwProject.controllers;

import it.polimi.tiw.TiwProject.beans.User;
import it.polimi.tiw.TiwProject.dao.UserDAO;
import it.polimi.tiw.TiwProject.exception.BadLoginException;
import it.polimi.tiw.TiwProject.utils.ConnectionHandler;
import org.apache.commons.lang3.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "CheckLogin", value = "/CheckLogin")
public class CheckLogin extends HttpServlet {

    private Connection connection = null;
    private TemplateEngine templateEngine;

    public void init() throws ServletException {

        // open connection to DB
        connection = ConnectionHandler.getConnection(getServletContext());

        // set up Template Engine
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String email = null;
        String password = null;
        String errorMessage = null;

        // check for syntactic errors
        try{

            email = StringEscapeUtils.escapeJava(request.getParameter("email"));
            password = StringEscapeUtils.escapeJava(request.getParameter("user-password"));

            if (email == null ){

                errorMessage = "Bad email: email is null";
                throw new BadLoginException(errorMessage);
            } else if ( email.isEmpty() ){

                errorMessage = "Bad email: email is empty";
                throw new BadLoginException(errorMessage);
            }else if ( !email.contains("@") ){

                errorMessage = "Bad email: email must contain @ ";
                throw new BadLoginException(errorMessage);
            } else if ( password == null  ){

                errorMessage = "Bad Password: password is null";
                throw new BadLoginException(errorMessage);
            } else if (password.isEmpty()){

                errorMessage = "Bad Password: password is empty";
                throw new BadLoginException(errorMessage);
            }

        } catch (BadLoginException badLoginException){

            // redirects to login screen with error message
            String path = "/WEB-INF/Templates/login.html";
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            ctx.setVariable("errorMsg", badLoginException.getMessage());
            templateEngine.process(path, ctx, response.getWriter());
            return;
        } catch ( Exception e){

            e.printStackTrace(); //TODO Comment
            String path = "/WEB-INF/Templates/login.html";
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            ctx.setVariable("errorMsg", errorMessage);
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }


        UserDAO userDAO = new UserDAO(connection); // Create DAO Object for Users
        User user = null;

        try{

            user = userDAO.checkCredentials(email,password);
            System.out.println(email); //TODO Comment
            System.out.println(password); //TODO Comment
        } catch (SQLException exception){

            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not Possible to check credentials");
            return;
        } catch (BadLoginException badLoginException){

            // redirects to login screen with error message
            String path = "/WEB-INF/Templates/login.html";
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            ctx.setVariable("errorMsg", badLoginException.getMessage());
            templateEngine.process(path, ctx, response.getWriter());
            return;
        } 

        if (user == null) {

            // redirects to login screen with error message
            String path = "/WEB-INF/Templates/login.html";
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            ctx.setVariable("errorMsg", "User does not exists");
            templateEngine.process(path, ctx, response.getWriter());
        } else {

            // sets the user as a session attribute
            request.getSession().setAttribute("user", user);

            // redirects to the GoToHome Servlet
            String path = getServletContext().getContextPath() + "/GoToHome";
            response.sendRedirect(path);

        }
    }

    public void destroy(){

        try{

            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e){

            e.printStackTrace();
        }
    }
}

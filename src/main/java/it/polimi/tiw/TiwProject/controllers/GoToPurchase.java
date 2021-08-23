package it.polimi.tiw.TiwProject.controllers;

import it.polimi.tiw.TiwProject.beans.Auction;
import it.polimi.tiw.TiwProject.beans.DashboardAuction;
import it.polimi.tiw.TiwProject.dao.DashboardAuctionDAO;
import it.polimi.tiw.TiwProject.utils.ConnectionHandler;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "GoToPurchase", value = "/GoToPurchase")
public class GoToPurchase extends HttpServlet {

    private TemplateEngine templateEngine;
    private Connection connection = null;

    public void init() throws ServletException {

        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("user") == null) {

            String loginPath = getServletContext().getContextPath() + "/hello-servlet";
            response.sendRedirect(loginPath);
            return;
        } else {

            String keystring = "";
            DashboardAuctionDAO dashboardAuctionDAO = new DashboardAuctionDAO(connection);
            List<DashboardAuction> auctionList = new ArrayList<>();

            try {

                auctionList = dashboardAuctionDAO.getListOfOpenAuctionByKeystring(keystring);
            } catch (SQLException e){

                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Non é possibile recuperare l'elenco delle Aste");
                e.printStackTrace();
                return;
            }

            String path = "/WEB-INF/Templates/Purchase.html";
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            ctx.setVariable("auctionList", auctionList);
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("user") == null) {

            String loginPath = getServletContext().getContextPath() + "/hello-servlet";
            response.sendRedirect(loginPath);
            return;
        } else {

            String keystring = null;

            try {

                keystring = request.getParameter("keystring");
            } catch ( NullPointerException e){

                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
                return;
            }

            // validation

            if ( (keystring == null) || (keystring.isEmpty())){

                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "A search string must be provided");
                return;
            }

            // get a new list with the searched keystring

            DashboardAuctionDAO dashboardAuctionDAO = new DashboardAuctionDAO(connection);
            List<DashboardAuction> auctionList = new ArrayList<>();

            try {

                auctionList = dashboardAuctionDAO.getListOfOpenAuctionByKeystring(keystring);
            } catch (SQLException e){

                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Non é possibile recuperare l'elenco delle Aste");
                e.printStackTrace();
                return;
            }

            String path = "/WEB-INF/Templates/Purchase.html";
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            ctx.setVariable("auctionList", auctionList);
            templateEngine.process(path, ctx, response.getWriter());
            return;

        }
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

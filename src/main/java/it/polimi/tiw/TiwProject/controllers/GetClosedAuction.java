package it.polimi.tiw.TiwProject.controllers;

import it.polimi.tiw.TiwProject.beans.DashboardAuction;
import it.polimi.tiw.TiwProject.beans.Offer;
import it.polimi.tiw.TiwProject.beans.User;
import it.polimi.tiw.TiwProject.dao.DashboardAuctionDAO;
import it.polimi.tiw.TiwProject.dao.OfferDAO;
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

@WebServlet(name = "GetClosedAuction", value = "/GetClosedAuction")
public class GetClosedAuction extends HttpServlet {

    private Connection connection = null;
    private TemplateEngine templateEngine = null;

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

            Integer auctionId = null;

            try{
                auctionId = Integer.parseInt(request.getParameter("auction_id"));
            } catch (NumberFormatException | NullPointerException e){

                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param value");
                return;
            }

            User user = (User) session.getAttribute("user");

            DashboardAuctionDAO dashboardAuctionDAO = new DashboardAuctionDAO(connection);
            DashboardAuction dashboardAuction = null;

            OfferDAO offerDAO = new OfferDAO(connection);
            Offer winnerOffer = null;

            try{

                dashboardAuction = dashboardAuctionDAO.getAuctionDetail(auctionId);
                winnerOffer = offerDAO.winningBetForAuction(auctionId);

                if (dashboardAuction == null){

                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Auction not found");
                    return;
                }
                if (dashboardAuction.getId_user() != user.getId()){

                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unauthorized user");
                    return;
                }

                if (dashboardAuction.isOpen()){

                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Auction is open");
                    return;
                }

            } catch ( SQLException e){

                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover mission");
                return;
            }

            String path = "/WEB-INF/Templates/ClosedAuctionDet.html";
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            ctx.setVariable("auction", dashboardAuction);
            ctx.setVariable("offer", winnerOffer);
            templateEngine.process(path, ctx, response.getWriter());
            return;

        }
    }
}

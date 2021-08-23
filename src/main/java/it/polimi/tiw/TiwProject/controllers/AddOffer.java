package it.polimi.tiw.TiwProject.controllers;

import it.polimi.tiw.TiwProject.beans.DashboardAuction;
import it.polimi.tiw.TiwProject.beans.Offer;
import it.polimi.tiw.TiwProject.beans.User;
import it.polimi.tiw.TiwProject.dao.DashboardAuctionDAO;
import it.polimi.tiw.TiwProject.dao.OfferDAO;
import it.polimi.tiw.TiwProject.utils.ConnectionHandler;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "AddOffer", value = "/AddOffer")
public class AddOffer extends HttpServlet {

    private Connection connection =null;

    public void init() throws ServletException{

        connection = ConnectionHandler.getConnection(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("user") == null) {

            String loginPath = getServletContext().getContextPath() + "/hello-servlet";
            response.sendRedirect(loginPath);
            return;
        } else {

            // get parameter from request
            User currentUser = (User) session.getAttribute("user");
            float amount = -1;
            int id_auction = -1;
            String sh_address = null;

            try {

                amount = Float.parseFloat(request.getParameter("amount"));
                id_auction = Integer.parseInt(request.getParameter("id_auction"));
                sh_address = request.getParameter("sh_address");
            } catch (NumberFormatException | NullPointerException e){

                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param value");
                return;
            }

            if ((sh_address==null) || (sh_address.isEmpty()) || (sh_address.length() > 100)){

                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Shipping Address is empty or longer than 100 char");
                return;
            }

            // get auction by specified id
            DashboardAuctionDAO dashboardAuctionDAO = new DashboardAuctionDAO(connection);
            DashboardAuction dashboardAuction = null;

            try {

                dashboardAuction = dashboardAuctionDAO.getAuctionDetail(id_auction);
            } catch ( SQLException e){

                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover auction");
                return;
            }

            if (dashboardAuction == null){

                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Auction not found");
                return;
            }

            if (dashboardAuction.isClosed()){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Auction is closed");
                return;
            }

            if ( (amount == -1) || (amount < (dashboardAuction.getMin_rise() + dashboardAuction.getWinningBet()))){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Offer is less than required");
                return;
            }

            // create and save  new Offer

            OfferDAO offerDAO = new OfferDAO(connection);
            Offer offer = new Offer(
                    amount,
                    sh_address,
                    currentUser.getId(),
                    id_auction

            );

            try {
                offerDAO.createOffer(offer);
            } catch (SQLException e){

                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to save offer");
                return;
            }
        }

        // redirects to Purchase
        String path = getServletContext().getContextPath() + "/GoToPurchase";
        response.sendRedirect(path);
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

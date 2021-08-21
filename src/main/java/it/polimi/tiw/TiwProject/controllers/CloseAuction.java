package it.polimi.tiw.TiwProject.controllers;

import it.polimi.tiw.TiwProject.beans.Auction;
import it.polimi.tiw.TiwProject.beans.DashboardAuction;
import it.polimi.tiw.TiwProject.beans.User;
import it.polimi.tiw.TiwProject.dao.AuctionDAO;
import it.polimi.tiw.TiwProject.dao.DashboardAuctionDAO;
import it.polimi.tiw.TiwProject.utils.ConnectionHandler;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "CloseAuction", value = "/CloseAuction")
public class CloseAuction extends HttpServlet {

    private Connection connection = null;

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

            AuctionDAO auctionDAO = new AuctionDAO(connection);

            try {

                dashboardAuction = dashboardAuctionDAO.getAuctionDetail(auctionId);

                if (dashboardAuction == null){

                    errorRender("Auction not found", response);
                    return;
                }

                if (dashboardAuction.getId_user() != user.getId()){

                    errorRender("Unauthorized user", response);
                    return;
                }

                if (dashboardAuction.isClosed()){

                    errorRender("Auction is already closed", response);
                    return;
                }

                auctionDAO.closeAuction(auctionId);

            } catch (SQLException e){

                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover mission");
                return;
            }

            String loginPath = getServletContext().getContextPath() + "/GoToSell";
            response.sendRedirect(loginPath);
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

    private void errorRender(String message, HttpServletResponse response) throws IOException{
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);

    }
}

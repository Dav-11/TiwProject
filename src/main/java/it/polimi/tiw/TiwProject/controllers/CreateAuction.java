package it.polimi.tiw.TiwProject.controllers;

import it.polimi.tiw.TiwProject.beans.Auction;
import it.polimi.tiw.TiwProject.beans.Item;
import it.polimi.tiw.TiwProject.beans.User;
import it.polimi.tiw.TiwProject.dao.AuctionDAO;
import it.polimi.tiw.TiwProject.dao.ItemDAO;
import it.polimi.tiw.TiwProject.utils.ConnectionHandler;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@WebServlet(name = "CreateAuction", value = "/CreateAuction")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
maxFileSize = 1024 * 1024 * 10, // 10 MB
maxRequestSize = 1024 * 1024 * 50 ) // 50 MB
public class CreateAuction extends HttpServlet {

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

            boolean isBadRequest = false;

            User currentUser = (User) session.getAttribute("user");
            String itemName = null;
            String itemDescription = null;
            Part itemPicturePart = null;
            InputStream itemPicture = null;
            Date end_date = null;
            float min_rise = -1;
            float initial_price = -1;

            try{

                itemName = StringEscapeUtils.escapeJava(request.getParameter("itemName"));
                itemDescription = ( request.getParameter("itemDescription") == null ) ? null : StringEscapeUtils.escapeJava(request.getParameter("itemDescription"));
                itemPicturePart = request.getPart("itemPicture");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                end_date = (Date) sdf.parse(request.getParameter("end_date"));
                initial_price = Float.parseFloat(request.getParameter("initial_price"));
                min_rise = Float.parseFloat(request.getParameter("min_rise"));

                //System.out.println("itmName: " + itemName + " description:" + itemDescription + " picture: " + itemPicture + " end_date:" + end_date + " min_rise:" + min_rise);

            } catch (NumberFormatException | NullPointerException | ParseException e){

                isBadRequest = true;
                e.printStackTrace();
            }

            if ( (itemName == null) || (itemName.isEmpty()) || (end_date == null)){

                errorRender("A name, and an end date must be provided", response);
                return;
            }

            if (min_rise < 0){

                errorRender("The Minimum rise must be >= 0", response);
                return;
            }

            if (initial_price < 0){

                errorRender("Initial price must be >= 0", response);
                return;
            }

            if ((itemPicturePart != null) && (itemPicturePart.getSize() > 0)){

                String contentType = itemPicturePart.getContentType();
                System.out.println("File contentType: " + contentType);
                if (! contentType.startsWith("image")){

                    errorRender("File must be of type jpeg", response);
                    return;
                }

                itemPicture = itemPicturePart.getInputStream();
            }

            if (itemDescription.length() >500 ){

                errorRender("Description must be less than 500 character long", response);
                return;
            }

            if (! end_date.after(new Date())){

                errorRender("Date must be in the future", response);
                return;
            }

            if (isBadRequest) {
                errorRender("Incorrect or missing param values", response);
                return;
            }


            // create a new Auction bean and saves it

            AuctionDAO auctionDAO = new AuctionDAO(connection);
            Auction auction = new Auction(
                    currentUser.getId(),
                    new Date(),
                    end_date,
                    min_rise,
                    initial_price,
                    true
                    );

            try {
                auction.setId(auctionDAO.createAuction(auction));
            } catch (SQLException e){
                errorRender("Not possible to create auction", response);
                return;
            }

            // create a new Item bean and saves it

            ItemDAO itemDAO = new ItemDAO(connection);
            Item item = new Item(
                    itemName,
                    (itemDescription.isEmpty())? null : itemDescription,
                    itemPicture,
                    auction.getId()
            );

            try {
                item.setId(itemDAO.createItem(item));
            } catch (SQLException e){
                errorRender("Not possible to create item", response);
                e.printStackTrace();
                return;
            }

            // redirects to sell
            String path = getServletContext().getContextPath() + "/GoToSell";
            response.sendRedirect(path);
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
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);

    }
}

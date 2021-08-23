package it.polimi.tiw.TiwProject.dao;

import it.polimi.tiw.TiwProject.beans.Offer;
import it.polimi.tiw.TiwProject.beans.User;
import it.polimi.tiw.TiwProject.exception.BadLoginException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OfferDAO {

    private Connection connection;

    public OfferDAO(Connection connection) {
        this.connection = connection;
    }

    public Offer winningBetForAuction(int auctionId) throws SQLException {

        String query = "SELECT * FROM offer WHERE id_auction = ? ORDER BY amount DESC ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, auctionId);

            try (ResultSet resultSet = preparedStatement.executeQuery();) {

                if (!resultSet.isBeforeFirst()) {

                    return null; // no offer found
                } else {

                    resultSet.next();

                    UserDAO userDAO = new UserDAO(connection);

                    return new Offer(
                            resultSet.getInt("id"),
                            resultSet.getFloat("amount"),
                            resultSet.getString("sh_address"),
                            resultSet.getInt("id_user"),
                            resultSet.getInt("id_auction"),
                            new Date(resultSet.getTimestamp("date").getTime()),
                            userDAO.getUserUsername(resultSet.getInt("id_user"))
                    );
                }
            }
        }
    }

    public List<Offer> offerListForAuction( int auctionId) throws SQLException{

        List<Offer> offerList = new ArrayList<>();
        String query = "SELECT * FROM offer WHERE id_auction = ? ORDER BY amount DESC ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, auctionId);

            try (ResultSet resultSet = preparedStatement.executeQuery();) {

                UserDAO userDAO = new UserDAO(connection);

                while (resultSet.next()){

                    Offer offer = new Offer(
                            resultSet.getInt("id"),
                            resultSet.getFloat("amount"),
                            resultSet.getString("sh_address"),
                            resultSet.getInt("id_user"),
                            resultSet.getInt("id_auction"),
                            new Date(resultSet.getTimestamp("date").getTime()),
                            userDAO.getUserUsername(resultSet.getInt("id_user"))
                    );
                    offerList.add(offer);
                }
            }
        }

        return offerList;
    }

    public int createOffer(Offer offer) throws SQLException {

        String query = "INSERT INTO offer (amount, sh_address, id_user, id_auction, date) VALUES (?,?,?,?,NOW())";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {

            preparedStatement.setFloat(1, offer.getAmount());
            preparedStatement.setString(2, offer.getSh_address());
            preparedStatement.setInt(3,offer.getId_user());
            preparedStatement.setInt(4, offer.getId_auction());

            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()){

                if (generatedKeys.next()){

                    return generatedKeys.getInt(1);
                }

                return -1;
            }
        }
    }
}

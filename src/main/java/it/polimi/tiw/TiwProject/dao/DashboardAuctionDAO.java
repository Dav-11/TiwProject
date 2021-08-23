package it.polimi.tiw.TiwProject.dao;

import it.polimi.tiw.TiwProject.beans.DashboardAuction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class DashboardAuctionDAO {

    private Connection connection;

    public DashboardAuctionDAO(Connection connection) {
        this.connection = connection;
    }

    public List<DashboardAuction> getListOfOpenAuctionsByUser(int userId) throws SQLException {

        List<DashboardAuction> dashboardAuctionList = new ArrayList<>();
        String query = "SELECT * FROM auction_dashboard WHERE ( open = 1 AND id_user = ? ) ORDER BY end_date ASC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setInt(1, userId);

            try (ResultSet result = preparedStatement.executeQuery();) {

                dashboardAuctionList = listBuilder(result);
            }
        }

        return  dashboardAuctionList;
    }

    public List<DashboardAuction> getListOfClosedAuctionsByUser(int userId) throws SQLException {

        List<DashboardAuction> dashboardAuctionList = new ArrayList<>();
        String query = "SELECT * FROM auction_dashboard WHERE ( open = 0 AND id_user = ? ) ORDER BY end_date ASC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setInt(1, userId);

            try (ResultSet result = preparedStatement.executeQuery();) {

                dashboardAuctionList = listBuilder(result);
            }
        }

        return  dashboardAuctionList;
    }

    public List<DashboardAuction> getListOfOpenAuctions() throws SQLException{

        List<DashboardAuction> dashboardAuctionList = new ArrayList<>();
        String query = "SELECT * FROM auction_dashboard WHERE open = 1 ORDER BY (end_date - NOW()) ASC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){

            try (ResultSet result = preparedStatement.executeQuery();) {

                dashboardAuctionList = listBuilder(result);
            }
        }

        return  dashboardAuctionList;
    }

    public List<DashboardAuction> getListOfOpenAuctionByKeystring(String keystring) throws SQLException{

        String searchstring = "%" + keystring + "%";
        List<DashboardAuction> dashboardAuctionList = new ArrayList<>();

        String query = "SELECT * FROM auction_dashboard WHERE (name LIKE ? OR description LIKE ?) AND ( end_date - NOW() > 0 ) ORDER BY (end_date - NOW()) ASC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setString(1, searchstring);
            preparedStatement.setString(2, searchstring);

            try (ResultSet result = preparedStatement.executeQuery();) {

                dashboardAuctionList = listBuilder(result);
            }
        }

        return  dashboardAuctionList;
    }

    public DashboardAuction getAuctionDetail(int auctionId) throws SQLException{

        String query = "SELECT * FROM auction_dashboard WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setInt(1, auctionId);

            try (ResultSet result = preparedStatement.executeQuery();) {

                if (!result.isBeforeFirst()) {

                    return null; // no user found
                } else {

                    result.next();
                    return new DashboardAuction(
                            result.getInt("id"),
                            result.getString("description"),
                            result.getString("name"),
                            result.getBinaryStream("picture"),
                            new Date(result.getTimestamp("end_date").getTime()),
                            new Date(result.getTimestamp("start_date").getTime()),
                            result.getInt("id_user"),
                            result.getInt("item_id"),
                            result.getInt("open") == 1,
                            result.getFloat("initial_price"),
                            result.getFloat("min_rise")
                    );
                }
            }
        }
    }


    private List<DashboardAuction> listBuilder(ResultSet result) throws SQLException{

        List<DashboardAuction> dashboardAuctionList = new ArrayList<>();

        while (result.next()) {

            DashboardAuction dashboardAuction = new DashboardAuction(
                    result.getInt("id"),
                    result.getString("description"),
                    result.getString("name"),
                    result.getBinaryStream("picture"),
                    new Date(result.getTimestamp("end_date").getTime()),
                    new Date(result.getTimestamp("start_date").getTime()),
                    result.getInt("id_user"),
                    result.getInt("item_id"),
                    result.getInt("open") == 1,
                    result.getFloat("initial_price"),
                    result.getFloat("min_rise")
            );

            OfferDAO offerDAO = new OfferDAO(this.connection);
            dashboardAuction.setWinningBet((offerDAO.winningBetForAuction(dashboardAuction.getId()) == null ) ? dashboardAuction.getInitial_price() : offerDAO.winningBetForAuction(dashboardAuction.getId()).getAmount());

            dashboardAuctionList.add(dashboardAuction);
        }

        return dashboardAuctionList;
    }


}

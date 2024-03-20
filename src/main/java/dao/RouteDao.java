
package dao;

import model.Route;
import model.Hub;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RouteDao implements IDao<Route> {

    private DBConnection dbConnection;

    public RouteDao(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public Route create(Route route) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            connection = dbConnection.getConnection();
            String query = "INSERT INTO route (routeName, startPoint, destinationPoint) VALUES (?, ?, ?)";
            statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, route.getRouteName());
            statement.setString(2, route.getStartPoint());
            statement.setString(3, route.getDestinationPoint());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating route failed, no rows affected.");
            }
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                route.setRouteId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating route failed, no ID obtained.");
            }
        } finally {
            if (generatedKeys != null) {
                generatedKeys.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return route;
    }

    @Override
    public boolean update(int id, Route route) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dbConnection.getConnection();
            String query = "UPDATE route SET routeName=?, startPoint=?, destinationPoint=? WHERE routeId=?";
            statement = connection.prepareStatement(query);
            statement.setString(1, route.getRouteName());
            statement.setString(2, route.getStartPoint());
            statement.setString(3, route.getDestinationPoint());
            statement.setInt(4, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dbConnection.getConnection();
            String query = "DELETE FROM route WHERE routeId=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public Route findOne(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Route route = null;
        try {
            connection = dbConnection.getConnection();
            String query = "SELECT * FROM route WHERE routeId=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                route = extractRouteFromResultSet(resultSet);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return route;
    }

    @Override
    public List<Route> findAll() throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Route> routes = new ArrayList<>();
        try {
            connection = dbConnection.getConnection();
            String query = "SELECT * FROM route";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Route route = extractRouteFromResultSet(resultSet);
                routes.add(route);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return routes;
    }

    private Route extractRouteFromResultSet(ResultSet resultSet) throws SQLException {
        Route route = new Route();
        route.setRouteId(resultSet.getInt("routeId"));
        route.setRouteName(resultSet.getString("routeName"));
        route.setStartPoint(resultSet.getString("startPoint"));
        route.setDestinationPoint(resultSet.getString("destinationPoint"));
        return route;
    }
}

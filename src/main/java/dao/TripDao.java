
package dao;

import model.Trip;
import model.Consignment;
import model.Route;
import model.Vehicle;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TripDao implements IDao<Trip> {

    private DBConnection dbConnection;

    public TripDao(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public Trip create(Trip trip) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            connection = dbConnection.getConnection();
            String query = "INSERT INTO trip (routeId, vehicleId, tripStartTime, tripEndTime, remarks) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setInt(1, trip.getRouteId().getRouteId());
            statement.setInt(2, trip.getVehicleId().getVehicleId());
            statement.setString(3, trip.getTripStartTime());
            statement.setString(4, trip.getTripEndTime());
            statement.setString(5, trip.getRemarks());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating trip failed, no rows affected.");
            }
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                trip.setTripId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating trip failed, no ID obtained.");
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
        return trip;
    }

    @Override
    public boolean update(int id, Trip trip) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dbConnection.getConnection();
            String query = "UPDATE trip SET routeId=?, vehicleId=?, tripStartTime=?, tripEndTime=?, remarks=? WHERE tripId=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, trip.getRouteId().getRouteId());
            statement.setInt(2, trip.getVehicleId().getVehicleId());
            statement.setString(3, trip.getTripStartTime());
            statement.setString(4, trip.getTripEndTime());
            statement.setString(5, trip.getRemarks());
            statement.setInt(6, id);
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
            String query = "DELETE FROM trip WHERE tripId=?";
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
    public Trip findOne(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Trip trip = null;
        try {
            connection = dbConnection.getConnection();
            String query = "SELECT * FROM trip WHERE tripId=?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                trip = extractTripFromResultSet(resultSet);
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
        return trip;
    }

    @Override
    public List<Trip> findAll() throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Trip> trips = new ArrayList<>();
        try {
            connection = dbConnection.getConnection();
            String query = "SELECT * FROM trip";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Trip trip = extractTripFromResultSet(resultSet);
                trips.add(trip);
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
        return trips;
    }

    private Trip extractTripFromResultSet(ResultSet resultSet) throws SQLException {
        Trip trip = new Trip();
        trip.setTripId(resultSet.getInt("tripId"));
        Route route = new Route();
        route.setRouteId(resultSet.getInt("routeId"));
        trip.setRouteId(route);
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(resultSet.getInt("vehicle"));
        trip.setVehicleId(vehicle);
        trip.setTripStartTime(resultSet.getString("tripStartTime"));
        trip.setTripEndTime(resultSet.getString("tripEndTime"));
        trip.setRemarks(resultSet.getString("remarks"));
        // Assuming you have a method to retrieve consignments associated with the trip
        // Replace this with the appropriate logic based on your application design
        ConsignmentDao consignmentDao = new ConsignmentDao(dbConnection);
        List<Consignment> consignments = consignmentDao.getConsignmentsByTripId(resultSet.getInt("tripId"));
        //trip.setConsignments(consignments);
        return trip;
    }
    
}

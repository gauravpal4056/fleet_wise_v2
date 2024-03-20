package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Driver;
import utils.DBConnection;

import java.io.IOException;

import dao.DriverDao;

//@WebServlet("/DriverServlet")
public class DriverServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public DriverServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String driverName = request.getParameter("driverName");
        String driverGender = request.getParameter("driverGender");
        int driverAge = Integer.parseInt(request.getParameter("driverAge")); // Convert string to int
        String licenceNumber = request.getParameter("licenceNumber");
        long driverPhone = Long.parseLong(request.getParameter("driverPhone")); // Convert string to long
        String driverEmail = request.getParameter("driverEmail");
        String driverAddress = request.getParameter("driverAddress");

        try {
            // Perform database operations to create a new driver entry
        	Class.forName("oracle.jdbc.driver.OracleDriver");
            DBConnection dbConnection = DBConnection.getDbConnnection();
            DriverDao dao = new DriverDao(dbConnection);
            Driver driver = new Driver(0, driverName, driverGender, driverAge, licenceNumber, driverPhone, driverEmail, driverAddress, null, null);
            Driver createdDriver = dao.create(driver);

            if (createdDriver != null) {
                // Driver entry created successfully
                // Forward the request to the JSP page to display success message or perform further actions
                request.setAttribute("createdDriver", createdDriver);
                System.out.println("Data Inserted Successfully.");
//                RequestDispatcher dispatcher = request.getRequestDispatcher("/Driver/Add_driver.jsp");
//                dispatcher.forward(request, response);
            } else {
                // Failed to create driver entry
                System.out.println("Failed to create driver entry.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Redirecting to doGet method to handle GET requests as well
        doGet(request, response);
    }
}

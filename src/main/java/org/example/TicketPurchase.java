package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TicketPurchase {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/ticketing_system";
    private static final String JDBC_USER = "username";
    private static final String JDBC_PASSWORD = "password";

    public static void purchaseTicket(String type, String modeOfTransport) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "INSERT INTO tickets (type, mode_of_transport) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, type);
                stmt.setString(2, modeOfTransport);
                stmt.executeUpdate();
                System.out.println("Ticket purchased successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error purchasing ticket: " + e.getMessage());
        }
    }
}
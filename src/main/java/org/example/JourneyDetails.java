package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JourneyDetails {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3307/ticketing_system";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "root";

    public static void viewJourneyDetails(String origin, String destination) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "SELECT * FROM journey_details WHERE origin = ? AND destination = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, origin);
                stmt.setString(2, destination);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String estimatedTime = rs.getString("estimated_time");
                        System.out.println("Estimated time for journey from " + origin + " to " + destination + ": " + estimatedTime);
                    } else {
                        System.out.println("No journey details found for specified origin and destination.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving journey details: " + e.getMessage());
        }
    }
}
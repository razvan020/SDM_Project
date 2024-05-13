package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class JourneyDetailsGUI extends JFrame {

    private JComboBox<String> originComboBox;
    private JComboBox<String> destinationComboBox;
    private JLabel timeLabel;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3307/ticketing_system";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "root";

    public JourneyDetailsGUI() {
        setTitle("Journey Details");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel originLabel = new JLabel("Select Origin:");
        originComboBox = new JComboBox<>();
        loadLocations(originComboBox, true);

        JLabel destinationLabel = new JLabel("Select Destination:");
        destinationComboBox = new JComboBox<>();
        loadLocations(destinationComboBox, false);

        JLabel timeTextLabel = new JLabel("Estimated Time:");
        timeLabel = new JLabel();

        JButton calculateButton = new JButton("Calculate Estimated Time");
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateEstimatedTime();
            }
        });

        panel.add(originLabel);
        panel.add(originComboBox);
        panel.add(destinationLabel);
        panel.add(destinationComboBox);
        panel.add(timeTextLabel);
        panel.add(timeLabel);
        panel.add(new JLabel()); // Empty label for spacing
        panel.add(calculateButton);

        add(panel);
        setVisible(true);
    }

    private void loadLocations(JComboBox<String> comboBox, boolean loadOrigins) {
        String columnName = loadOrigins ? "origin" : "destination";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "SELECT DISTINCT " + columnName + " FROM journey_details";
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    String location = rs.getString(columnName);
                    comboBox.addItem(location);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load locations: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calculateEstimatedTime() {
        String origin = (String) originComboBox.getSelectedItem();
        String destination = (String) destinationComboBox.getSelectedItem();

        if (origin == null || destination == null) {
            JOptionPane.showMessageDialog(this, "Please select both origin and destination.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "SELECT estimated_time FROM journey_details WHERE origin = ? AND destination = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, origin);
                stmt.setString(2, destination);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int estimatedTime = rs.getInt("estimated_time");
                    timeLabel.setText(estimatedTime + " minutes");
                } else {
                    JOptionPane.showMessageDialog(this, "Journey details not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to calculate estimated time: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(JourneyDetailsGUI::new);
    }
}

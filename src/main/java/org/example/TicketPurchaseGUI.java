package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TicketPurchaseGUI extends JFrame {

    private JComboBox<String> typeComboBox;
    private JComboBox<String> transportComboBox;
    private JLabel priceLabel;
    private JButton purchaseButton;

    private static final String[] ticketTypes = {"90 minutes", "Full day", "Monthly"};
    private static final String[] transportOptions = {"Bus", "Train", "Trolley"};

    private static final String JDBC_URL = "jdbc:mysql://localhost:3307/ticketing_system";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "root";

    public TicketPurchaseGUI() {
        setTitle("Ticket Purchase");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel typeLabel = new JLabel("Ticket Type:");
        typeComboBox = new JComboBox<>(ticketTypes);
        typeComboBox.addActionListener(e -> updatePriceLabel());

        JLabel transportLabel = new JLabel("Mode of Transport:");
        transportComboBox = new JComboBox<>(transportOptions);

        JLabel priceTextLabel = new JLabel("Price:");
        priceLabel = new JLabel();
        updatePriceLabel(); // Initialize price label with default value

        purchaseButton = new JButton("Purchase Ticket");
        purchaseButton.addActionListener(e -> {
            purchaseButton.setEnabled(false); // Disable button during purchase process
            purchaseTicket();
        });

        panel.add(typeLabel);
        panel.add(typeComboBox);
        panel.add(transportLabel);
        panel.add(transportComboBox);
        panel.add(priceTextLabel);
        panel.add(priceLabel);
        panel.add(new JLabel()); // Empty label for spacing
        panel.add(purchaseButton);

        add(panel);
        setVisible(true);
    }

    private void updatePriceLabel() {
        String selectedType = (String) typeComboBox.getSelectedItem();
        String selectedTransport = (String) transportComboBox.getSelectedItem();
        double price = getPrice(selectedType, selectedTransport);
        priceLabel.setText(String.valueOf(price));
    }

    private double getPrice(String type, String transport) {
        switch (type) {
            case "90 minutes":
                return transport.equals("Train") ? 3.5 : 3.0;
            case "Full day":
                return transport.equals("Train") ? 18.5 : 12.0;
            case "Monthly":
                return transport.equals("Train") ? 120.0 : 80.0;
            default:
                return 0.0; // Default price if type is not recognized
        }
    }

    private void purchaseTicket() {
        SwingWorker<Void, Void> purchaseWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Simulate purchase process
                Thread.sleep(2000); // Simulate a delay of 2 seconds

                // Perform ticket purchase logic
                String selectedType = (String) typeComboBox.getSelectedItem();
                String selectedTransport = (String) transportComboBox.getSelectedItem();
                double price = getPrice(selectedType, selectedTransport);

                try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                    String sql = "INSERT INTO tickets (type, mode_of_transport, price) VALUES (?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, selectedType);
                        stmt.setString(2, selectedTransport);
                        stmt.setDouble(3, price);
                        stmt.executeUpdate();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(TicketPurchaseGUI.this, "Failed to purchase ticket: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                return null;
            }

            @Override
            protected void done() {
                purchaseButton.setEnabled(true); // Enable button after purchase process is complete
                JOptionPane.showMessageDialog(TicketPurchaseGUI.this, "Ticket purchased successfully!");
            }
        };
        purchaseWorker.execute(); // Start the SwingWorker
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicketPurchaseGUI::new);
    }
}

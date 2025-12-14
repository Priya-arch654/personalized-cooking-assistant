package com.project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PreferencesPanel extends JPanel {

    private String backgroundImagePath = "C:\\Users\\hp\\Downloads\\Grilled Chicken with Roasted Potatoes and Salad.png";

    public PreferencesPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initialize();
    }

    private void initialize() {
        
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 180));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        formPanel.setOpaque(false);
        formPanel.setPreferredSize(new Dimension(400, 200));
        formPanel.setMaximumSize(new Dimension(400, 200));
        formPanel.setMinimumSize(new Dimension(400, 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Preferences of " + SmartMealPlannerAllInOne.loggedInUser, SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.BLACK);

        JLabel typeLabel = new JLabel("Preference Type:");
        String[] types = {"Diet", "Taste", "Allergy", "Cuisine", "Cooking Style"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setPreferredSize(new Dimension(250, 28));

        JLabel prefLabel = new JLabel("Preference:");
        JTextField prefField = new JTextField();
        prefField.setPreferredSize(new Dimension(250, 28));

        JButton saveBtn = UIUtils.colorfulButton("Save Preference", new Color(0, 177, 106));
        saveBtn.setPreferredSize(new Dimension(180, 30));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; formPanel.add(title, gbc);
        gbc.gridwidth = 1; gbc.gridy++;
        gbc.gridx = 0; formPanel.add(typeLabel, gbc);
        gbc.gridx = 1; formPanel.add(typeCombo, gbc);
        gbc.gridx = 0; gbc.gridy++; formPanel.add(prefLabel, gbc);
        gbc.gridx = 1; formPanel.add(prefField, gbc);
        gbc.gridx = 1; gbc.gridy++; formPanel.add(saveBtn, gbc);

        JTextArea prefList = new JTextArea();
        prefList.setEditable(false);
        prefList.setOpaque(false);
        prefList.setForeground(Color.BLACK);
        prefList.setFont(new Font("Monospaced", Font.BOLD, 16));
        JScrollPane scroll = new JScrollPane(prefList);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createTitledBorder("Saved Preferences"));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(formPanel, BorderLayout.NORTH); 
        centerPanel.add(scroll, BorderLayout.CENTER);   
        add(centerPanel, BorderLayout.CENTER);

        List<String> preferences = new java.util.ArrayList<>();
        loadUserPreferences(preferences, prefList);

        saveBtn.addActionListener((ActionEvent e) -> {
            String type = (String) typeCombo.getSelectedItem();
            String pref = prefField.getText().trim();
            if (!pref.isEmpty()) {
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement pst = con.prepareStatement(
                             "INSERT INTO user_preferences(username, pref_type, pref_value) VALUES (?, ?, ?)")) {
                    pst.setString(1, SmartMealPlannerAllInOne.loggedInUser);
                    pst.setString(2, type);
                    pst.setString(3, pref);
                    pst.executeUpdate();
                    preferences.add(type + ": " + pref);
                    prefList.setText(String.join("\n", preferences));
                    prefField.setText("");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(PreferencesPanel.this, "Error saving preference: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(PreferencesPanel.this, "Please enter a preference.");
            }
        });
    }

    private void loadUserPreferences(List<String> preferences, JTextArea prefList) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(
                     "SELECT pref_type, pref_value FROM user_preferences WHERE username = ? ORDER BY id DESC")) {
            pst.setString(1, SmartMealPlannerAllInOne.loggedInUser);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String prefStr = rs.getString("pref_type") + ": " + rs.getString("pref_value");
                preferences.add(prefStr);
            }
            prefList.setText(String.join("\n", preferences));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading preferences: " + ex.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImagePath != null && !backgroundImagePath.isEmpty()) {
            ImageIcon bgIcon = new ImageIcon(backgroundImagePath);
            g.drawImage(bgIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }
}

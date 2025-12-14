package com.project;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private SmartMealPlannerAllInOne app;

    private String backgroundImagePath = "C:\\Users\\hp\\Downloads\\Grilled Chicken with Roasted Potatoes and Salad.png";
    private final String DB_URL = "jdbc:mysql://localhost:3306/cookingassistant";
    private final String DB_USER = "cookingassistant";
    private final String DB_PASS = "root";

    public RegisterPanel(CardLayout cardLayout, JPanel mainPanel, SmartMealPlannerAllInOne app) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.app = app;
        setLayout(new GridBagLayout());
        initialize();
    }

    private void initialize() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 180)); // semi-transparent white
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panel.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Register New User");
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(Color.BLACK);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(title, c);
        c.gridwidth = 1;

        c.gridy++;
        panel.add(new JLabel("Username:"), c);
        usernameField = new JTextField(14);
        c.gridx = 1;
        panel.add(usernameField, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(new JLabel("Password:"), c);
        passwordField = new JPasswordField(14);
        c.gridx = 1;
        panel.add(passwordField, c);

        JButton regBtn = UIUtils.colorfulButton("Register", Color.ORANGE);
        JButton backBtn = UIUtils.colorfulButton("Back", Color.GREEN);

        c.gridy++;
        c.gridx = 0;
        panel.add(regBtn, c);
        c.gridx = 1;
        panel.add(backBtn, c);

        add(panel);

        regBtn.addActionListener(e -> registerUser());
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Login"));
    }

    private void registerUser() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT * FROM users WHERE username=?");
            checkStmt.setString(1, user);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Username already exists!");
                return;
            }

            PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO users(username, password, role) VALUES(?, ?, 'user')");
            insertStmt.setString(1, user);
            insertStmt.setString(2, pass);
            insertStmt.executeUpdate();

            app.getUserData(user, true);

            JOptionPane.showMessageDialog(this, "User registered! You can now login.");
            cardLayout.show(mainPanel, "Login");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error!", "Error", JOptionPane.ERROR_MESSAGE);
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

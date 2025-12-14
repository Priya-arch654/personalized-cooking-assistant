package com.project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private SmartMealPlannerAllInOne app;

    private String backgroundImagePath = "C:\\Users\\hp\\Downloads\\Grilled Chicken with Roasted Potatoes and Salad.png";

 
    private final String DB_URL = "jdbc:mysql://localhost:3306/cookingassistant";
    private final String DB_USER = "cookingassistant";
    private final String DB_PASS = "root";

    public LoginPanel(CardLayout cardLayout, JPanel mainPanel, SmartMealPlannerAllInOne app) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.app = app;
        setLayout(new GridBagLayout()); // center the form
        initialize();
    }

    private void initialize() {
     
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 180));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panel.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Smart Meal Planner");
        title.setFont(new Font("Serif", Font.BOLD, 28));
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

        JButton loginBtn = UIUtils.colorfulButton("Login", Color.ORANGE);
        JButton regBtn = UIUtils.colorfulButton("Register", Color.GREEN);
        c.gridy++;
        c.gridx = 0;
        panel.add(loginBtn, c);
        c.gridx = 1;
        panel.add(regBtn, c);

        add(panel);

        loginBtn.addActionListener(e -> login());
        regBtn.addActionListener(e -> cardLayout.show(mainPanel, "Register"));
    }

    private void login() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, user);
            pst.setString(2, pass);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                SmartMealPlannerAllInOne.loggedInUser = user;
                SmartMealPlannerAllInOne.loggedInRole = role;

                if (role.equals("admin")) {
                    cardLayout.show(mainPanel, "AdminPanel");
                } else {
           
                    boolean isNewUser = !app.userDataMap.containsKey(user);
                    SmartMealPlannerAllInOne.UserData data = app.getUserData(user, isNewUser);
                    JTabbedPane mainTabs = app.createMainTabs(data);
                    app.mainPanel.add(mainTabs, "Main");
                    app.cardLayout.show(app.mainPanel, "Main");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
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
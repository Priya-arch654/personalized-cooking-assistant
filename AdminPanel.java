package com.project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private SmartMealPlannerAllInOne app;
    private String backgroundImagePath = "C:\\Users\\hp\\Downloads\\Grilled Chicken with Roasted Potatoes and Salad.png";

    private final String DB_URL = "jdbc:mysql://localhost:3306/cookingassistant";
    private final String DB_USER = "cookingassistant";
    private final String DB_PASS = "root";

    public AdminPanel(CardLayout cardLayout, JPanel mainPanel, SmartMealPlannerAllInOne app) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.app = app;
        setLayout(new GridBagLayout());
        initialize();
    }

    private void initialize() {
        JPanel buttonPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 180));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        buttonPanel.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 50, 20, 50); 
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;

        JButton viewUsersBtn = UIUtils.colorfulButton("View Users", new Color(33, 150, 243)); 
        JButton mealListsBtn = UIUtils.colorfulButton("Meal Lists", new Color(255, 87, 34)); 
        JButton viewRatingsBtn = UIUtils.colorfulButton("View Ratings", new Color(255, 193, 7)); // yellow
        JButton logoutBtn = UIUtils.colorfulButton("Logout", new Color(244, 67, 54)); 

        Font buttonFont = new Font("Arial", Font.BOLD, 24);
        Dimension buttonSize = new Dimension(300, 80);

        JButton[] buttons = {viewUsersBtn, mealListsBtn, viewRatingsBtn, logoutBtn};
        for (JButton btn : buttons) {
            btn.setFont(buttonFont);
            btn.setPreferredSize(buttonSize);
            buttonPanel.add(btn, c);
            c.gridy++;
        }

        add(buttonPanel);


        viewUsersBtn.addActionListener(e -> viewUsers());
        mealListsBtn.addActionListener(e -> manageMealLists());
        viewRatingsBtn.addActionListener(e -> viewRatings());
        logoutBtn.addActionListener(e -> {
            SmartMealPlannerAllInOne.loggedInRole = "user";
            SmartMealPlannerAllInOne.loggedInUser = "";
            cardLayout.show(mainPanel, "Login");
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImagePath != null && !backgroundImagePath.isEmpty()) {
            ImageIcon bgIcon = new ImageIcon(backgroundImagePath);
            g.drawImage(bgIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }

    
    private void viewUsers() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT username, role FROM users");

            DefaultTableModel model = new DefaultTableModel(new String[]{"Username", "Role"}, 0);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("username"), rs.getString("role")});
            }

            JTable table = new JTable(model);
            JScrollPane scroll = new JScrollPane(table);
            JOptionPane.showMessageDialog(this, scroll, "All Users", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void manageMealLists() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            DefaultTableModel model = new DefaultTableModel(new String[]{"Meal ID", "Meal Name", "Category"}, 0);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, meal_name, category FROM meal_list");
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("meal_name"), rs.getString("category")});
            }

            JTable table = new JTable(model);
            JScrollPane scroll = new JScrollPane(table);

            int option = JOptionPane.showOptionDialog(this, scroll, "Manage Meal List",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, new String[]{"Add", "Edit", "Delete", "Close"}, "Close");

            switch (option) {
            case 0: // Add
                String mealName = JOptionPane.showInputDialog(this, "Enter Meal Name:");
                String category = JOptionPane.showInputDialog(this, "Enter Category:");
                if (mealName != null && category != null) {
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO meal_list(meal_name, category) VALUES(?,?)"
                    );
                    ps.setString(1, mealName);
                    ps.setString(2, category);
                    ps.executeUpdate();

                    PreparedStatement ps2 = conn.prepareStatement(
                        "INSERT INTO user_meal_list(username, meal_name, category) " +
                        "SELECT username, ?, ? FROM users"
                    );
                    ps2.setString(1, mealName);
                    ps2.setString(2, category);
                    ps2.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Meal added for all users!");
                }
                break;
            case 1: // Edit
                int editRow = table.getSelectedRow();
                if (editRow >= 0) {
                    int id = (int) table.getValueAt(editRow, 0);
                    String newName = JOptionPane.showInputDialog(this, "Enter new Meal Name:", table.getValueAt(editRow, 1));
                    String newCat = JOptionPane.showInputDialog(this, "Enter new Category:", table.getValueAt(editRow, 2));
                    if (newName != null && newCat != null) {
                        PreparedStatement ps = conn.prepareStatement(
                            "UPDATE meal_list SET meal_name=?, category=? WHERE id=?"
                        );
                        ps.setString(1, newName);
                        ps.setString(2, newCat);
                        ps.setInt(3, id);
                        ps.executeUpdate();

                        PreparedStatement ps2 = conn.prepareStatement(
                            "UPDATE user_meal_list SET meal_name=?, category=? " +
                            "WHERE meal_name=?"
                        );
                        ps2.setString(1, newName);
                        ps2.setString(2, newCat);
                        ps2.setString(3, table.getValueAt(editRow, 1).toString());
                        ps2.executeUpdate();

                        JOptionPane.showMessageDialog(this, "Meal updated for all users!");
                    }
                }
                break;
            case 2:
                int delRow = table.getSelectedRow();
                if (delRow >= 0) {
                    int id = (int) table.getValueAt(delRow, 0);
                    String mealNameToDelete = table.getValueAt(delRow, 1).toString();

                    PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM meal_list WHERE id=?"
                    );
                    ps.setInt(1, id);
                    ps.executeUpdate();

                    PreparedStatement ps2 = conn.prepareStatement(
                        "DELETE FROM user_meal_list WHERE meal_name=?"
                    );
                    ps2.setString(1, mealNameToDelete);
                    ps2.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Meal deleted from all users!");
                }
                break;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewRatings() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT username, rating, timestamp FROM ratings");

            DefaultTableModel model = new DefaultTableModel(new String[]{"Username", "Rating", "Date/Time"}, 0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("username"),
                        rs.getInt("rating") + " ⭐",
                        rs.getTimestamp("timestamp")
                });
            }

            JTable table = new JTable(model);
            JScrollPane scroll = new JScrollPane(table);
            JOptionPane.showMessageDialog(this, scroll, "User Ratings", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


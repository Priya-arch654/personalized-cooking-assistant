package com.project;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.*;
import java.util.List;

public class SmartMealPlannerAllInOne extends JFrame {
    public static String loggedInUser = "";
    public static String loggedInRole = "user";
    public static java.util.List<String> defaultBreakfastList = new java.util.ArrayList<>(java.util.Arrays.asList(
    	    "Idli", "Dosa", "Upma", "Poha", "Oats Bowl", "Sandwich", "Paratha", "Pongal", "Bread Omelette"
    	));
    	public static java.util.List<String> defaultLunchList = new java.util.ArrayList<>(java.util.Arrays.asList(
    	    "Veg Fried Rice", "Curd Rice", "Tomato Rice", "Chapati & Sabji",
    	    "Veg Biryani", "Sambar Rice", "Pulao", "Paneer Rice", "Mixed Veg Curry with Rice"
    	));
    	public static java.util.List<String> defaultDinnerList = new java.util.ArrayList<>(java.util.Arrays.asList(
    	    "Chapati & Curry", "Paneer Curry", "Veg Noodles", "Roti & Dhal",
    	    "Pasta", "Soup & Sandwich", "Fried Rice", "Upma", "Masala Dosa with Chutney"
    	));
    	public static List<UserMealPlan> allMealPlans = new ArrayList<>();
    	public SavedPlansPanel  savedPlansPanel;
    	public java.util.Map<String, UserData> userDataMap = new java.util.HashMap<>();
    public static class UserData {
        public List<String> breakfastList = new ArrayList<>();
        public List<String> lunchList = new ArrayList<>();
        public List<String> dinnerList = new ArrayList<>();
        public List<String> savedPlans = new ArrayList<>();
    }
    
    public static class UserMealPlan {
        public String username;
        public String plan;

        public UserMealPlan(String username, String plan) {
            this.username = username;
            this.plan = plan;
        }
    }
    public void saveUserData(String username) {
        if (username == null || username.isEmpty()) return;
        UserData data = getUserData(username, false);
        userDataMap.put(username, data);
        System.out.println("Saved data for user: " + username);
    }
    public UserData getUserData(String username, boolean isNewUser) {
        if (isNewUser) {
            UserData data = new UserData();
            data.breakfastList.addAll(defaultBreakfastList);
            data.lunchList.addAll(defaultLunchList);
            data.dinnerList.addAll(defaultDinnerList);
            userDataMap.put(username, data);
            return data;
        }
        return userDataMap.getOrDefault(username, new UserData());
    }

    public CardLayout cardLayout;
    public JPanel mainPanel;

    public SmartMealPlannerAllInOne() {
        setTitle("PERSONALIZED COOKING ASSISTANCE AND MEAL PLANNER");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 760);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setOpaque(false);
        LoginPanel loginPanel = new LoginPanel(cardLayout, mainPanel, this);
        RegisterPanel registerPanel = new RegisterPanel(cardLayout, mainPanel, this);
        AdminPanel adminPanel = new AdminPanel(cardLayout, mainPanel, this);
        LogoutPanel logoutPanel = new LogoutPanel(cardLayout, mainPanel, this);
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(registerPanel, "Register");
        mainPanel.add(adminPanel, "AdminPanel");

        setContentPane(mainPanel);
        cardLayout.show(mainPanel, "Login");
    }
    public JTabbedPane createMainTabs(UserData data) {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabs.setOpaque(false);

        tabs.addTab("Recipe Generator", new RecipeGeneratorPanel());
        tabs.addTab("Manage Pantry", new ManagePantryPanel(loggedInUser));
        tabs.addTab("Meal Lists", new MealListsPanel(data, this));

        tabs.addTab("Preferences", new PreferencesPanel());

        savedPlansPanel = new SavedPlansPanel(data);
        AutomaticPlannerPanel autoPlannerPanel = new AutomaticPlannerPanel(data, savedPlansPanel);

        tabs.addTab("Automatic Meal Planner", autoPlannerPanel);
        tabs.addTab("Saved Plans", savedPlansPanel);

        tabs.addTab("AI Chat", new AIChatPanel());
        tabs.addTab("Logout", new LogoutPanel(cardLayout, mainPanel, this));

        return tabs;
    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        try (Connection con = DBConnection.getConnection()) {
            if (con != null && !con.isClosed()) {
                System.out.println("Database connected successfully!");
            }
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
        SwingUtilities.invokeLater(() -> {
            SmartMealPlannerAllInOne app = new SmartMealPlannerAllInOne();
            app.setVisible(true);
        });
    }
}

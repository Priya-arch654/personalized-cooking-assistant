package com.project;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MealListsPanel extends JPanel {

    private SmartMealPlannerAllInOne.UserData data;
    private SmartMealPlannerAllInOne app;
    private Image backgroundImage;

    public MealListsPanel(SmartMealPlannerAllInOne.UserData data,
                          SmartMealPlannerAllInOne app) {

        this.data = data;
        this.app = app;

        backgroundImage = new ImageIcon(
                "C:\\Users\\hp\\Downloads\\Grilled Chicken with Roasted Potatoes and Salad.png"
        ).getImage();

        setLayout(new BorderLayout());
        setOpaque(false);

        initialize();
    }

    private void initialize() {

        // ✅ Load from meal_list table (NOT user based now)
        List<String> breakfastList = loadMeals("breakfast");
        List<String> lunchList = loadMeals("lunch");
        List<String> dinnerList = loadMeals("dinner");

        JPanel listsPanel = new JPanel(new GridLayout(1, 3, 10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 180));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        listsPanel.setOpaque(false);

        JTextArea breakfastArea = createReadOnlyArea(breakfastList);
        JTextArea lunchArea = createReadOnlyArea(lunchList);
        JTextArea dinnerArea = createReadOnlyArea(dinnerList);

        listsPanel.add(wrap("Breakfast", breakfastArea));
        listsPanel.add(wrap("Lunch", lunchArea));
        listsPanel.add(wrap("Dinner", dinnerArea));

        add(listsPanel, BorderLayout.CENTER);
    }

    private JTextArea createReadOnlyArea(List<String> list) {
        JTextArea area = new JTextArea(String.join("\n", list));
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        area.setEditable(false);
        area.setOpaque(false);
        return area;
    }

    private JScrollPane wrap(String title, JTextArea area) {
        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(BorderFactory.createTitledBorder(title));
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        return sp;
    }

    private List<String> loadMeals(String category) {
        List<String> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT meal_name FROM meal_list WHERE category=?"
            );
            ps.setString(1, category);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("meal_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

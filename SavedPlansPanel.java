package com.project;

import javax.swing.*;
import java.awt.*;

public class SavedPlansPanel extends JPanel {

    private SmartMealPlannerAllInOne.UserData data;
    private JTextArea plansArea;

    // Background image path
    private String backgroundImagePath = "C:\\Users\\hp\\Downloads\\Grilled Chicken with Roasted Potatoes and Salad.png";

    public SavedPlansPanel(SmartMealPlannerAllInOne.UserData data) {
        this.data = data;
        setLayout(new BorderLayout());
        setOpaque(false);
        initialize();
    }

    private void initialize() {
        // Title
        JLabel title = new JLabel("Saved Meal Plans", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(Color.BLACK);
        add(title, BorderLayout.NORTH);

        // Text area for plans
        plansArea = new JTextArea();
        plansArea.setFont(new Font("Monospaced", Font.BOLD, 16));
        plansArea.setEditable(false);
        plansArea.setOpaque(false);
        plansArea.setForeground(Color.BLACK);

        JScrollPane scroll = new JScrollPane(plansArea);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // Refresh button
        JButton refreshBtn = UIUtils.colorfulButton("Refresh", new Color(76, 175, 80));
        refreshBtn.addActionListener(e -> refreshPlans());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 180));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        bottom.setOpaque(false);
        bottom.add(refreshBtn);
        add(bottom, BorderLayout.SOUTH);

        refreshPlans();
    }

    // Make this public so AutomaticPlannerPanel can call it
    public void refreshPlans() {
        StringBuilder sb = new StringBuilder();
        int idx = 1;
        if (data != null && data.savedPlans != null) {
            for (String plan : data.savedPlans) {
                sb.append("Plan ").append(idx++).append(":\n")
                  .append(plan).append("\n------\n");
            }
        }
        plansArea.setText(sb.toString());
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

package com.project;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ManagePlansAdminPanel extends JPanel {

    private JTextArea planArea;

    public ManagePlansAdminPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 500));

        JLabel title = new JLabel("All Users' Meal Plans", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        planArea = new JTextArea();
        planArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        planArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(planArea);
        add(scroll, BorderLayout.CENTER);

        // Load all users' plans
        loadAllUserPlans();

        // Optional: Clear All Plans button
        JButton clearBtn = UIUtils.colorfulButton("Clear All Plans", new Color(244, 67, 54));
        clearBtn.setFont(new Font("Arial", Font.BOLD, 20));
        clearBtn.addActionListener(e -> {
            SmartMealPlannerAllInOne.allMealPlans.clear();
            planArea.setText("");
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(clearBtn);
        add(bottom, BorderLayout.SOUTH);
    }
    
    

    private void loadAllUserPlans() {
        List<SmartMealPlannerAllInOne.UserMealPlan> plans = SmartMealPlannerAllInOne.allMealPlans;

        if (plans.isEmpty()) {
            planArea.setText("No meal plans available.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (SmartMealPlannerAllInOne.UserMealPlan plan : plans) {
            sb.append(plan.username).append(": ").append(plan.plan).append("\n");
        }
        planArea.setText(sb.toString());
    }

}

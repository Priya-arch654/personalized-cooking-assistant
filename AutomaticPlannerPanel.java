package com.project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class AutomaticPlannerPanel extends JPanel {
    private JTextField startDateField, endDateField;
    private JTextArea planArea;
    private SmartMealPlannerAllInOne.UserData data;
    private SavedPlansPanel savedPlanPanel;
    private String backgroundImagePath = "C:\\Users\\hp\\Downloads\\Grilled Chicken with Roasted Potatoes and Salad.png";

    public AutomaticPlannerPanel(SmartMealPlannerAllInOne.UserData data, SavedPlansPanel savedPlanPanel) {
        this.data = data;
        this.savedPlanPanel = savedPlanPanel;
        setLayout(new GridBagLayout());
        initialize();
    }

    private void initialize() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 180));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        formPanel.setOpaque(false);

        c.gridx = 0;
        c.gridy = 0;
        formPanel.add(new JLabel("Start Date (YYYY-MM-DD):"), c);
        startDateField = new JTextField(12);
        startDateField.setText(LocalDate.now().toString());
        c.gridx = 1;
        formPanel.add(startDateField, c);

        c.gridx = 0;
        c.gridy++;
        formPanel.add(new JLabel("End Date (YYYY-MM-DD):"), c);
        endDateField = new JTextField(12);
        endDateField.setText(LocalDate.now().plusDays(4).toString());
        c.gridx = 1;
        formPanel.add(endDateField, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        JButton generateBtn = UIUtils.colorfulButton("Generate Meal Plan", new Color(255, 87, 34));
        formPanel.add(generateBtn, c);
        c.gridwidth = 1;

        c.gridy++;
        JButton saveBtn = UIUtils.colorfulButton("Save Plan", new Color(76, 175, 80));
        formPanel.add(saveBtn, c);

        GridBagConstraints formC = new GridBagConstraints();
        formC.gridx = 0;
        formC.gridy = 0;
        formC.weightx = 1;
        formC.anchor = GridBagConstraints.NORTH;
        add(formPanel, formC);

        planArea = new JTextArea(20, 40);
        planArea.setEditable(false);
        planArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        planArea.setForeground(Color.BLACK);
        planArea.setOpaque(false);

        JScrollPane scroll = new JScrollPane(planArea);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        GridBagConstraints scrollC = new GridBagConstraints();
        scrollC.gridx = 0;
        scrollC.gridy = 1;
        scrollC.weightx = 1;
        scrollC.weighty = 1;
        scrollC.fill = GridBagConstraints.BOTH;
        scrollC.insets = new Insets(10, 10, 10, 10);
        add(scroll, scrollC);

        final List<String[]> lastGeneratedRows = new ArrayList<>();

        generateBtn.addActionListener((ActionEvent e) -> generatePlan(lastGeneratedRows));
        saveBtn.addActionListener(e -> {
            if (lastGeneratedRows.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No plan generated to save!");
                return;
            }
            for (String[] row : lastGeneratedRows) {
                data.savedPlans.add(row[0] + " → " + row[1] + ", " + row[2] + ", " + row[3]);
            }
            JOptionPane.showMessageDialog(this, "Meal plan saved!");
            if (savedPlanPanel != null) {
                savedPlanPanel.refreshPlans();
            }
        });
    }

    private void generatePlan(List<String[]> lastGeneratedRows) {
        try {
            LocalDate start = LocalDate.parse(startDateField.getText().trim());
            LocalDate end = LocalDate.parse(endDateField.getText().trim());
            if (end.isBefore(start)) {
                JOptionPane.showMessageDialog(this, "End date must be after or equal to start date!");
                return;
            }

            StringBuilder plan = new StringBuilder();
            lastGeneratedRows.clear();
            LocalDate current = start;

            while (!current.isAfter(end)) {
                String[] meals = generateUniqueMealsForDay();
                plan.append(current).append("\n")
                    .append("  Breakfast : ").append(meals[0]).append("\n")
                    .append("  Lunch     : ").append(meals[1]).append("\n")
                    .append("  Dinner    : ").append(meals[2]).append("\n\n");

                lastGeneratedRows.add(new String[]{current.toString(), meals[0], meals[1], meals[2]});
                current = current.plusDays(1);
            }

            planArea.setText(plan.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Use YYYY-MM-DD");
        }
    }

    private String[] generateUniqueMealsForDay() {
        Random rnd = new Random();
        String breakfast = data.breakfastList.get(rnd.nextInt(data.breakfastList.size()));
        String lunch = data.lunchList.get(rnd.nextInt(data.lunchList.size()));
        String dinner = data.dinnerList.get(rnd.nextInt(data.dinnerList.size()));

        int attempts = 0;
        while ((lunch.equals(breakfast) || lunch.equals(dinner)) && attempts < 20) {
            lunch = data.lunchList.get(rnd.nextInt(data.lunchList.size()));
            attempts++;
        }

        attempts = 0;
        while ((dinner.equals(breakfast) || dinner.equals(lunch)) && attempts < 20) {
            dinner = data.dinnerList.get(rnd.nextInt(data.dinnerList.size()));
            attempts++;
        }

        return new String[]{breakfast, lunch, dinner};
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

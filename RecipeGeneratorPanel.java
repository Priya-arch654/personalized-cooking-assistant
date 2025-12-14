package com.project;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RecipeGeneratorPanel extends JPanel {

    private JTextField cuisineField, heightField, weightField, caloriesField, fatField;
    private JTextArea ingredientsArea;
    private JTable outputTable;
    private DefaultTableModel tableModel;

    private String backgroundImagePath =
            "C:\\Users\\hp\\Downloads\\Grilled Chicken with Roasted Potatoes and Salad.png";

    public RecipeGeneratorPanel() {
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

        GridBagConstraints fc = new GridBagConstraints();
        fc.insets = new Insets(8, 8, 8, 8);
        fc.fill = GridBagConstraints.HORIZONTAL;

        fc.gridx = 0; fc.gridy = 0;
        formPanel.add(new JLabel("Cuisine:"), fc);
        cuisineField = new JTextField(20);
        styleField(cuisineField);
        fc.gridx = 1;
        formPanel.add(cuisineField, fc);

        fc.gridx = 0; fc.gridy++;
        formPanel.add(new JLabel("Height (cm):"), fc);
        heightField = new JTextField(20);
        styleField(heightField);
        fc.gridx = 1;
        formPanel.add(heightField, fc);

        fc.gridx = 0; fc.gridy++;
        formPanel.add(new JLabel("Weight (kg):"), fc);
        weightField = new JTextField(20);
        styleField(weightField);
        fc.gridx = 1;
        formPanel.add(weightField, fc);

        fc.gridx = 0; fc.gridy++;
        formPanel.add(new JLabel("Calories:"), fc);
        caloriesField = new JTextField(20);
        styleField(caloriesField);
        fc.gridx = 1;
        formPanel.add(caloriesField, fc);

        // Fat
        fc.gridx = 0; fc.gridy++;
        formPanel.add(new JLabel("Fat Limit (g):"), fc);
        fatField = new JTextField(20);
        styleField(fatField);
        fc.gridx = 1;
        formPanel.add(fatField, fc);

        fc.gridx = 0; fc.gridy++;
        formPanel.add(new JLabel("Ingredients:"), fc);
        ingredientsArea = new JTextArea(6, 20);
        ingredientsArea.setLineWrap(true);
        ingredientsArea.setWrapStyleWord(true);
        ingredientsArea.setOpaque(false);
        ingredientsArea.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));

        JScrollPane ingScroll = new JScrollPane(ingredientsArea);
        ingScroll.setOpaque(false);
        ingScroll.getViewport().setOpaque(false);
        ingScroll.setBorder(BorderFactory.createEmptyBorder());

        fc.gridx = 1;
        formPanel.add(ingScroll, fc);

        JButton generateBtn = new JButton("Generate Recipes");
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setBackground(new Color(34, 139, 34));
        generateBtn.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 0), 2));

        fc.gridx = 0; fc.gridy++; fc.gridwidth = 2;
        formPanel.add(generateBtn, fc);

        c.gridx = 0; c.gridy = 0;
        add(formPanel, c);

        String[] columns = {"Recommended Recipes"};
        tableModel = new DefaultTableModel(columns, 0);

        outputTable = new JTable(tableModel);
        outputTable.setRowHeight(55); // LINE SPACING
        outputTable.setShowGrid(false);
        outputTable.setOpaque(false);
        outputTable.setBackground(new Color(0, 0, 0, 0));
        outputTable.setFillsViewportHeight(true);

        outputTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setFont(new Font("Segoe UI", Font.BOLD, 20)); // BIG FONT
                label.setForeground(new Color(139, 0, 0)); // DARK RED
                label.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                label.setOpaque(false);

                return label;
            }
        });

        JScrollPane scroll = new JScrollPane(outputTable);
        scroll.setPreferredSize(new Dimension(650, 320));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        c.gridx = 0; c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(scroll, c);
        generateBtn.addActionListener(e -> generateRecipesAction());
    }

    private void styleField(JTextField field) {
        field.setOpaque(false);
        field.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
    }

    private void generateRecipesAction() {

        tableModel.setRowCount(0);

        String cuisine = cuisineField.getText().trim();
        String height = heightField.getText().trim();
        String weight = weightField.getText().trim();
        String calories = caloriesField.getText().trim();
        String fat = fatField.getText().trim();
        String ingredients = ingredientsArea.getText().trim();

        List<String> recipes =
                UIUtils.generateRecipes(cuisine, height, weight, calories, fat, ingredients);

        if (recipes.isEmpty()) {
            tableModel.addRow(new Object[]{"No suitable recipes found. Try different inputs."});
        } else {
            for (String recipe : recipes) {
                tableModel.addRow(new Object[]{recipe});
            }
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImagePath != null && !backgroundImagePath.isEmpty()) {
            ImageIcon bg = new ImageIcon(backgroundImagePath);
            g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }
}

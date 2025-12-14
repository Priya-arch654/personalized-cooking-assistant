package com.project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class ManagePantryPanel extends JPanel {
    private JTextField nameField, qtyField, catField;
    private JTable table;
    private DefaultTableModel model;
    private String username; 

    private static Map<String, java.util.List<Ingredient>> userIngredients = new HashMap<>();

    private String backgroundImagePath = "C:\\Users\\hp\\Downloads\\Grilled Chicken with Roasted Potatoes and Salad.png";
    private BufferedImage blurredBackground;

    public ManagePantryPanel(String username) {
        this.username = username;
        setLayout(new GridBagLayout());
        prepareBlurredBackground(backgroundImagePath);
        initialize();
        loadUserIngredients(); 
    }

    private void prepareBlurredBackground(String path){
        if(path == null || path.isEmpty()) return;

        try {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage();
            BufferedImage original = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = original.createGraphics();
            g2.drawImage(img, 0, 0, null);
            g2.dispose();
            float[] matrix = new float[25];
            Arrays.fill(matrix, 1f/25f); 
            ConvolveOp op = new ConvolveOp(new Kernel(5, 5, matrix), ConvolveOp.EDGE_NO_OP, null);
            BufferedImage temp = original;
            for(int i=0;i<3;i++){
                temp = op.filter(temp, null);
            }

            blurredBackground = temp;

        } catch(Exception e){
            e.printStackTrace();
        }
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

        c.gridx = 0; c.gridy = 0;
        formPanel.add(new JLabel("Ingredient:"), c);
        nameField = new JTextField(10);
        nameField.setOpaque(false);
        nameField.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.BLACK));
        c.gridx = 1;
        formPanel.add(nameField, c);

        c.gridx = 0; c.gridy++;
        formPanel.add(new JLabel("Quantity:"), c);
        qtyField = new JTextField(5);
        qtyField.setOpaque(false);
        qtyField.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.BLACK));
        c.gridx = 1;
        formPanel.add(qtyField, c);

        c.gridx = 0; c.gridy++;
        formPanel.add(new JLabel("Category:"), c);
        catField = new JTextField(10);
        catField.setOpaque(false);
        catField.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.BLACK));
        c.gridx = 1;
        formPanel.add(catField, c);

        c.gridx = 0; c.gridy++; c.gridwidth = 2;
        JButton addBtn = new JButton("Add Item");
        addBtn.setForeground(Color.WHITE);
        addBtn.setBackground(new Color(34, 139, 34));
        addBtn.setOpaque(true);
        addBtn.setBorder(BorderFactory.createLineBorder(new Color(0,100,0),2));
        formPanel.add(addBtn, c);

        c.gridy++;
        JButton clearBtn = new JButton("Clear Fields");
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setBackground(new Color(220, 20, 60));
        clearBtn.setOpaque(true);
        clearBtn.setBorder(BorderFactory.createLineBorder(new Color(139,0,0),2));
        formPanel.add(clearBtn, c);

        GridBagConstraints formC = new GridBagConstraints();
        formC.gridx = 0; formC.gridy = 0;
        formC.insets = new Insets(10,10,10,10);
        formC.anchor = GridBagConstraints.NORTH;
        add(formPanel, formC);

        String[] columns = {"Name","Quantity","Category"};
        model = new DefaultTableModel(columns,0);
        table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer,int row,int column){
                Component comp = super.prepareRenderer(renderer,row,column);
                comp.setForeground(new Color(139,0,0)); // dark red
                comp.setFont(new Font("Arial",Font.BOLD,18));
                return comp;
            }
        };
        table.setOpaque(false);
        table.setShowGrid(false);
        table.setFillsViewportHeight(true);
        table.setBackground(new Color(0,0,0,0));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        GridBagConstraints tableC = new GridBagConstraints();
        tableC.gridx=0; tableC.gridy=1;
        tableC.weightx=1; tableC.weighty=1;
        tableC.fill=GridBagConstraints.BOTH;
        tableC.insets = new Insets(10,10,10,10);
        add(scroll, tableC);

        addBtn.addActionListener(e -> addItem());
        clearBtn.addActionListener(e -> clearFields());
    }

    private void addItem() {
        String name = nameField.getText().trim();
        String qty = qtyField.getText().trim();
        String cat = catField.getText().trim();

        if (name.isEmpty() || qty.isEmpty() || cat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Ingredient ing = new Ingredient(name, qty, cat);
        userIngredients.computeIfAbsent(username, k -> new ArrayList<>()).add(ing);

        model.addRow(new Object[]{name, qty, cat});
        model.addRow(new Object[]{"", "", ""});

        int lastRow = table.getRowCount() - 2; // actual item row
        table.scrollRectToVisible(table.getCellRect(lastRow, 0, true));
        table.setRowSelectionInterval(lastRow, lastRow);

        clearFields();

        JOptionPane.showMessageDialog(this, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearFields(){
        nameField.setText("");
        qtyField.setText("");
        catField.setText("");
    }

    private void loadUserIngredients(){
        java.util.List<Ingredient> list = userIngredients.get(username);
        if(list!=null){
            for(Ingredient ing:list){
                model.addRow(new Object[]{ing.name,ing.qty,ing.category});
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if(blurredBackground != null){
            g.drawImage(blurredBackground, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(245,245,245));
            g.fillRect(0,0,getWidth(),getHeight());
        }
    }

    private static class Ingredient{
        String name,qty,category;
        public Ingredient(String name,String qty,String category){
            this.name=name;
            this.qty=qty;
            this.category=category;
        }
    }
}


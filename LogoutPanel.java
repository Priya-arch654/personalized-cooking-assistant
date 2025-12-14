package com.project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class LogoutPanel extends JPanel {
    private Image backgroundImage;

    // MySQL connection
    private final String DB_URL = "jdbc:mysql://localhost:3306/cookingassistant";
    private final String DB_USER = "cookingassistant";
    private final String DB_PASS = "root";

    public LogoutPanel(CardLayout cardLayout, JPanel mainPanel, SmartMealPlannerAllInOne app) {
        String path = "C:\\Users\\hp\\Downloads\\Grilled Chicken with Roasted Potatoes and Salad.png";
        backgroundImage = new ImageIcon(path).getImage();

        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20,20,20,20);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Overlay panel
        JPanel overlay = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255,255,255,180));
                g.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
            }
        };
        overlay.setOpaque(false);

        GridBagConstraints ogbc = new GridBagConstraints();
        ogbc.insets = new Insets(10,10,10,10);
        ogbc.gridx = 0;
        ogbc.gridy = 0;

        JLabel thankYou = new JLabel("Thank you for using the app!", SwingConstants.CENTER);
        thankYou.setFont(new Font("Arial", Font.BOLD, 28));
        overlay.add(thankYou, ogbc);

        ogbc.gridy++;
        JLabel rateLabel = new JLabel("Rate your experience:");
        rateLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        overlay.add(rateLabel, ogbc);

        // Stars as JLabel
        ogbc.gridy++;
        JPanel starsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        starsPanel.setOpaque(false);
        JLabel[] stars = new JLabel[5];

        for (int i=0; i<5; i++) {
            JLabel star = new JLabel("\u2606"); // empty star
            star.setFont(new Font("Arial", Font.BOLD, 36)); // larger font
            star.setForeground(Color.ORANGE);

            final int index = i;
            star.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    for(int j=0;j<5;j++){
                        stars[j].setText(j<=index ? "\u2605" : "\u2606"); // fill stars
                    }
                    saveRating(index+1);
                }
            });
            stars[i] = star;
            starsPanel.add(star);
        }
        overlay.add(starsPanel, ogbc);

        ogbc.gridy++;
        JButton loginBtn = new JButton("Go to Login");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 18));
        loginBtn.setBackground(new Color(34,139,34));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setOpaque(true);
        loginBtn.setBorder(BorderFactory.createLineBorder(new Color(0,100,0),2));
        overlay.add(loginBtn, ogbc);

        add(overlay, gbc);

        loginBtn.addActionListener(e -> {
            SmartMealPlannerAllInOne.loggedInUser = "";
            SmartMealPlannerAllInOne.loggedInRole = "user";
            cardLayout.show(mainPanel, "Login");
        });
    }

    private void saveRating(int rating){
        String username = SmartMealPlannerAllInOne.loggedInUser;
        if(username == null || username.isEmpty()) return;

        try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ratings(username,rating) VALUES(?,?)"
            );
            ps.setString(1, username);
            ps.setInt(2, rating);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thank you for your rating: " + rating + "⭐");
        } catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving rating!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if(backgroundImage!=null){
            g.drawImage(backgroundImage,0,0,getWidth(),getHeight(),this);
        }
    }
}

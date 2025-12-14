package com.project;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.List;

public class UIUtils {

    public static JButton colorfulButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 90), 1, true));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { b.setBackground(brighter(bg, 0.08f)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { b.setBackground(bg); }
        });
        return b;
    }

    public static Color brighter(Color c, float factor) {
        int r = Math.min(255, (int) (c.getRed() + 255 * factor));
        int g = Math.min(255, (int) (c.getGreen() + 255 * factor));
        int b = Math.min(255, (int) (c.getBlue() + 255 * factor));
        return new Color(r, g, b);
    }

    public static List<String> generateRecipes(
            String cuisine,
            String height,
            String weight,
            String calories,
            String fat,
            String ingredientsText
    ) {

        Map<String, List<String>> db = new HashMap<>();
        db.put("chicken", Arrays.asList("Chicken Curry", "Grilled Chicken", "Chicken Stir Fry", "Chicken Biryani", "Chicken Kebab"));
        db.put("rice", Arrays.asList("Veg Fried Rice", "Tomato Rice", "Curd Rice", "Jeera Rice", "Lemon Rice"));
        db.put("tomato", Arrays.asList("Tomato Rice", "Tomato Soup", "Tomato Chutney", "Tomato Pasta", "Tomato Salad"));
        db.put("paneer", Arrays.asList("Paneer Butter Masala", "Paneer Tikka", "Paneer Bhurji", "Shahi Paneer", "Paneer Makhani"));
        db.put("egg", Arrays.asList("Egg Omelette", "Egg Curry", "Boiled Eggs", "Egg Bhurji", "Egg Sandwich"));
        db.put("milk", Arrays.asList("Milk Shake", "Kheer", "Paneer (home-made)", "Rice Pudding"));
        db.put("bread", Arrays.asList("Bread Sandwich", "French Toast", "Bread Upma", "Garlic Bread"));
        db.put("vegetables", Arrays.asList("Mixed Veg Sabji", "Vegetable Stir Fry", "Veg Manchurian", "Veg Curry"));
        db.put("dal", Arrays.asList("Dal Fry", "Dal Tadka", "Sambar", "Dal Makhani"));
        db.put("pasta", Arrays.asList("Pasta Alfredo", "Tomato Pasta", "Pasta Primavera", "Macaroni Cheese"));
        db.put("noodles", Arrays.asList("Veg Noodles", "Hakka Noodles", "Chow Mein", "Singapore Noodles"));
        db.put("potato", Arrays.asList("Aloo Fry", "Mashed Potato", "Aloo Curry", "Aloo Paratha", "Potato Wedges"));
        db.put("onion", Arrays.asList("Onion Pakoda", "Curry Base", "Onion Rings", "Caramelized Onions"));
        db.put("carrot", Arrays.asList("Carrot Poriyal", "Carrot Soup", "Carrot Salad", "Carrot Halwa"));
        db.put("beans", Arrays.asList("Beans Poriyal", "Beans Curry", "Stir Fried Beans"));
        db.put("spinach", Arrays.asList("Palak Paneer", "Palak Soup", "Spinach Salad"));
        db.put("banana", Arrays.asList("Banana Smoothie", "Banana Pancake", "Banana Bread"));
        db.put("apple", Arrays.asList("Apple Pie", "Apple Crumble", "Apple Smoothie"));
        db.put("fish", Arrays.asList("Grilled Fish", "Fish Curry", "Fish Fry"));
        db.put("mushroom", Arrays.asList("Mushroom Curry", "Stuffed Mushrooms", "Mushroom Soup"));
        db.put("chili", Arrays.asList("Chili Paneer", "Spicy Veg Stir Fry", "Chili Chicken"));
        db.put("cabbage", Arrays.asList("Cabbage Sabji", "Cabbage Soup", "Cabbage Salad"));
        db.put("corn", Arrays.asList("Corn Salad", "Corn Soup", "Corn Stir Fry"));
        db.put("yogurt", Arrays.asList("Raita", "Yogurt Smoothie", "Curd Rice"));
        db.put("beetroot", Arrays.asList("Beetroot Salad", "Beetroot Soup", "Beetroot Poriyal"));
        db.put("chickpeas", Arrays.asList("Chole", "Chickpea Salad", "Chana Masala"));
        db.put("bell pepper", Arrays.asList("Capsicum Curry", "Stuffed Bell Peppers", "Bell Pepper Stir Fry"));

        Map<String, List<String>> cuisineExtras = new HashMap<>();
        cuisineExtras.put("chinese", Arrays.asList("Manchurian", "Hakka Noodles", "Veg Spring Rolls"));
        cuisineExtras.put("italian", Arrays.asList("Margherita Pizza", "Pasta Alfredo", "Bruschetta"));
        cuisineExtras.put("south indian", Arrays.asList("Idli", "Dosa", "Sambar Rice", "Pongal"));
        Map<String, Integer> score = new HashMap<>();
        if (ingredientsText != null && !ingredientsText.isEmpty()) {
            String[] tokens = ingredientsText.toLowerCase().split("[,\\s]+");
            for (String token : tokens) {
                token = token.trim();
                if (token.isEmpty()) continue;
                for (Map.Entry<String, List<String>> e : db.entrySet()) {
                    if (token.contains(e.getKey()) || e.getKey().contains(token)) {
                        for (String rec : e.getValue()) {
                            score.put(rec, score.getOrDefault(rec, 0) + 2);
                        }
                    }
                }
            }
        }

        if (cuisine != null && !cuisine.isEmpty()) {
            List<String> cuisineRecs = cuisineExtras.get(cuisine.toLowerCase());
            if (cuisineRecs != null) {
                for (String rec : cuisineRecs) {
                    score.put(rec, score.getOrDefault(rec, 0) + 1);
                }
            }
        }

        try {
            int calLimit = (calories != null && !calories.isEmpty()) ? Integer.parseInt(calories) : -1;
            int fatLimit = (fat != null && !fat.isEmpty()) ? Integer.parseInt(fat) : -1;

            for (String rec : score.keySet()) {
                if (rec.toLowerCase().contains("salad") || rec.toLowerCase().contains("curd") || rec.toLowerCase().contains("steamed")) {
                    if (calLimit > 0) score.put(rec, score.get(rec) + 1);
                    if (fatLimit > 0) score.put(rec, score.get(rec) + 1);
                }
                if (rec.toLowerCase().contains("curry") || rec.toLowerCase().contains("fried") || rec.toLowerCase().contains("pizza")) {
                    if (calLimit > 0) score.put(rec, score.get(rec) - 1);
                    if (fatLimit > 0) score.put(rec, score.get(rec) - 1);
                }
            }
        } catch (NumberFormatException ignored) {}
        try {
            if (height != null && weight != null && !height.isEmpty() && !weight.isEmpty()) {
                double h = Double.parseDouble(height) / 100.0;
                double w = Double.parseDouble(weight);
                double bmi = w / (h * h);

                for (String rec : score.keySet()) {
                    if (bmi >= 25) {
                        if (rec.toLowerCase().contains("fried") || rec.toLowerCase().contains("curry"))
                            score.put(rec, score.get(rec) - 2);
                        else
                            score.put(rec, score.get(rec) + 1);
                    } else if (bmi < 18.5) { 
                        if (rec.toLowerCase().contains("curry") || rec.toLowerCase().contains("rice"))
                            score.put(rec, score.get(rec) + 2);
                    }
                }
            }
        } catch (NumberFormatException ignored) {}
        List<Map.Entry<String, Integer>> list = new ArrayList<>(score.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        List<String> suggestions = new ArrayList<>();
        for (Map.Entry<String, Integer> e : list) {
            suggestions.add(e.getKey());
            if (suggestions.size() >= 15) break; 
        }

        return new ArrayList<>(new LinkedHashSet<>(suggestions));
    }
}
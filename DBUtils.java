package com.project;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {
    public static List<String> getMeals(String mealType) {
        List<String> meals = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT meal_name FROM meals WHERE meal_type = ?")) {
            ps.setString(1, mealType);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                meals.add(rs.getString("meal_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return meals;
    }
}




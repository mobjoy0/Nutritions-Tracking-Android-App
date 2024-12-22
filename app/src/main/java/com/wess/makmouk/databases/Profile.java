package com.wess.makmouk.databases;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "profile_table")
public class Profile {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private int age;
    private String gender;
    private double height;
    private double current_weight;
    private double goal_weight;
    private double activityIndex;
    private double BMI;
    private String Birthday;

    public Profile(String name, int age, float height, float current_weight, String gender, double activityIndex) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.current_weight = current_weight;
        this.gender = gender;
        this.activityIndex = activityIndex;
        calcBMI();
    }

    public Profile() {
        this.name = "";
        this.age = 0;
        this.height = 0.0f;
        this.current_weight = 0.0f;
        this.gender = "";
        this.goal_weight = 100.0f;
        this.activityIndex = 0.0d;
    }

    // BMI Calculation
    public void calcBMI() {
        BMI = current_weight / ((height * height) / 10000);
        BMI = Double.parseDouble(String.format("%.2f", BMI));
        setBMI(BMI);
    }

    // Getters and setters for all fields

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday; calcAge();
    }

    public double getActivityIndex() {
        return activityIndex;
    }

    public void setActivityIndex(double activityIndex) {
        this.activityIndex = activityIndex;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; calcBMI(); }
    public double getCurrent_weight() { return current_weight; }
    public void setCurrent_weight(double current_weight) { this.current_weight = current_weight; calcBMI(); }
    public double getGoal_weight() { return goal_weight; }
    public void setGoal_weight(double goal_weight) { this.goal_weight = goal_weight; }
    public double getBMI() { return BMI; }

    public void setBMI(double BMI) {
        this.BMI = BMI;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", height=" + height +
                ", current_weight=" + current_weight +
                ", goal_weight=" + goal_weight +
                ", BMI=" + BMI +
                '}';
    }

    public void calcAge() {
        if (Birthday == null || Birthday.isEmpty()) {
            return; // Return early if no birthday is set
        }

        try {
            // Parse the birthday string (in format YYYY/MM/DD)
            String[] parts = Birthday.split("/");

            int birthYear = Integer.parseInt(parts[0]);
            int birthMonth = Integer.parseInt(parts[1]) - 1; // Month is 0-based in Calendar
            int birthDay = Integer.parseInt(parts[2]);

            // Get the current date
            Calendar currentCalendar = Calendar.getInstance();
            int currentYear = currentCalendar.get(Calendar.YEAR);
            int currentMonth = currentCalendar.get(Calendar.MONTH);
            int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);

            // Calculate age
            int age = currentYear - birthYear;

            // If the birthday hasn't occurred yet this year, subtract 1 from the age
            if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
                age--;
            }

            // Set the calculated age to the class variable
            this.age = age;

        } catch (Exception e) {
            e.printStackTrace();
            // Handle invalid date format or any parsing errors here
        }
    }

    public double getTDEE(){
        double returnValue = 0;
        if (gender.equals("Male")) {
            returnValue = (66 + (13.7 * current_weight) + (5 * height) - (6.8 * age)) * activityIndex;
        } else {
            returnValue = (655 + (9.6 * current_weight) + (1.8 * height) - (4.7 * age)) * activityIndex;
        }

        if (goal_weight - current_weight > 2) {
            return  returnValue + 350;
        } else if (goal_weight - current_weight < -2) {
            return  returnValue - 500;
        }
        return returnValue;
    }

}
package com.wess.makmouk.databases;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_table")
public class Food {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String barCode;
    private String name;
    private int calories;
    private float protein;
    private float carbs, fats;
    private String unit;
    private int amount;

    // Constructor
    public Food(String name, int calories, float protein, String unit, int amount, float carbs, float fats) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.unit = unit;
        this.amount = amount;
        this.carbs = carbs;
        this.fats = fats;
        this.barCode = "";
    }

    public Food(int id, String name, int amount){
        this.id = id;
        this.name = name;
        this.amount = amount;
    }

    public Food() {}

    // Getters and Setters

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    // Getters and Setters for carbs and fats
    public float getCarbs() {
        return carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }

    public float getFats() {
        return fats;
    }

    public void setFats(float fats) {
        this.fats = fats;
    }



    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", barCode='" + barCode + '\'' +
                ", name='" + name + '\'' +
                ", calories=" + calories +
                ", protein=" + protein +
                ", carbs=" + carbs +
                ", fats=" + fats +
                ", unit='" + unit + '\'' +
                ", amount=" + amount +
                '}';
    }
}

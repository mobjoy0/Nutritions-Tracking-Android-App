package com.wess.makmouk.databases;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "food_consumption_table",
        foreignKeys = {
                @ForeignKey(
                        entity = Profile.class,
                        parentColumns = "id",
                        childColumns = "profile_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Food.class,
                        parentColumns = "id",
                        childColumns = "food_id",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class FoodConsumption {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "profile_id")
    private int profileId;

    @ColumnInfo(name = "food_id")
    private int foodId;

    @ColumnInfo(name = "food_name")
    private String foodName;

    private int quantity;

    @ColumnInfo(name = "total_calories")
    private int totalCalories;

    @ColumnInfo(name = "total_protein")
    private double totalProtein;

    private String date;

    // Constructor
    public FoodConsumption(int id, int profileId, String foodName, int foodId, int quantity, String date) {
        this.id = id;
        this.profileId = profileId;
        this.foodName = foodName;
        this.foodId = foodId;
        this.quantity = quantity;
        this.date = date;
    }

    public FoodConsumption(int profileId, String foodName, int foodId, int quantity, String date) {
        this.profileId = profileId;
        this.foodName = foodName;
        this.foodId = foodId;
        this.quantity = quantity;
        this.date = date;
    }

    // Default constructor
    public FoodConsumption() {}

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    public double getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(int totalProtein) {
        this.totalProtein = totalProtein;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void addCaloriesNdProtein(int calories, int protein, int quantity){
        this.totalCalories += calories;
        this.totalProtein += protein;
        this.quantity += quantity;
    }



    @Override
    public String toString() {
        return "FoodConsumption{" +
                "id=" + id +
                ", profileId=" + profileId +
                ", foodId=" + foodId +
                ", foodName='" + foodName + '\'' +
                ", quantity=" + quantity +
                ", totalCalories=" + totalCalories +
                ", totalProtein=" + totalProtein +
                ", date='" + date + '\'' +
                '}';
    }
}

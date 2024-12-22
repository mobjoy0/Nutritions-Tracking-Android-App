package com.wess.makmouk.databases;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "foodpref_table")

public class FoodPreferences {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int profileId;
    private int foodId;
    private int quantity;

    public FoodPreferences(int profileId, int foodId, int quantity){
        this.profileId = profileId;
        this.foodId = foodId;
        this.quantity = quantity;
    }

    public FoodPreferences() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }
}

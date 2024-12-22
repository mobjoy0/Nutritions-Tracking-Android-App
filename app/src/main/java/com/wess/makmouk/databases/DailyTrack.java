package com.wess.makmouk.databases;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import org.jetbrains.annotations.NotNull;

@Entity(
        tableName = "daily_track_table",
        foreignKeys = @ForeignKey(
                entity = Profile.class,
                parentColumns = "id",
                childColumns = "profileId",
                onDelete = ForeignKey.CASCADE
        ),
        primaryKeys = {"profileId", "date"}
)
public class DailyTrack {

    @NonNull
    private int profileId; // Foreign key
    @NotNull
    private String date;   // Format: YYYY-MM-DD
    private int caloriesConsumed;
    private float protein;
    private float weight;
    private int carbs, fats;

    // Constructors
    public DailyTrack(int profileId, String date, int caloriesConsumed, float protein, float weight, int carbs, int fats) {
        this.profileId = profileId;
        this.date = date;
        this.caloriesConsumed = caloriesConsumed;
        this.protein = protein;
        this.weight = weight;
        this.carbs = carbs;
        this.fats = fats;
    }

    public DailyTrack() {}

    // Getters and Setters
    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCaloriesConsumed() {
        return caloriesConsumed;
    }

    public void setCaloriesConsumed(int caloriesConsumed) {
        this.caloriesConsumed = caloriesConsumed;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    // Getters and Setters for carbs and fats
    public int getCarbs() {
        return carbs;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public int getFats() {
        return fats;
    }

    public void setFats(int fats) {
        this.fats = fats;
    }

    @Override
    public String toString() {
        return "DailyTrack{" +
                "profileId=" + profileId +
                ", date='" + date + '\'' +
                ", caloriesConsumed=" + caloriesConsumed +
                ", protein=" + protein +
                ", weight=" + weight +
                ", carbs=" + carbs +
                ", fats=" + fats +
                '}';
    }
}

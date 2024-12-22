package com.wess.makmouk.databases;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Upsert;

import java.util.List;

@Dao
public interface FoodPrefDao {

    // Insert a new FoodPreference into the database
    @Insert
    void insert(FoodPreferences foodPreferences);

    // Insert multiple FoodPreferences into the database
    @Insert
    void insertAll(List<FoodPreferences> foodPreferencesList);

    @Upsert
    void upsert(FoodPreferences foodPreferences);

    @Query("SELECT * FROM foodpref_table WHERE profileId = :profileId AND foodId = :foodId")
    FoodPreferences getFavFoodByIds(int profileId, int foodId);

    // Update a FoodPreference in the database
    @Update
    void update(FoodPreferences foodPreferences);

    // Delete a FoodPreference from the database
    @Delete
    void delete(FoodPreferences foodPreferences);

    // Query all food preferences for a specific profile
    @Query("SELECT * FROM foodpref_table WHERE profileId = :profileId")
    List<FoodPreferences> getFoodPreferencesByProfileId(int profileId);

    // Get a specific FoodPreference by its id
    @Query("SELECT * FROM foodpref_table WHERE id = :id")
    FoodPreferences getFoodPreferencesById(int id);

    // Delete all preferences for a given profile
    @Query("DELETE FROM foodpref_table WHERE profileId = :profileId AND foodId = :foodId")
    void deleteFoodPreferencesByProfileId(int profileId, int foodId);

}

package com.wess.makmouk.databases;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProfileDao {
    // Profile operations
    @Insert
    void insertProfile(Profile profile);


    @Query("SELECT * FROM profile_table WHERE id = :id")
    LiveData<Profile> getLiveProfileById(int id);

    @Query("SELECT * FROM profile_table WHERE id = :id")
    Profile getProfileById(int id);

    // Task operations
    @Query("SELECT * FROM profile_table ORDER BY id")
    LiveData<List<Profile>> getAllProfiles();

    @Update
    void updateProfile(Profile profile);

    @Delete
    void deleteProfile(Profile profile);

    @Query("DELETE FROM profile_table")
    void DeleteAllProfiles();

    @Query("SELECT MAX(id) FROM profile_table")
    Integer getLastInsertedProfileId();

    @Query("UPDATE profile_table SET name = :name, age = :age, gender = :gender, height = :height, current_weight = :currentWeight, goal_weight = :goalWeight WHERE id = :id")
    void updateProfileById(int id, String name, int age, String gender, float height, float currentWeight, float goalWeight);

    @Query("SELECT id FROM profile_table")
    List<Integer> getAllProfileIds();

    @Query("SELECT current_weight FROM profile_table WHERE id = :id")
    float getLastdayWeight(int id);
}



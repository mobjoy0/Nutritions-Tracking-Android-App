package com.wess.makmouk.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface Daily_TrackDao {

    @Insert
    void insertDailyTrack(DailyTrack dailyTrack);

    @Query("SELECT * FROM daily_track_table WHERE profileId = :profileId ORDER BY date DESC")
    LiveData<List<DailyTrack>> getAllDailyTracks(int profileId);


    @Query("SELECT * FROM daily_track_table WHERE profileId = :profileId AND date = :date LIMIT 1")
    LiveData<DailyTrack> getDailyTrackByDate(int profileId, String date);


    @Update
    void updateDailyTrack(DailyTrack dailyTrack);

    @Delete
    void deleteDailyTrack(DailyTrack dailyTrack);

    @Query("SELECT * FROM daily_track_table WHERE profileId = :profileId AND date >= date('now', '-6 days') ORDER BY date DESC")
    List<DailyTrack> getLast7DaysRecords(int profileId);

    @Query("UPDATE daily_track_table SET caloriesConsumed = caloriesConsumed + :AddCalories, protein = protein + :AddProtein, carbs = carbs + :AddCarbs, fats = fats + :AddFats WHERE profileId = :profileId AND date = CURRENT_DATE")
    void AddCaloriesOrProtein(int profileId, int AddCalories, double AddProtein, int AddCarbs, int AddFats);


    @Query("SELECT * FROM daily_track_table WHERE profileId = :profileId AND date = :date LIMIT 1")
    DailyTrack get_1_DailyTrack(int profileId, String date);

    @Query("SELECT * FROM daily_track_table WHERE profileId = :profileId AND date >= date('now', '-27 days') ORDER BY date DESC")
    List<DailyTrack> get_30_DailyTracks(int profileId);

    @Query("SELECT * FROM daily_track_table WHERE profileId = :profileId AND date == date('now')")
    DailyTrack get_TodaysTrack(int profileId);






}

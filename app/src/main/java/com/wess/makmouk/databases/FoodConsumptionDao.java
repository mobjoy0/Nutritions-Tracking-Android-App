package com.wess.makmouk.databases;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.List;

@Dao
public interface FoodConsumptionDao {

    @Upsert
    void upsert(FoodConsumption foodConsumption);

    @Insert
    void insert(FoodConsumption foodConsumption);

    @Query("UPDATE food_consumption_table SET quantity = quantity + :quantity WHERE profile_id = :id")
    void addQtFood(int id, int quantity);

    @Query("SELECT * FROM food_consumption_table WHERE profile_id = :profileId AND food_id = :foodId")
    FoodConsumption getConsumedFoodById(int profileId, int foodId);



    @Query("SELECT * FROM food_consumption_table WHERE profile_id = :profileId")
    List<FoodConsumption> getConsumptionsByProfileId(int profileId);

    @Query("SELECT * FROM food_consumption_table WHERE date = :date")
    List<FoodConsumption> getConsumptionsByDate(String date);

    @Query("SELECT * FROM food_consumption_table WHERE profile_id = :profileId AND date = :date")
    List<FoodConsumption> getConsumptionsByProfileIdAndDate(int profileId, String date);

    @Query("SELECT * FROM food_consumption_table WHERE profile_id = :profileId AND date = date('now')")
    List<FoodConsumption> getConsumedFood_Today(int profileId);

    @Query("DELETE FROM food_consumption_table WHERE date != date('now')")
    void clearData();


}


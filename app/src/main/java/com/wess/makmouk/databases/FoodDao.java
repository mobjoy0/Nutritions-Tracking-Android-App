package com.wess.makmouk.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface FoodDao {

    @Insert
    void insertFood(Food food);

    @Query("SELECT * FROM food_table WHERE name = :name LIMIT 1")
    Food getFoodByName(String name);

    @Query("SELECT * FROM food_table")
    List<Food> getAllFoods();

    @Query("SELECT DISTINCT(name) FROM food_table")
    List<String> getAllFoodsNames();

    @Update
    void updateFood(Food food);

    @Delete
    void deleteFood(Food food);
    @Query("SELECT * FROM food_table WHERE id = :id LIMIT 1")
    Food getFoodById(int id);

    @Insert
    void insertAllFoods(List<Food> foods);

    @Query("SELECT COUNT(*) FROM food_table")
    int getFoodItemsCount();


}

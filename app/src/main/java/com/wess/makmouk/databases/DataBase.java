package com.wess.makmouk.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {Profile.class, DailyTrack.class, Food.class,
        FoodConsumption.class, FoodPreferences.class}, version = 50)
public abstract class DataBase extends RoomDatabase {

    public static volatile DataBase instance;

    public abstract Daily_TrackDao daily_TrackDao();
    public  abstract ProfileDao profileDao();
    public abstract FoodDao foodDao();
    public abstract FoodConsumptionDao foodConsumptionDao();
    public abstract FoodPrefDao foodPrefDao();

    public static DataBase getInstance(Context context) {
        if (instance == null) {
            synchronized (DataBase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    DataBase.class, "DataBase")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}

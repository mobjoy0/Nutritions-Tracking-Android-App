package com.wess.makmouk.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.wess.makmouk.R;
import com.wess.makmouk.databases.DailyTrack;
import com.wess.makmouk.databases.Daily_TrackDao;
import com.wess.makmouk.databases.DataBase;
import com.wess.makmouk.databases.FoodDao;
import com.wess.makmouk.databases.ProfileDao;
import com.wess.makmouk.others.CSVImport;
import com.wess.makmouk.preferencesmanager.PreferencesManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadingScreenActivity extends AppCompatActivity {

    private ProfileDao profileDao;
    private FoodDao foodDao;
    private DataBase dataBase;
    private Daily_TrackDao dailyTrackDao;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static int animationTime = 2000;

    public static void setAnimationTime(int time) {
        animationTime = time;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_loadingscreen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.cherry_red));
        }

        // Initialize database and check conditions
        dataBase = DataBase.getInstance(getApplicationContext());
        profileDao = dataBase.profileDao();
        foodDao = dataBase.foodDao();
        dailyTrackDao = dataBase.daily_TrackDao();

        Check_FoodDatabase();
        Check_SelectedProfile(PreferencesManager.getLastProfileId(this));




        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LoadingScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, animationTime);
    }


    private void Check_SelectedProfile(int lastProfileId) {
        if (lastProfileId == 0 || lastProfileId == -1) {
            Intent intent = new Intent(LoadingScreenActivity.this, CreateProfileActivity.class);
            startActivity(intent);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        executorService.submit(() -> {
            if (dailyTrackDao.get_TodaysTrack(lastProfileId) == null){

                dailyTrackDao.insertDailyTrack(new DailyTrack(lastProfileId, currentDate, 0,
                        0f, profileDao.getLastdayWeight(lastProfileId), 0, 0));
                Log.d("DailyTrackWorker", "Created DailyTrack for Profile ID: " + lastProfileId);

            } else {
                Log.d("DailyTrackWorker", "DailyTrack already exists for Profile ID: " + lastProfileId);
            }

        });

    }

    private void Check_FoodDatabase() {
        executorService.submit(() -> {
            if (foodDao.getFoodItemsCount() == 0) {
                CSVImport csvImport = new CSVImport(foodDao);
                csvImport.loadFoodDataFromCSV(this);
            }
        });
    }

}

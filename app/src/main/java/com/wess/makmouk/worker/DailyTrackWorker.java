package com.wess.makmouk.worker;

import com.wess.makmouk.databases.*;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.wess.makmouk.databases.DataBase;
import com.wess.makmouk.databases.Daily_TrackDao;
import com.wess.makmouk.databases.ProfileDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyTrackWorker extends Worker {

    public DailyTrackWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            DataBase db = DataBase.getInstance(getApplicationContext());
            Daily_TrackDao dailyTrackDao = db.daily_TrackDao();
            ProfileDao profileDao = db.profileDao();

            // Get all profiles
            List<Integer> profileIds = profileDao.getAllProfileIds();

            // Create a daily track for each profile
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = sdf.format(new Date());

            for (int profileId : profileIds) {
                if (dailyTrackDao.get_1_DailyTrack(profileId, currentDate) == null) {

                    dailyTrackDao.insertDailyTrack(new DailyTrack(profileId, currentDate, 0,
                            0f, profileDao.getLastdayWeight(profileId), 0, 0));
                    Log.d("DailyTrackWorker", "Created DailyTrack for Profile ID: " + profileId);
                } else {
                    Log.d("DailyTrackWorker", "DailyTrack already exists for Profile ID: " + profileId);
                }
            }

            return Result.success();
        } catch (Exception e) {
            Log.e("DailyTrackWorker", "Error creating daily tracks", e);
            return Result.failure();
        }
    }
}

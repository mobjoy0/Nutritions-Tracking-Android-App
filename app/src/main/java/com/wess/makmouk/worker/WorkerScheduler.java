package com.wess.makmouk.worker;

import android.content.Context;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class WorkerScheduler {
    public static void scheduleDailyTrackWorker(Context context) {
        // Calculate the delay until midnight
        Calendar current = Calendar.getInstance();
        Calendar nextMidnight = Calendar.getInstance();
        nextMidnight.set(Calendar.HOUR_OF_DAY, 0);
        nextMidnight.set(Calendar.MINUTE, 0);
        nextMidnight.set(Calendar.SECOND, 0);
        nextMidnight.set(Calendar.MILLISECOND, 0);
        nextMidnight.add(Calendar.DAY_OF_MONTH, 1); // Move to next day

        long delayInMillis = nextMidnight.getTimeInMillis() - current.getTimeInMillis();

        // Schedule the worker
        PeriodicWorkRequest dailyTrackWorkRequest = new PeriodicWorkRequest.Builder(
                DailyTrackWorker.class,
                1, TimeUnit.DAYS // Repeat every 24 hours
        ).setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS) // Start at midnight
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "DailyTrackWorker",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP, // Keep existing schedule
                dailyTrackWorkRequest
        );
    }
}

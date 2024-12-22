package com.wess.makmouk.preferencesmanager;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREFS_NAME = "com.wess.makmouk.ProfilePREFERENCES";
    private static final String LAST_PROFILE_ID_KEY = "last_profile_id";

    public static void saveLastProfileId(Context context, int profileId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LAST_PROFILE_ID_KEY, profileId);
        editor.apply();
    }

    public static int getLastProfileId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(LAST_PROFILE_ID_KEY, -1); // Default to -1 if no profile is saved
    }
}

package com.wess.makmouk.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wess.makmouk.adapters.*;
import com.wess.makmouk.activities.*;
import com.wess.makmouk.databases.*;
import com.wess.makmouk.fragments.*;
import com.wess.makmouk.preferencesmanager.*;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wess.makmouk.R;

import java.util.ArrayList;
import java.util.List;

public class ProfileSelectionActivity extends AppCompatActivity {

    private ProfileAdapter profileAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileselection);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // Use ContextCompat.getColor() for compatibility with older Android versions
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.cherry_red));
        }


        floatingActionButton = findViewById(R.id.actionbutton_addProfile);

        //create a new linearlayout for each profile
        recyclerView = findViewById(R.id.profiles_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //setup adapter and send data to it
        profileAdapter = new ProfileAdapter(new ArrayList<>());
        recyclerView.setAdapter(profileAdapter);


        getAllProfiles();


        floatingActionButton.setOnClickListener(view1 -> {


            Intent intent = new Intent(ProfileSelectionActivity.this, CreateProfileActivity.class);
            startActivity(intent);
            finish();

        });




        profileAdapter.setOnProfileClickListener(profile -> {
            Log.wtf("selection", "lastP ="+profile.getId());
            PreferencesManager.saveLastProfileId(ProfileSelectionActivity.this, profile.getId());

            LoadingScreenActivity.setAnimationTime(1000);
            Intent intent = new Intent(ProfileSelectionActivity.this, LoadingScreenActivity.class);
            startActivity(intent);
            finish();
        });




    }

    private void getAllProfiles() {
        DataBase db = DataBase.getInstance(getApplicationContext());
        ProfileDao profileDao = db.profileDao();

        LiveData<List<Profile>> profiles = profileDao.getAllProfiles();

        profiles.observe(this, profileList -> {
            if (profileList != null) {
                profileAdapter.updateProfiles(profileList);
            }
        });
    }

}



package com.wess.makmouk.activities;
import com.wess.makmouk.R;
import com.wess.makmouk.adapters.PagerAdapter;
import com.wess.makmouk.databinding.ActivityMainBinding;
import com.wess.makmouk.fragments.*;
import com.wess.makmouk.databases.*;
import com.wess.makmouk.preferencesmanager.*;
import com.wess.makmouk.worker.*;
import com.wess.makmouk.others.*;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    int currentFrag = 0; // To track the current fragment index
    ActivityMainBinding binding;
    private ViewPager2 viewPager;
    private ProfileDao profileDao;
    private FoodDao foodDao;
    private DataBase dataBase;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set status bar color for devices running Lollipop or later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.cherry_red));
        }
        overridePendingTransition(0, 0);



        // Setup ViewPager2
        viewPager = binding.frameLayout;
        setupViewPager(viewPager);

        // Sync ViewPager2 and BottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (item.getItemId() == R.id.nav_track) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                viewPager.setCurrentItem(2);
                return true;
            }
            return false;
        });


        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentFrag = position;
                switch (position) {
                    case 0:
                        binding.bottomNavigationView.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        binding.bottomNavigationView.setSelectedItemId(R.id.nav_track);
                        break;
                    case 2:
                        binding.bottomNavigationView.setSelectedItemId(R.id.nav_profile);
                        break;
                }
            }
        });

        // Restore selected fragment on configuration change
        if (savedInstanceState != null) {
            currentFrag = savedInstanceState.getInt("currentFrag", 0);
            viewPager.setCurrentItem(currentFrag);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentFrag", currentFrag);
    }

    private void setupViewPager(ViewPager2 viewPager) {
        PagerAdapter adapter = new PagerAdapter(this);
        viewPager.setAdapter(adapter);

    }


}

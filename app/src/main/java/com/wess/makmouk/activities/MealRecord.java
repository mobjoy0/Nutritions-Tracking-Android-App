package com.wess.makmouk.activities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wess.makmouk.adapters.FoodConsumptionAdapter;
import com.wess.makmouk.databases.DataBase;
import com.wess.makmouk.databases.FoodConsumption;
import com.wess.makmouk.databases.FoodConsumptionDao;
import com.wess.makmouk.preferencesmanager.PreferencesManager;
import com.wess.makmouk.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MealRecord extends AppCompatActivity {
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private RecyclerView recyclerView;
    private FoodConsumptionAdapter adapter;
    private List<FoodConsumption> Foods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mealrecord);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // Use ContextCompat.getColor() for compatibility with older Android versions
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.cherry_red));
        }

        Foods = new ArrayList<>(); // Initialize the list

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.mealRecordRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FoodConsumptionAdapter(getApplicationContext(), Foods);
        recyclerView.setAdapter(adapter);

        int ProfileId = PreferencesManager.getLastProfileId(this);
        Retrive_Data(ProfileId); // Load data
    }

    private void Retrive_Data(int id) {
        DataBase database = DataBase.getInstance(getApplicationContext());
        FoodConsumptionDao foodConsumptionDao = database.foodConsumptionDao();

        executorService.submit(() -> {
            // Fetch data from the database
            foodConsumptionDao.clearData();
            List<FoodConsumption> fetchedFoods = foodConsumptionDao.getConsumedFood_Today(id);

            // Update UI on the main thread
            runOnUiThread(() -> {
                if (fetchedFoods != null) {
                    Foods.clear();
                    Foods.addAll(fetchedFoods);
                    adapter.notifyDataSetChanged(); // Notify adapter of data changes
                } else {
                    Log.wtf("MealRecord", "No data available");
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        int ProfileId = PreferencesManager.getLastProfileId(this);
        Retrive_Data(ProfileId); // Refresh data on resume
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow(); // Gracefully stop background tasks
        }
    }
}

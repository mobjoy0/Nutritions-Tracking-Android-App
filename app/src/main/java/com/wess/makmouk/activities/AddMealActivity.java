package com.wess.makmouk.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wess.makmouk.R;
import com.wess.makmouk.adapters.FavFoodAdapter;
import com.wess.makmouk.adapters.FoodConsumptionAdapter;
import com.wess.makmouk.preferencesmanager.*;
import com.wess.makmouk.databases.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddMealActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextView;
    private EditText quantityEditText;
    private CardView addMealButton, addToFav;
    private List<Food> foodList = new ArrayList<>();  // Initialize as an empty list
    private Daily_TrackDao dailyTrackDao;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<String> foodNamesArray = new ArrayList<>();
    private FoodConsumption foodConsumption;
    private FoodConsumptionDao foodConsumptionDao;
    private FoodDao foodDao;
    private FoodPrefDao foodPrefDao;
    private RecyclerView recyclerView;
    private FavFoodAdapter adapter;
    private List<Food> Foods = new ArrayList<>();
    private List<FoodPreferences> FoodsIds = new ArrayList<>();
    private int profileId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmeal);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // Use ContextCompat.getColor() for compatibility with older Android versions
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.cherry_red));
        }
        profileId = PreferencesManager.getLastProfileId(this);
        // Initialize views
        autoCompleteTextView = findViewById(R.id.autoCompleteView);
        quantityEditText = findViewById(R.id.quantityEditText);
        addMealButton = findViewById(R.id.addMealButton);
        autoCompleteTextView.requestFocus();
        addToFav = findViewById(R.id.addToFav);

        // Initialize the database and DAO
        DataBase database = DataBase.getInstance(getApplicationContext());
        foodDao = database.foodDao();
        foodConsumptionDao = database.foodConsumptionDao();
        foodPrefDao = database.foodPrefDao();
        dailyTrackDao = database.daily_TrackDao();

        getFavFoods(profileId);




        //recylce
        recyclerView = findViewById(R.id.fav_foodRecycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavFoodAdapter(this, Foods);
        recyclerView.setAdapter(adapter);





        //insertFakeFoods();
        Fill_FoodSpinnerItems();




        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {

            String selectedFoodName = parent.getItemAtPosition(position).toString();
            if ( selectedFoodName.equals("Egg(large)") ||selectedFoodName.equals("Egg(small)")){
                quantityEditText.setHint("Enter quantity (pieces)");
            } else {
                quantityEditText.setHint("Enter quantity (grams)");
            }
            Toast.makeText(AddMealActivity.this, "Selected Item: " + selectedFoodName, Toast.LENGTH_SHORT).show();
        });







        // Set button listener to add meal
        addMealButton.setOnClickListener(v -> addMealToDailyTrack(profileId));
        addToFav.setOnClickListener(v -> addMealToFav(profileId));
    }

    private void Fill_FoodSpinnerItems(){

        executorService.submit(() -> {
            // Get the food items from the database
            foodNamesArray = foodDao.getAllFoodsNames();

            // Make sure to update the UI on the main thread
            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, foodNamesArray);
                autoCompleteTextView.setAdapter(adapter);

            });
        });


    }


    private void addMealToDailyTrack(int profileId) {
        String selectedFoodname = autoCompleteTextView.getText().toString();
        String quantityString = quantityEditText.getText().toString().trim();

        if (!CheckInput(selectedFoodname, quantityString)) return;

        double quantity = Double.parseDouble(quantityString);

        executorService.submit(() -> {
            Food selectedFood = foodDao.getFoodByName(selectedFoodname);

            if (selectedFood == null) {
                runOnUiThread(() -> Toast.makeText(this, "Food not found in database", Toast.LENGTH_SHORT).show());
                return;
            }

            double[] nutritionValues = getNutritionValues(selectedFood, quantity);
            double calories = nutritionValues[0];
            double proteins = nutritionValues[1];
            double carbs = nutritionValues[2];
            double fats = nutritionValues[3];


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = sdf.format(new Date());

            foodConsumptionDao.clearData();
            foodConsumption = foodConsumptionDao.getConsumedFoodById(profileId, selectedFood.getId());

            if (foodConsumption == null) {

                foodConsumption = new FoodConsumption(profileId, selectedFood.getName(),
                        selectedFood.getId(), (int) quantity, currentDate);

                foodConsumption.setTotalCalories((int) calories);
                foodConsumption.setTotalProtein( proteins);

            } else {
                foodConsumption.addCaloriesNdProtein((int) calories, (int) proteins, (int) quantity);
            }

            foodConsumptionDao.upsert(foodConsumption);

            try {
                Log.wtf("addmeal", "Adding to daily track...");
                dailyTrackDao.AddCaloriesOrProtein(profileId, (int) calories,proteins, (int) carbs, (int) fats);
                Log.wtf("addmeal", "Calories and protein added successfully to daily track");
            } catch (Exception e) {
                Log.wtf("addmeal", "Error adding to daily track", e);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "Meal added", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddMealActivity.this, MainActivity.class));
                finish();
            });
        });
    }



    private void addMealToFav(int profileId) {
        String selectedFoodname = autoCompleteTextView.getText().toString();
        String quantityString = quantityEditText.getText().toString().trim();


        if (!CheckInput(selectedFoodname, quantityString)) return;

        executorService.submit(() -> {
            Food selectedFood = foodDao.getFoodByName(selectedFoodname);
            if (selectedFood == null) {
                runOnUiThread(() -> Toast.makeText(this, "Food not found in database", Toast.LENGTH_SHORT).show());
                return;
            }

            FoodPreferences foodPreferences = foodPrefDao.getFavFoodByIds(profileId, selectedFood.getId());
            if (foodPreferences == null) {
                foodPreferences = new FoodPreferences(profileId, selectedFood.getId(), Integer.parseInt(quantityString));
                foodPrefDao.upsert(foodPreferences);
            } else {
                foodPreferences.setQuantity(Integer.parseInt(quantityString));
                foodPrefDao.update(foodPreferences);
            }

            runOnUiThread(() -> Toast.makeText(this, "Food added to favorites", Toast.LENGTH_SHORT).show());
        });
    }


    private double[] getNutritionValues(Food selectedFood, double quantity) {
        double calories, proteins, carbs, fats;

        if ("g".equals(selectedFood.getUnit()) || "ml".equals(selectedFood.getUnit())) {
            calories = selectedFood.getCalories() * (quantity / 100.0);
            proteins = selectedFood.getProtein() * (quantity / 100.0);
            carbs = selectedFood.getCarbs() * (quantity / 100.0);
            fats = selectedFood.getFats() * (quantity / 100.0);
        } else {
            calories = selectedFood.getCalories() * quantity;
            proteins = selectedFood.getProtein() * quantity;
            carbs = selectedFood.getCarbs() * quantity;
            fats = selectedFood.getFats() * quantity;
        }

        return new double[]{calories, proteins, carbs, fats};
    }


    private Boolean CheckInput(String selectedFoodname, String quantityString){

        if (foodNamesArray == null || foodNamesArray.isEmpty()) {
            Toast.makeText(this, "No food items available", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Fetch the selected food and quantity

        if (selectedFoodname.isEmpty()) {
            Toast.makeText(this, "Please select a food", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (quantityString.isEmpty()) {
            Toast.makeText(this, "Please enter a quantity", Toast.LENGTH_SHORT).show();
            return false;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(quantityString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid quantity", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getFavFoods(int profileId) {
        executorService.submit(() -> {
            FoodsIds = foodPrefDao.getFoodPreferencesByProfileId(profileId);

            // Resolve Food details for RecyclerView
            List<Food> resolvedFoods = new ArrayList<>();
            for (FoodPreferences fp : FoodsIds) {
                Food food = foodDao.getFoodById(fp.getFoodId());
                if (food != null) {
                    resolvedFoods.add(new Food(food.getId() ,food.getName(), fp.getQuantity()));
                }
            }

            // Update UI on the main thread
            runOnUiThread(() -> {
                Foods.clear();
                Foods.addAll(resolvedFoods);
                adapter.notifyDataSetChanged();
            });
        });
    }

}

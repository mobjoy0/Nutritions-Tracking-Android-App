package com.wess.makmouk.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wess.makmouk.R;
import com.wess.makmouk.activities.AddMealActivity;
import com.wess.makmouk.activities.MainActivity;
import com.wess.makmouk.databases.Daily_TrackDao;
import com.wess.makmouk.databases.DataBase;
import com.wess.makmouk.databases.Food;
import com.wess.makmouk.databases.FoodConsumption;
import com.wess.makmouk.databases.FoodConsumptionDao;
import com.wess.makmouk.databases.FoodDao;
import com.wess.makmouk.databases.FoodPrefDao;
import com.wess.makmouk.databases.FoodPreferences;
import com.wess.makmouk.preferencesmanager.PreferencesManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavFoodAdapter extends RecyclerView.Adapter<FavMealViewModel> {
    private final Context context;
    private final List<Food> foods;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Constructor
    public FavFoodAdapter(Context context, List<Food> foods) {
        this.context = context;
        this.foods = foods;
    }

    @NonNull
    @Override
    public FavMealViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavMealViewModel(LayoutInflater.from(context)
                .inflate(R.layout.item_favorite_food, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavMealViewModel holder, int position) {
        if (foods == null || foods.isEmpty()) return;

        // Bind data
        Food currentFood = foods.get(position);
        holder.Foodname.setText(currentFood.getName());
        holder.quantity.setText(String.valueOf(currentFood.getAmount()));

        // Set click listener for the Remove icon
        holder.Remove.setOnClickListener(v -> {
            int positionToRemove = holder.getAdapterPosition();
            if (positionToRemove != RecyclerView.NO_POSITION) {

                // Call method to remove food from database and update UI after removal
                removeFavFood(foods.get(positionToRemove), positionToRemove);
            }
        });

        holder.linearLayout.setOnClickListener(v -> {
            int addMealPosition = holder.getAdapterPosition();
            if (addMealPosition != RecyclerView.NO_POSITION) {

                addFoodDB(foods.get(addMealPosition));

            }
        });
    }

    @Override
    public int getItemCount() {
        return foods != null ? foods.size() : 0;
    }

    public void removeFavFood(Food food, int positionToRemove) {
        int profileId = PreferencesManager.getLastProfileId(context);

        DataBase database = DataBase.getInstance(context);
        FoodPrefDao foodPrefDao = database.foodPrefDao();

        executorService.submit(() -> {
            // Delete food from the database
            foodPrefDao.deleteFoodPreferencesByProfileId(profileId, food.getId());

            // Update UI on the main thread after database operation
            ((android.app.Activity) context).runOnUiThread(() -> {
                foods.remove(positionToRemove);
                notifyItemRemoved(positionToRemove);
            });
        });
    }

    private FoodDao foodDao;
    private Daily_TrackDao dailyTrackDao;
    private FoodConsumptionDao foodConsumptionDao;
    private FoodConsumption foodConsumption;
    private void addFoodDB(Food food ){
        int profileId = PreferencesManager.getLastProfileId(context);

        DataBase database = DataBase.getInstance(context);
        foodDao = database.foodDao();
        dailyTrackDao = database.daily_TrackDao();
        foodConsumptionDao = database.foodConsumptionDao();

        executorService.submit(() -> {
            Food selectedFood = foodDao.getFoodById(food.getId());
            if (selectedFood == null) {
                Log.wtf("FavFoodAdapter", "Selected food is null");
                return;
            }

            double[] nutritionValues = getNutritionValues(selectedFood, food.getAmount());
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
                        selectedFood.getId(), (int) food.getAmount(), currentDate);

                foodConsumption.setTotalCalories((int) calories);
                foodConsumption.setTotalProtein((int) proteins);
            } else {
                foodConsumption.addCaloriesNdProtein((int) calories, (int) proteins, (int) food.getAmount());
            }
            Log.wtf("addmeal", "nits:"+foodConsumption.toString());
            foodConsumptionDao.upsert(foodConsumption);
            Log.wtf("addmeal", "nits:"+calories+"/"+proteins);
            dailyTrackDao.AddCaloriesOrProtein(profileId, (int) calories, (int) proteins, (int) carbs, (int) fats);

            Log.wtf("FavFoodAdapter", "meal added sufuecly");
            ((android.app.Activity) context).runOnUiThread(() -> {
                Toast.makeText(context, "Meal added", Toast.LENGTH_SHORT).show();
            });
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
}


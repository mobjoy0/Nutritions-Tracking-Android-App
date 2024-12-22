package com.wess.makmouk.others;

import android.content.Context;
import android.util.Log;
import com.opencsv.CSVReader;
import com.wess.makmouk.R;
import com.wess.makmouk.R.raw;
import com.wess.makmouk.databases.Food;
import com.wess.makmouk.databases.FoodDao;


import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVImport {
    private FoodDao foodDao;

    public CSVImport(FoodDao foodDao) {
        this.foodDao = foodDao;
    }

    public void loadFoodDataFromCSV(Context context) {
        List<Food> foods = new ArrayList<>();

        try {
            // Open the raw CSV file
            InputStreamReader isReader = new InputStreamReader(context.getResources().openRawResource(R.raw.food_data));
            // Change to your CSV file name
            CSVReader reader = new CSVReader(isReader);

            String[] nextLine;
            // Read the CSV file line by line
            while ((nextLine = reader.readNext()) != null) {
                // Skip header if needed (first row is headers)
                if (nextLine[0].equalsIgnoreCase("Food")) {
                    continue; // Skip header row
                }

                // Extract values
                String name = nextLine[0];
                String grams = nextLine[1];
                String calories = nextLine[2];
                String protein = nextLine[3];
                String fat = nextLine[4];
                String carbs = nextLine[5];

                // Create a new Food object
                Food food = new Food();
                food.setName(name);
                food.setAmount((int)(Float.parseFloat(grams)));
                food.setCalories((int)(Float.parseFloat(calories)));
                food.setProtein(Float.parseFloat(protein));
                food.setFats(Float.parseFloat(fat));
                food.setCarbs(Float.parseFloat(carbs));
                food.setBarCode("");
                if (food.getName().equals("Egg(large)") || food.getName().equals("Egg(small)")){
                    food.setUnit("piece");
                } else {
                    food.setUnit("g");
                }

                // Add to the list
                foods.add(food);
            }

            // Save to database using foodDao
            for (Food food : foods) {
                foodDao.insertFood(food);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CSVImport", "Error reading CSV", e);
        }
    }
}

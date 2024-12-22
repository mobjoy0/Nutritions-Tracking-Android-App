package com.wess.makmouk.others;

import android.content.Context;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;
import android.os.StrictMode;
import android.util.Pair;

import com.wess.makmouk.databases.*;

public class OpenFoodFactsAPI {
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private DataBase dataBase;
    private FoodDao foodDao;
    public Pair<Food, String> getNutritionValuesAPI(String barcode, Context context) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String apiUrl = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";
        try {
            // Create URL object
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse JSON response
            JSONObject myResponse = new JSONObject(response.toString());
            int status = myResponse.optInt("status", 0);

            if (status != 1) {
                System.out.println("Product not found for barcode: " + barcode);
                return null;
            }

            JSONObject product = myResponse.getJSONObject("product");
            String productName = product.optString("product_name", "Unknown product");

            JSONObject nutriments = product.optJSONObject("nutriments");
            if (nutriments == null) {
                System.out.println("No nutritional information found for product: " + productName);
                return null;
            }

            double calories = nutriments.optDouble("energy-kcal_100g", 0.0);
            double caloriesKj = nutriments.optDouble("energy-kj_100g", 0.0);
            double protein = nutriments.optDouble("proteins_100g", 0.0);
            double carbs = nutriments.optDouble("carbohydrates_100g", 0.0);
            double fats = nutriments.optDouble("fat_100g", 0.0);
            String imageUrl = product.optString("image_url", "");

            // Output results
            if (calories == 0){
                calories = (caloriesKj / 4.184);
            }

            Food food = new Food();
            food.setBarCode(barcode);
            food.setName(productName);
            food.setCalories((int)calories);
            food.setProtein((float)protein);
            food.setCarbs((float)carbs);
            food.setFats((float)fats);
            food.setAmount(100);
            food.setUnit("g");

            dataBase = DataBase.getInstance(context);
            foodDao = dataBase.foodDao();

            executorService.submit(() -> {
                if ( foodDao.getFoodByName(productName) == null){
                    foodDao.insertFood(food);
                    Log.wtf("api"," food added to database");
                }
            });







            return new Pair<>(food, imageUrl);

        } catch (Exception e) {
            System.out.println("Error fetching product details: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}

package com.wess.makmouk.others;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.JSONObject;
import android.util.Pair;

import com.wess.makmouk.databases.*;

public class OpenFoodFactsAPI {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private DataBase dataBase;
    private FoodDao foodDao;

    public Pair<Food, String> getNutritionValuesAPI(String barcode, Context context) {
        String apiUrl = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";

        try {

            Future<String> futureResponse = executorService.submit(() -> {
                HttpURLConnection connection = null;
                try {

                    URL url = new URL(apiUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            });

            // Wait for response
            String jsonResponse = futureResponse.get();
            JSONObject myResponse = new JSONObject(jsonResponse);

            // Parse JSON response
            return parseProductDetails(myResponse, barcode, context);

        } catch (Exception e) {
            Log.e("OpenFoodFactsAPI", "Error fetching product details", e);
        }
        return null;
    }

    private Pair<Food, String> parseProductDetails(JSONObject response, String barcode, Context context) {
        int status = response.optInt("status", 0);
        if (status != 1) {
            Log.w("OpenFoodFactsAPI", "Product not found for barcode: " + barcode);
            return null;
        }

        JSONObject product = response.optJSONObject("product");
        if (product == null) return null;

        String productName = product.optString("product_name", "Unknown product");
        JSONObject nutriments = product.optJSONObject("nutriments");
        if (nutriments == null) {
            Log.w("OpenFoodFactsAPI", "No nutritional information found for product: " + productName);
            return null;
        }

        double calories = nutriments.optDouble("energy-kcal_100g", 0.0);
        if (calories == 0) {
            calories = nutriments.optDouble("energy-kj_100g", 0.0) / 4.184;
        }
        double protein = nutriments.optDouble("proteins_100g", 0.0);
        double carbs = nutriments.optDouble("carbohydrates_100g", 0.0);
        double fats = nutriments.optDouble("fat_100g", 0.0);
        String imageUrl = product.optString("image_url", "");

        // Create Food object
        Food food = new Food();
        food.setBarCode(barcode);
        food.setName(productName);
        food.setCalories((int) calories);
        food.setProtein((float) protein);
        food.setCarbs((float) carbs);
        food.setFats((float) fats);
        food.setAmount(100);
        food.setUnit("g");

        // Save to database
        dataBase = DataBase.getInstance(context);
        foodDao = dataBase.foodDao();

        executorService.submit(() -> {
            if (foodDao.getFoodByName(productName) == null) {
                foodDao.insertFood(food);
                Log.d("OpenFoodFactsAPI", "Food added to database");
            }
        });

        return new Pair<>(food, imageUrl);
    }
}

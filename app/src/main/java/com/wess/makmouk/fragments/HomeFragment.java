package com.wess.makmouk.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;


import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.text.style.ForegroundColorSpan;
import android.text.Spanned;
import android.graphics.Color;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.squareup.picasso.Picasso;
import com.wess.makmouk.R;
import com.wess.makmouk.databases.*;
import com.wess.makmouk.activities.*;
import com.wess.makmouk.others.OpenFoodFactsAPI;
import com.wess.makmouk.preferencesmanager.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private DataBase dataBase;
    private ProfileDao profileDao;
    private Daily_TrackDao dailyTrackDao;
    private FoodDao foodDao;
    private TextView carbsText, fatsText, rightProgressText, leftProgressText, insideProgressText, DetailsText, proteinText, weight;
    private CircularProgressIndicator calories_bar;
    private LinearProgressIndicator protein_bar;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ImageButton plus_weight, minus_weight;
    private int Profile_index ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout
        View View = inflater.inflate(R.layout.fragment_home, container, false);


        carbsText = View.findViewById(R.id.carbsView);
        fatsText = View.findViewById(R.id.fatView);
        proteinText = View.findViewById(R.id.proteinView);
        rightProgressText = View.findViewById(R.id.rightProgress);
        leftProgressText = View.findViewById(R.id.leftofprogress);
        insideProgressText = View.findViewById(R.id.insideProgressData);
        calories_bar = View.findViewById(R.id.Circle_progress);
        protein_bar = View.findViewById(R.id.protein_bar);
        plus_weight = View.findViewById(R.id.plus_weight);
        minus_weight = View.findViewById(R.id.minus_weight);
        weight = View.findViewById(R.id.weight);

        dataBase = DataBase.getInstance(requireContext());
        profileDao = dataBase.profileDao();
        dailyTrackDao = dataBase.daily_TrackDao();

        Profile_index = PreferencesManager.getLastProfileId(requireContext());

        LoadProfileInfo(Profile_index);

        plus_weight.setOnClickListener(adjustWeight(0.1, Profile_index));
        minus_weight.setOnClickListener(adjustWeight(-0.1, Profile_index));

        LinearLayout mealRecord = View.findViewById(R.id.mealRecord);
        LinearLayout addMeal = View.findViewById(R.id.add_meal);
        LinearLayout scanMeal = View.findViewById(R.id.scan_product);
        LinearLayout Create_meal = View.findViewById(R.id.Create_meal);


        Create_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(requireContext(), CreateMealActivity.class);
                startActivity(intent);
            }
        });

        scanMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                scanCode();
            }
        });

        mealRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(requireContext(), MealRecord.class);
                startActivity(intent);
            }
        });

        addMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(requireContext(), AddMealActivity.class);
                startActivity(intent);
            }
        });


        return View;
    }

    @Override
    public void onResume() {
        super.onResume();

        Profile_index = PreferencesManager.getLastProfileId(requireContext());
        LoadProfileInfo(Profile_index);
    }


    private View.OnClickListener adjustWeight(double x, int profileId) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateButtonScale(view, 1.1f);

                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        DailyTrack track = dailyTrackDao.get_TodaysTrack(profileId);
                        Profile profile = profileDao.getProfileById(profileId);

                        track.setWeight((float) (track.getWeight() + x));
                        profile.setCurrent_weight(track.getWeight());

                        dailyTrackDao.updateDailyTrack(track);
                        profileDao.updateProfile(profile);

                        requireActivity().runOnUiThread(() -> {
                            weight.setText(String.format("%.1f", profile.getCurrent_weight()));
                        });
                    }
                });
            }
        };
    }

    private void animateButtonScale(View button, float scaleFactor) {
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(button, "scaleX", 1f, scaleFactor);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(button, "scaleY", 1f, scaleFactor);

        scaleUpX.setDuration(100);
        scaleUpY.setDuration(100);

        scaleUpX.start();
        scaleUpY.start();

        scaleUpX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(button, "scaleX", scaleFactor, 1f);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(button, "scaleY", scaleFactor, 1f);

                scaleDownX.setDuration(100);
                scaleDownY.setDuration(100);

                scaleDownX.start();
                scaleDownY.start();
            }
        });
    }







    private void LoadProfileInfo(int id) {


        executorService.submit(new Runnable() {
            @Override
            public void run() {

                Profile profile = profileDao.getProfileById(id);
                DailyTrack track = dailyTrackDao.get_TodaysTrack(id);

                double calories_goal = profile.getGoal_weight() - profile.getCurrent_weight();

                final double[] TDEE = new double[1];

                Log.d("log", "Calculating cal_goal=" + calories_goal + "\n" + track.toString());

                TDEE[0] = profile.getTDEE();


                // Perform progress calculations on the main thread
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //set the nutrictions values
                        weight.setText(String.format("%.1f", profile.getCurrent_weight()));
                        carbsText.setText(track.getCarbs()+"g");
                        fatsText.setText(track.getFats()+"g");
                        leftProgressText.setText(String.valueOf(track.getCaloriesConsumed()));
                        rightProgressText.setText(String.valueOf((int)TDEE[0]));

                        protein_bar.setProgress((int)((track.getProtein()/(profile.getCurrent_weight() * 1.6)) * 100));

                        String proteinTextContent = track.getProtein() + "/" + (int)(profile.getCurrent_weight() * 1.6) + "g";
                        if (track.getProtein() < profile.getCurrent_weight() * 1.6 / 4) {

                            SpannableString spannableString = new SpannableString(proteinTextContent);
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFA500")), 0, proteinTextContent.indexOf("/"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            proteinText.setText(spannableString);

                        } else if (track.getProtein() > profile.getCurrent_weight() * 1.6) {

                            SpannableString spannableString = new SpannableString(proteinTextContent);
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#00ff00")), 0, proteinTextContent.indexOf("/"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            proteinText.setText(spannableString);

                        } else {
                            proteinText.setText(proteinTextContent);
                        }



                        if (TDEE[0] - track.getCaloriesConsumed() < 0) {
                            Log.d("log", "Calories consumed exceed TDEE");
                            calories_bar.setProgress(100);

                            insideProgressText.setTextColor(Color.parseColor("#dde5b6"));

                            insideProgressText.setText("Done!");
                        } else {
                            int progress = (int) ((track.getCaloriesConsumed() / TDEE[0]) * 100);

                            if (track.getCaloriesConsumed() == 0) {
                                Log.wtf("log","tfff = "+track.getCaloriesConsumed());
                                progress = 3;
                                insideProgressText.setText("Start tracking!");
                            } else {
                                insideProgressText.setText(String.valueOf((int) (TDEE[0] - track.getCaloriesConsumed())));
                            }
                            calories_bar.setProgress(progress, true);

                        }
                    }
                });
            }
        });
    }

    private void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES);
        options.setPrompt("Scan a product");
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setCaptureActivity(ScanActivity.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {

        if (result.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            // Inflate the custom layout
            View dialogView = getLayoutInflater().inflate(R.layout.product_info, null);

            // Get references to the views
            ImageView productImageView = dialogView.findViewById(R.id.productImageView);
            TextView productNameTextView = dialogView.findViewById(R.id.productNameTextView);
            TextView nutritionalValuesTextView = dialogView.findViewById(R.id.nutritionalValuesTextView);

            // Fetch product information
            OpenFoodFactsAPI api = new OpenFoodFactsAPI();
            Pair<Food, String> resultPair = api.getNutritionValuesAPI(result.getContents(), requireContext());

            if (resultPair != null) {
                Food food = resultPair.first;
                String imageUrl = resultPair.second;

                // Set product name and nutritional info
                productNameTextView.setText(food.getName());
                nutritionalValuesTextView.setText("Calories: " + food.getCalories() + " kcal\n" +
                        "Protein: " + food.getProtein() + "g\n" +
                        "Carbs: " + food.getCarbs() + "g\n" +
                        "Fats: " + food.getFats() + "g");

                // Load the product image if the URL exists
                if (!imageUrl.isEmpty()) {
                    Picasso.get().load(imageUrl).into(productImageView);
                } else {
                    productImageView.setImageResource(R.drawable.new_button);
                }
            } else {
                productNameTextView.setText("Product not found.");
                nutritionalValuesTextView.setText("No nutritional information available.");
            }

            // Set the dialog view
            builder.setView(dialogView);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });






}


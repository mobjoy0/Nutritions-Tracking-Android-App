package com.wess.makmouk.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.slider.Slider;
import com.wess.makmouk.R;
import com.wess.makmouk.databases.*;
import com.wess.makmouk.preferencesmanager.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateProfileActivity extends AppCompatActivity {

    EditText weight_input, height_input, name_input;
    DatePicker birthday_input;
    CardView saveButton;
    TextView Error_message;
    RadioGroup gender_radio;
    DataBase db;
    ProfileDao profileDao;
    Daily_TrackDao dailyTrackDao;
    Spinner spinner;
    Slider goalWeightSlider;

    // Background thread
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createprofile);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // Use ContextCompat.getColor() for compatibility with older Android versions
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.cherry_red));
        }
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (PreferencesManager.getLastProfileId(CreateProfileActivity.this) == -1){
                } else {
                    finish();
                }
            }
        };
        // Add the callback to the activity's back pressed dispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);



        name_input = findViewById(R.id.name_input);
        birthday_input = findViewById(R.id.birthday_input);
        height_input = findViewById(R.id.height_input);
        weight_input = findViewById(R.id.weight_input);
        saveButton = findViewById(R.id.saveButton);
        Error_message = findViewById(R.id.textView);
        gender_radio = findViewById(R.id.gender_radio);
        spinner = findViewById(R.id.activitySpinner);
        setSpinnerTheme();
        goalWeightSlider = findViewById(R.id.goal_weight_slider);




        // Save button clicked to validate and save the profile
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Saving profile...", Toast.LENGTH_SHORT).show();
                Profile profile = new Profile();


                String name = name_input.getText().toString().trim();
                int year = birthday_input.getYear();
                int month = birthday_input.getMonth();
                int day = birthday_input.getDayOfMonth();

                String birthday = year + "/" + (month + 1) + "/" + day;

                String selectedActivityLevel = spinner.getSelectedItem() != null ? spinner.getSelectedItem().toString() : "Sedentary";
                profile.setActivityIndex(getSelectedActivityLevel(selectedActivityLevel));

                profile.setGoal_weight(goalWeightSlider.getValue());


                // Validate empty fields
                Boolean validity = Check_Data_validity(profile, name, birthday);

                // Initialize Room database
                db = DataBase.getInstance(getApplicationContext());
                profileDao = db.profileDao();
                dailyTrackDao = db.daily_TrackDao();



                Log.d("CreateProfileActivity", profile.toString());

                // Submit the task to the background thread using executor
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("CreateProfileActivity", "CONDITION1 ");
                        try{
                            if (validity){
                                profileDao.insertProfile(profile);
                                Integer LastProfile_index = profileDao.getLastInsertedProfileId();
                                Log.wtf("CreateProfileActivity", "sucessful profile Profile count="+LastProfile_index);
                                PreferencesManager.saveLastProfileId(CreateProfileActivity.this, LastProfile_index);
                                //create first day track
                                Create_track(LastProfile_index,Float.parseFloat(weight_input.getText().toString()));
                            } else {
                                return;
                            }

                        ;}catch(Exception e){
                            Log.wtf("CreateProfileActivity", "CONDITION3 exepction happened:"+e.toString());
                            Log.wtf("CreateProfileActivity", "profile id ="+profileDao.getLastInsertedProfileId());
                        }


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(getApplicationContext(), "Created sucs", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CreateProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        });



                    }
                });
            }
        });
    }


    private Boolean Check_Data_validity(Profile profile, String name, String birthday) {
        if (name.isEmpty() || birthday.isEmpty()) {
            Error_message.setText("Please fill in all fields.");
            return false;
        }

        if (name.length() > 20) {
            Error_message.setText("Name is too long.");
            return false;
        }

        double height = 0, weight = 0;
        try {
            height = Float.parseFloat(height_input.getText().toString());
        } catch (NumberFormatException e) {
            Error_message.setText("Invalid height. Please enter a valid number.");
            return false;
        }

        try {
            weight = Float.parseFloat(weight_input.getText().toString());
        } catch (NumberFormatException e) {
            Error_message.setText("Invalid weight. Please enter a valid number.");
            return false;
        }

        if (weight <= 0 || height <= 0 || weight > 300 || height > 300) {
            Error_message.setText("Please enter a valid number.");
            return false;
        }

        // Validate gender selection
        int gender_radio_selected = gender_radio.getCheckedRadioButtonId();
        if (gender_radio_selected == -1) {
            Error_message.setText("Please select a gender.");
            return false;
        }

        // Get the selected gender
        RadioButton gender_selected = findViewById(gender_radio_selected);
        String gender = gender_selected.getText().toString();

        profile.setBirthday(birthday);

        if (profile.getAge() < 12){
            Error_message.setText("you have to be older than 12 years old.");
            return false;
        }

        // Set other profile data
        profile.setName(name);
        profile.setCurrent_weight(weight);
        profile.setHeight(height);
        profile.setGender(gender);

        return true;
    }


    private void Create_track(int id, float weight) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        DailyTrack track = new DailyTrack(id, currentDate, 0, 0.0f, weight, 0, 0);

        executorService.submit(() -> {
            try {
                DailyTrack existingTrack = dailyTrackDao.get_1_DailyTrack(id, currentDate);
                if (existingTrack == null) {
                    // Insert new track
                    dailyTrackDao.insertDailyTrack(track);
                    Log.wtf("DailyTrack", "New track added for date: " + currentDate);
                } else {
                    Log.wtf("DailyTrack", "Error Track already exists for date: " + currentDate);
                }
            } catch (Exception e) {
                Log.wtf("DailyTrack", "Error creating track: ", e);
            }
        });
    }

    public void setSpinnerTheme(){

        String[] activityLevels = {
                "Sedentary",
                "Lightly Active",
                "Moderately Active",
                "Very Active",
                "Extra Active"
        };

        // Create an ArrayAdapter with the activity levels
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, activityLevels) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                // Customize the dropdown view here
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.soft_cream)); // Set dropdown item color
                return view;
            }
        };

        // Apply custom layout for dropdown items
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter for the Spinner
        spinner.setAdapter(adapter);
    }


    private double getSelectedActivityLevel(String activityLevel) {
        switch (activityLevel) {
            case "Sedentary":
                return 1.2;
            case "Lightly Active":
                return 1.375;
            case "Moderately Active":
                return 1.55;
            case "Very Active":
                return 1.725;
            case "Extra Active":
                return 1.9;
            default:
                return 1.2;
        }
    }






}




package com.wess.makmouk.fragments;
import com.wess.makmouk.R;
import com.wess.makmouk.activities.CreateProfileActivity;
import com.wess.makmouk.activities.ProfileSelectionActivity;
import com.wess.makmouk.databases.*;
import com.wess.makmouk.preferencesmanager.*;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ProfileFragment extends Fragment {
    private DataBase db;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ProfileDao profileDao;
    private TextView name, age, gender, height, goal, weight, BMIview;
    private ProgressBar goal_bar;
    private Button track, edit_name, edit_height, manage, delete;
    private Profile profileData;
    private DailyTrack dailyTrack;
    private Daily_TrackDao dailyTrackDao;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Get the selected profile index




        name = view.findViewById(R.id.tvname);
        age = view.findViewById(R.id.tvage);
        gender = view.findViewById(R.id.tvgender);
        height = view.findViewById(R.id.heightView);
        goal = view.findViewById(R.id.tvgoal);
        goal_bar = view.findViewById(R.id.goal_bar);
        manage = view.findViewById(R.id.manage_button);
        delete = view.findViewById(R.id.delete_button);
        weight = view.findViewById(R.id.weightView);
        BMIview = view.findViewById(R.id.BMIView);


        // Fetch and display profile data
        int Profile_index = PreferencesManager.getLastProfileId(requireContext());
        Log.wtf("CreateProfileActivity", "profilefrag lastP="+Profile_index);

        getProfile_info(Profile_index);


        delete.setOnClickListener(view1 -> {

            Confirm_Delete_Profile(Profile_index);

        });


        //manage button
        manage.setOnClickListener(view1 -> {

            NavigateToProfileSelection();

        });




        // Track button functionality
   /*     track.setOnClickListener(view1 -> {

            if (profileData == null) {
                Toast.makeText(getContext(), "Profile data not loaded. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }


            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileFragment.this.getContext());
            LayoutInflater inflater1 = requireActivity().getLayoutInflater();
            View dialogView = inflater1.inflate(R.layout.diagol_input, null);
            builder.setView(dialogView);


            EditText Input = dialogView.findViewById(R.id.weight_input);
            Input.setHint("Enter your weight");

            builder.setTitle("Update Weight")
                    .setMessage("Enter your new weight:")
                    .setPositiveButton("Save", (dialog, id) -> {

                        String weightText = Input.getText().toString().trim();
                        if (!(weightText.isEmpty()) && testPositivity(weightText) && Float.parseFloat(weightText) < 200) {


                            executorService.submit(new Runnable() {
                                @Override
                                public void run() {

                                    profileData.setCurrent_weight(Float.parseFloat(weightText));
                                    Log.wtf("ID OF THE PROFILE GOING TO GET UPDAETED", "profiel updated weight id="+profileData.getId());
                                    profileDao.updateProfile(profileData);
                                    UpdateTrack(profileData.getId(), Float.parseFloat(weightText));

                                }
                            });

                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    name.setText(weightText);
                                    Toast.makeText(getContext(), "weight is set to " + weightText+" kgs", Toast.LENGTH_SHORT).show();

                                }
                            });

                        } else {
                            Toast.makeText(getContext(), "Please enter a valid weight.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

            // Show the dialog
            builder.create().show();
        });*/




        //height button
    /*    edit_height.setOnClickListener(view1 -> {

            if (profileData == null) {
                Toast.makeText(getContext(), "Profile data not loaded. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }


            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileFragment.this.getContext());
            LayoutInflater inflater1 = requireActivity().getLayoutInflater();
            View dialogView = inflater1.inflate(R.layout.diagol_input, null);
            builder.setView(dialogView);


            EditText Input = dialogView.findViewById(R.id.weight_input);
            Input.setHint("Enter your height");

            builder.setTitle("Update Height")
                    .setMessage("Enter your new height:")
                    .setPositiveButton("Save", (dialog, id) -> {

                        String heightText = Input.getText().toString().trim();
                        if (!(heightText.isEmpty()) && testPositivity(heightText) && Float.parseFloat(heightText) < 300) {


                            executorService.submit(new Runnable() {
                                @Override
                                public void run() {

                                    profileData.setHeight(Float.parseFloat(heightText));
                                    Log.wtf("ID OF THE PROFILE GOING TO GET UPDAETED", "profiel updated height id="+profileData.getId());
                                    profileDao.updateProfile(profileData);

                                }
                            });

                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    name.setText(heightText);
                                    Toast.makeText(getContext(), "weight is set to " + heightText+" kgs", Toast.LENGTH_SHORT).show();

                                }
                            });

                        } else {
                            Toast.makeText(getContext(), "Please enter a valid weight.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

            // Show the dialog
            builder.create().show();
        });*/





        //edit button
        /*edit_name.setOnClickListener(view1 -> {

            if (profileData == null) {
                Toast.makeText(getContext(), "Profile data not loaded. Please try again.", Toast.LENGTH_SHORT).show();
                return; // Exit if profile data is not available
            }
            // Create a custom layout for the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileFragment.this.getContext());
            LayoutInflater inflater1 = requireActivity().getLayoutInflater();
            View dialogView = inflater1.inflate(R.layout.diagol_input, null);
            builder.setView(dialogView);

            // Get the input field from the dialog layout
            EditText Input = dialogView.findViewById(R.id.weight_input);
            Input.setHint("Enter your name");
            Input.setInputType(InputType.TYPE_CLASS_TEXT);  // Correct way to set input type for text

            builder.setTitle("Update Name")
                    .setMessage("Enter your new name:")
                    .setPositiveButton("Save", (dialog, id) -> {
                        String nameText = Input.getText().toString().trim();
                        if (!nameText.isEmpty() && nameText.length() < 20) {


                            executorService.submit(new Runnable() {
                                @Override
                                public void run() {

                                    profileData.setName(nameText);
                                    Log.wtf("ID OF THE PROFILE GOING TO GET UPDAETED", "profiel updated height id="+profileData.getId());
                                    profileDao.updateProfile(profileData);

                                }
                            });

                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    name.setText(nameText);
                                    Toast.makeText(getContext(), "New name: " + nameText, Toast.LENGTH_SHORT).show();

                                }
                            });

                        } else {
                            Toast.makeText(getContext(), "Please enter a name.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

            // Show the dialog
            builder.create().show();
        })*/;




        return view; // Return the inflated view
    }


    public void getProfile_info(int id) {
        db = DataBase.getInstance(getContext());
        profileDao = db.profileDao();
        dailyTrackDao = db.daily_TrackDao();

        profileDao.getLiveProfileById(id).observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                profileData = profile;

                // Update UI elements with profile data
                name.setText(profile.getName());
                weight.setText(((int) profile.getCurrent_weight()) + "kgs");
                height.setText((int) profile.getHeight() + "cms");
                age.setText(profile.getAge()+"");
                gender.setText((profile.getGender().equals("Male") ? "Male" : "Female"));
                String goalText;
                goalText = (profile.getCurrent_weight() >= profile.getGoal_weight())? "You have reached your goal! \uD83E\uDD73  \uD83C\uDF89" : "You are "
                        +((int) ((profile.getCurrent_weight()/profile.getGoal_weight()*100))+"% there,\n"+(profile.getGoal_weight() - profile.getCurrent_weight())+"kgs left!");

                goal.setText(goalText);
                goal_bar.setMax((int) profile.getGoal_weight());
                goal_bar.setProgress((int) profile.getCurrent_weight());

                if (profile.getBMI() < 18.0f || profile.getBMI() > 30.0f){
                    BMIview.setTextColor(Color.parseColor("#ff0000"));
                }
                BMIview.setText(profile.getBMI()+" BMI");

            } else {
                Toast.makeText(getContext(), "Profile not found. Please create one. ID: " + id, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public boolean testPositivity(String x){

        try{
            float y = Float.parseFloat(x);

            return !(y <= 0);
        } catch (NumberFormatException e){
            return false;
        }
    }




    private void Confirm_Delete_Profile(int id) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("Do you really want to kys?")
                .setCancelable(false)  // Prevent dialog from being dismissed by touching outside
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // Delete the profile from the database
                                    profileDao.deleteProfile(profileDao.getProfileById(id));
                                    Log.wtf("CreateProfileActivity", "Profile deleted successfully");

                                    // Ensure the deletion was successful by checking the last inserted profile
                                    Integer lastProfileId = profileDao.getLastInsertedProfileId();
                                    Log.wtf("CreateProfileActivity", "Last Profile ID after deletion: " + lastProfileId);

                                    if (lastProfileId == null) {

                                        PreferencesManager.saveLastProfileId(requireContext(), -1);
                                        Intent intent = new Intent(requireContext(), CreateProfileActivity.class);
                                        startActivity(intent);
                                        requireActivity().finish();
                                    } else {

                                        PreferencesManager.saveLastProfileId(requireContext(), 0);
                                        PreferencesManager.saveLastProfileId(requireContext(), lastProfileId);
                                        NavigateToProfileSelection();

                                    }
                                } catch (Exception e) {
                                    Log.wtf("CreateProfileActivity", "Exception occurred during profile deletion: " + e.toString());
                                }

                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Show a toast after the deletion process
                                        Toast.makeText(requireContext(), "Profile deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                })
                .setNegativeButton("No", null)  // Dismiss dialog on "No"
                .show();
    }


    private void UpdateTrack(int id, float weight){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        DailyTrack new_track = new DailyTrack(id, currentDate, 0, 0.0f, weight, 0, 0);

        executorService.submit(() -> {
            try {
                DailyTrack existingTrack = dailyTrackDao.get_1_DailyTrack(id, currentDate);
                if (existingTrack == null) {
                    // Insert new track
                    dailyTrackDao.insertDailyTrack(new_track);
                    Log.wtf("DailyTrack", "New track added for date: " + currentDate);
                } else {
                    Log.wtf("DailyTrack", "UPDATING| Track already exists for date: " + currentDate);
                    dailyTrackDao.updateDailyTrack(new_track);
                    Log.wtf("DailyTrack", "Track updated for date: " + currentDate);
                }
            } catch (Exception e) {
                Log.wtf("DailyTrack", "Error creating track: ", e);
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();

        int profileId = PreferencesManager.getLastProfileId(requireContext());
        getProfile_info(profileId);
    }




    public void NavigateToProfileSelection(){

        Intent intent = new Intent(getContext(), ProfileSelectionActivity.class);
        startActivity(intent);

    }


}



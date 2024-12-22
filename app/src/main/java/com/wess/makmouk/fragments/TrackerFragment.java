package com.wess.makmouk.fragments;

import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.wess.makmouk.others.*;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.wess.makmouk.databases.DataBase;
import com.wess.makmouk.databases.Daily_TrackDao;
import com.wess.makmouk.databases.DailyTrack;
import com.wess.makmouk.preferencesmanager.PreferencesManager;
import com.wess.makmouk.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrackerFragment extends Fragment {
    private SwitchCompat dataToggle;
    private TextView averageProteinView, averageCaloriesView, todayscaloriesView, todaysproteinVeiw, dateView;
    private BarChart barChart;
    private LineChart lineChart;
    private RadioGroup radioGroup;
    private DataBase dataBase;
    private Daily_TrackDao dailyTrackDao;
    private int lastselectedDay = 1;
    private int Profile_index;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track, container, false);

        Profile_index = PreferencesManager.getLastProfileId(requireContext());

        // Initialize views
        dataToggle = view.findViewById(R.id.dataToggle);
        barChart = view.findViewById(R.id.barChart);
        lineChart = view.findViewById(R.id.combinedChart);
        barChart.getDescription().setEnabled(false);

        averageCaloriesView = view.findViewById(R.id.averageCalories);
        averageProteinView = view.findViewById(R.id.averageProtein);
        todayscaloriesView = view.findViewById(R.id.todayCalories);
        todaysproteinVeiw = view.findViewById(R.id.todayProtein);
        dateView = view.findViewById(R.id.date_text);

        // make today checked by default
        radioGroup = view.findViewById(R.id.radioGroup);
        RadioButton day1 = radioGroup.findViewById(R.id.day_1);
        day1.setChecked(true);

        // Set the listener for the switch toggle
        dataToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    // Show the LineChart and hide the BarChart
                    barChart.setVisibility(View.GONE);
                    lineChart.setVisibility(View.VISIBLE);
                } else {
                    // Show the BarChart and hide the LineChart
                    barChart.setVisibility(View.VISIBLE);
                    lineChart.setVisibility(View.GONE);
                }
            }
        });

// Initially, you can set the BarChart visible and LineChart hidden
        barChart.setVisibility(View.VISIBLE);
        lineChart.setVisibility(View.GONE);

        loadChartData(1, Profile_index);

        // Handle the selection of a radio button (Today, 7 days, 30 days)
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Check which radio button was selected and update chart data accordingly
                Log.wtf("Log","Loggigngng");
                if (checkedId == R.id.day_1) {
                    // Get data for today (use the database)
                    Log.wtf("HEREE1","TODAY");
                    lastselectedDay = 1;
                    loadChartData(1, Profile_index);
                } else if (checkedId == R.id.day_7) {
                    // Get data for the last 7 days
                    Log.wtf("HEREE1","7dyas");
                    lastselectedDay = 7;
                    loadChartData(7, Profile_index);
                } else if (checkedId == R.id.day_30) {
                    // Get data for the last 30 days
                    Log.wtf("HEREE1","30days");
                    lastselectedDay = 30;
                    loadChartData(30, Profile_index);
                }

            }
        });

        return view;
    }

    private void loadChartData(int days, int id) {
        Log.wtf("loadChartData", "Loading data for " + days + " days for Profile " + id);
        ConnectToDatabase(id);

        // Get today's date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        ArrayList<BarEntry> caloriesEntries = new ArrayList<>();
        ArrayList<BarEntry> proteinEntries = new ArrayList<>();
        ArrayList<Entry> weightEntries = new ArrayList<>();

        executorService.submit(new Runnable() {
            @Override
            public void run() {

                List<DailyTrack> tracks = new ArrayList<>();
                if (days == 7){
                    tracks = dailyTrackDao.getLast7DaysRecords(id);
                } else if (days > 7){
                    tracks = dailyTrackDao.get_30_DailyTracks(id);
                } else {
                    DailyTrack track = dailyTrackDao.get_TodaysTrack(id);
                    tracks.add(track);
                }
                Collections.reverse(tracks);

                if (tracks == null || tracks.isEmpty()) {
                    // Show a message indicating no data is available for the selected range
                    Toast.makeText(getContext(), "No data available for the selected range", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean divideByWeeks = days > 7;
                int weekIndex = 0;
                float weeklyCalories = 0, weeklyProtein = 0, weeklyWeight = 0;
                int averageCalories = 0;float averageProtein = 0;
                int weekCount = 0;
                String dateview = "";
                // loop through the last 'days' days
                for (int i = 0; i < days && i < tracks.size(); i++) {
                    DailyTrack track = tracks.get(i);
                    if (i == 0) {
                        dateview = track.getDate();
                    } else if (
                            (days == 7 && i == tracks.size() - 1) || (days == 7 && i == days - 1) || (i == tracks.size() - 1)
                    ) {
                        dateview = track.getDate() + " / " + dateview;
                    }
                    averageCalories += track.getCaloriesConsumed();
                    averageProtein += track.getProtein();

                    if (days == 1) {
                        caloriesEntries.add(new BarEntry(i, track.getCaloriesConsumed()));
                        proteinEntries.add(new BarEntry(i, track.getProtein()));
                        weightEntries.add(new Entry(i, track.getWeight()));
                        dateView.setText(track.getDate());
                    } else {
                        // Accumulate data for each week
                        if (divideByWeeks) {
                            Log.wtf("Log", "infos = "+ track.toString());
                            weeklyCalories += track.getCaloriesConsumed();
                            weeklyProtein += track.getProtein();
                            weeklyWeight += track.getWeight();
                            weekCount++;

                            // If we've accumulated data for a full week, store it and reset
                            if (weekCount == 7 || i == tracks.size() - 1) {
                                // Calculate the average weight for the week
                                float averageWeeklyWeight = weeklyWeight / weekCount;
                                Log.wtf("Log", "weekly average = " + weeklyCalories);

                                // Add weekly averages to the chart
                                caloriesEntries.add(new BarEntry(weekIndex, weeklyCalories));
                                proteinEntries.add(new BarEntry(weekIndex, weeklyProtein));
                                weightEntries.add(new Entry(weekIndex, averageWeeklyWeight)); // Use the average weight for the week

                                // Reset the weekly counters
                                weeklyCalories = 0;
                                weeklyProtein = 0;
                                weeklyWeight = 0;
                                weekCount = 0;
                                weekIndex++;
                            }
                        } else {
                            // If we don't need to group by week, just add the data for each day

                            caloriesEntries.add(new BarEntry(i, track.getCaloriesConsumed()));
                            proteinEntries.add(new BarEntry(i, track.getProtein()));
                            weightEntries.add(new Entry(i, track.getWeight()));
                        }
                    }
                }
                UpdateRecapTextView(averageCalories/days, averageProtein/days,
                        tracks.get(0).getCaloriesConsumed(), tracks.get(0).getProtein(), dateview);

                //ADDING TO CHARTS
                Log.wtf("loadChartData", "Adding data to charts averages="+averageCalories/tracks.size()+"/protien="+averageProtein/tracks.size()+"//"+tracks.get(0).getCaloriesConsumed());
                Add_DataToCharts(caloriesEntries, proteinEntries, weightEntries, days);

            }

        });

        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.wtf("loadChartData", "Data loaded ");

            }
        });


    }



    //ADD DATA TO CHARTS
    private void Add_DataToCharts(ArrayList<BarEntry> caloriesEntries, ArrayList<BarEntry> proteinEntries, ArrayList<Entry> weightEntries, int days){

        // Create the BarDataSet for Calories
        BarDataSet calorieDataSet = new BarDataSet(caloriesEntries, "Calories");
        calorieDataSet.setValueTextSize(12f);
        calorieDataSet.setColor(ContextCompat.getColor(requireContext(), R.color.static_textcolor));



        // Create the BarDataSet for Protein
        BarDataSet proteinDataSet = new BarDataSet(proteinEntries, "Protein");
        proteinDataSet.setValueTextSize(12f);
        proteinDataSet.setColor(ContextCompat.getColor(requireContext(), R.color.rosish_red));

        // Create a LineDataSet for weight data
        LineDataSet weightDataSet = new LineDataSet(weightEntries, "Weight");
        weightDataSet.setValueTextSize(12f);


        LineData lineData = new LineData(weightDataSet);
        lineChart.setData(lineData);


        BarData barData = new BarData(calorieDataSet, proteinDataSet);

        barData.setBarWidth(0.4f);

        //settings for each day mode
        if (days > 7 ) { // 7 days
            barChart.getXAxis().setValueFormatter(new WeekValueFormatter());
            lineChart.getXAxis().setValueFormatter(new WeekValueFormatter());
            barData.groupBars(-0.5f, 0.25f, 0f);
            XAxis xAxis = barChart.getXAxis();
            xAxis.setGranularity(1.1f); // Ensure labels are spaced at intervals of 1
            xAxis.setLabelCount(4); // Adjust this based on the number of labels
            XAxis xAxisLine = lineChart.getXAxis();
            xAxisLine.setGranularity(1.04f);
            xAxisLine.setLabelCount(4);
            CustomizeChart(xAxis);

        } else if (days == 1) {

            barChart.getXAxis().setValueFormatter(new DayValueFormatter(true));
            lineChart.getXAxis().setValueFormatter(new DayValueFormatter(true));
            XAxis xAxis = barChart.getXAxis();

            xAxis.setGranularity(1.05f);
            xAxis.setLabelCount(1);
            XAxis xAxisLine = lineChart.getXAxis();
            xAxisLine.setGranularity(1.04f);
            xAxisLine.setLabelCount(1);
            CustomizeChart(xAxis);


        } else {
            barChart.getXAxis().setValueFormatter(new DayValueFormatter(false));
            lineChart.getXAxis().setValueFormatter(new DayValueFormatter(false));
            barData.groupBars(-0.5f, 0.21f, 0f);
            XAxis xAxis = barChart.getXAxis();
            xAxis.setGranularity(1.05f);
            xAxis.setLabelCount(7);
            XAxis xAxisLine = lineChart.getXAxis();
            xAxisLine.setGranularity(1.04f);
            xAxisLine.setLabelCount(7);
            CustomizeChart(xAxis);

        }



        // Set the adjusted BarData to the BarChart
        barChart.setData(barData);  // Set data for BarChart
        barChart.invalidate(); // Refresh the chart to show updated data

        // Refresh the line chart
        lineChart.invalidate(); // Refresh the line chart
    }

    private void UpdateRecapTextView(int calories, float protein,int todaycalories, float todayprotein, String dateview){
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dateView.setText(dateview);
                averageCaloriesView.setText("Average Calories: " + calories);
                averageProteinView.setText("Average Protein: " + protein);
                todayscaloriesView.setText("Today's Calories intake: " + todaycalories);
                todaysproteinVeiw.setText("Today's Protein intake: " + todayprotein);
                Log.wtf("update views", "Updated views with " + calories + "/" + protein + "/" + todaycalories + "/" + todayprotein);
            }
        });

    }



    private void CustomizeChart(XAxis xAxis){

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(14f);
        xAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.parseColor("#FFFFFF"));


        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // Set the minimum Y-axis value to 0
        leftAxis.setDrawGridLines(false); // Optional: Clean up gridlines
        leftAxis.setTextSize(14f);
        leftAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Access the YAxis on the right and change its text color
        YAxis linerightAxis = lineChart.getAxisRight();
        linerightAxis.setEnabled(false);


        xAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.black)); // X-axis text color
        lineChart.getXAxis().setTextColor(ContextCompat.getColor(getContext(), R.color.black));

    }


    private void ConnectToDatabase(int id) {
        // Connect to DB
        dataBase = DataBase.getInstance(getContext());
        dailyTrackDao = dataBase.daily_TrackDao();
    }

    private void insertTestData(int id) {
        // Example data for the past 7 days
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 24; i++) {
            calendar.add(Calendar.DATE, -1);  // Move back one day at a time

            // Example values for testing
            String date = sdf.format(calendar.getTime());
            int caloriesConsumed = 2000 + (i * 50);  // Simulating calorie data
            float protein = 150 + (i * 5.0f);  // Simulating protein data
            float weight = 60 + (i * 0.5f);  // Simulating weight data
            int fats = 20 +(i*2);
            int carbs = 30 +(i*2);

            // Create and insert the DailyTrack object
            DailyTrack track = new DailyTrack(id, date, caloriesConsumed, protein, weight, carbs, fats);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    dailyTrackDao.insertDailyTrack(track);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Profile_index = PreferencesManager.getLastProfileId(requireContext());
        Log.wtf("log", "eeee:"+Profile_index);
        loadChartData(lastselectedDay, Profile_index);
    }

}
package com.wess.makmouk.others;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class DayValueFormatter extends ValueFormatter {
    private final String[] days = {"day7", "Day6", "Day5", "Day4", "Day3", "Yday", "Today"};
    private final boolean showOnlyToday;

    // Constructor to initialize the formatter with a toggle
    public DayValueFormatter(boolean showOnlyToday) {
        this.showOnlyToday = showOnlyToday;
    }

    @Override
    public String getFormattedValue(float value) {
        if (showOnlyToday) {
            return "Today"; // Always return "Today" if the toggle is true
        } else {
            int dayIndex = (int) value; // Convert the x value to an integer
            return days[dayIndex % 7]; // Rotate through the days array
        }
    }
}

package com.wess.makmouk.others;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class WeekValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        int weekIndex = (int) value;
        return (weekIndex == 3) ? "This week" : "Week "+String.valueOf(weekIndex + 1);
    }
}
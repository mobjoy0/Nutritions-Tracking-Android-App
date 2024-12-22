package com.wess.makmouk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wess.makmouk.fragments.*;
import com.wess.makmouk.databases.*;
import com.wess.makmouk.preferencesmanager.*;
import com.wess.makmouk.worker.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wess.makmouk.R;
import com.wess.makmouk.databases.DailyTrack;
import com.wess.makmouk.databases.DataBase;
import com.wess.makmouk.databases.FoodConsumption;
import com.wess.makmouk.databases.FoodDao;

import java.util.List;

public class FoodConsumptionAdapter extends RecyclerView.Adapter<MealRecordViewModel> {

    private final Context context;
    private final List<FoodConsumption> foodConsumptionList;

    // Constructor
    public FoodConsumptionAdapter(Context context, List<FoodConsumption> foodConsumptionList) {
        this.context = context;
        this.foodConsumptionList = foodConsumptionList;
    }

    @NonNull
    @Override
    public MealRecordViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MealRecordViewModel(LayoutInflater.from(context).inflate(R.layout.item_food_consumption, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MealRecordViewModel holder, int position) {

        holder.Foodname.setText(String.valueOf(foodConsumptionList.get(position).getFoodName()));
        holder.calories.setText(String.valueOf(foodConsumptionList.get(position).getTotalCalories()));
        holder.quantity.setText(String.valueOf(foodConsumptionList.get(position).getQuantity()));
        holder.proteins.setText(String.valueOf(foodConsumptionList.get(position).getTotalProtein()));



    }



    @Override
    public int getItemCount() {
        return foodConsumptionList != null ? foodConsumptionList.size() : 0;
    }


}

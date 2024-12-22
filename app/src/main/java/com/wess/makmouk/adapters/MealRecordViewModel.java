package com.wess.makmouk.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.wess.makmouk.R;
import com.wess.makmouk.activities.MainActivity;
import com.wess.makmouk.fragments.*;
import com.wess.makmouk.databases.*;
import com.wess.makmouk.preferencesmanager.*;
import com.wess.makmouk.worker.*;

import java.util.List;


public class MealRecordViewModel extends RecyclerView.ViewHolder{

    TextView Foodname, calories, proteins, quantity;

    public MealRecordViewModel(@NonNull View itemView){
        super(itemView);

        Foodname = itemView.findViewById(R.id.foodName);
        calories = itemView.findViewById(R.id.cals);
        proteins = itemView.findViewById(R.id.protein);
        quantity = itemView.findViewById(R.id.quantity);

    }
}


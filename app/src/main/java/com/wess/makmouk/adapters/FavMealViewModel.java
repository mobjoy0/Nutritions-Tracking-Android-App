package com.wess.makmouk.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wess.makmouk.R;

public class FavMealViewModel extends RecyclerView.ViewHolder{

    TextView Foodname, quantity;
    ImageView Remove;
    LinearLayout linearLayout;

    public FavMealViewModel(@NonNull View itemView){
        super(itemView);

        Foodname = itemView.findViewById(R.id.foodNameTextView);
        quantity = itemView.findViewById(R.id.foodQtTextView);
        Remove = itemView.findViewById(R.id.removeIcon);
        linearLayout = itemView.findViewById(R.id.addToRecord);

    }
}

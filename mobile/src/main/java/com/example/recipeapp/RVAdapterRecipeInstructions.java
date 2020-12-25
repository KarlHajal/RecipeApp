package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RVAdapterRecipeInstructions extends RecyclerView.Adapter {
    private Context applicationContext;
    private AnalysedInstructions instructions;

    public RVAdapterRecipeInstructions(Context applicationContext, AnalysedInstructions instructions) {
        this.applicationContext = applicationContext;
        this.instructions = instructions;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterSearchResult.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(applicationContext);
        view = mInflater.inflate(R.layout.cardview_item_search_result, parent, false);
        return new RecyclerViewAdapterSearchResult.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView recipe_title;
        ImageView recipe_thumbnail;
        CardView cardView;
        public MyViewHolder(View view) {
            super(view);
            recipe_title = itemView.findViewById(R.id.search_result_recipe_title);
            recipe_thumbnail = itemView.findViewById(R.id.search_result_recipe_img);
            cardView = itemView.findViewById(R.id.search_result_cardview);
        }
    }
}

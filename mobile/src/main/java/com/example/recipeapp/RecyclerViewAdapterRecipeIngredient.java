package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapterRecipeIngredient extends RecyclerView.Adapter<RecyclerViewAdapterRecipeIngredient.MyViewHolder> {
    private Context thisContext;
    private List<Ingredient> thisData;
    public static List<String> ingredientsList;
    public RecyclerViewAdapterRecipeIngredient(Context thisContext, List<Ingredient> thisData) {
        this.thisContext = thisContext;
        this.thisData = thisData;
        ingredientsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerViewAdapterRecipeIngredient.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(thisContext);
        view = mInflater.inflate(R.layout.item_ingredient, parent, false);
        return new RecyclerViewAdapterRecipeIngredient.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapterRecipeIngredient.MyViewHolder holder, final int position) {
        holder.text_ingredient_name.setText(thisData.get(position).getName());
        Picasso.get().load(thisData.get(position).getThumbnail()).into(holder.img_ingredient_thumbnail);
    }

    @Override
    public int getItemCount() {
        return thisData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_ingredient_name;
        ImageView img_ingredient_thumbnail;
        public MyViewHolder(View itemView) {
            super(itemView);
            text_ingredient_name = itemView.findViewById(R.id.recipe_ingredient_name);
            img_ingredient_thumbnail = itemView.findViewById(R.id.recipe_ingredient_img);
        }
    }
}

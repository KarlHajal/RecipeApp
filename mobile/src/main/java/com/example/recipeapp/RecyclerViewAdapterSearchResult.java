package com.example.recipeapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapterSearchResult extends RecyclerView.Adapter<RecyclerViewAdapterSearchResult.MyViewHolder> {
    private Context applicationContext;
    private List<Recipe> lstRecipe;
    public RecyclerViewAdapterSearchResult(Context applicationContext, List<Recipe> lstRecipe) {
        this.applicationContext = applicationContext;
        this.lstRecipe = lstRecipe;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(applicationContext);
        view = mInflater.inflate(R.layout.cardview_item_search_result, parent, false);
        return new MyViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.recipe_title.setText(lstRecipe.get(position).getTitle());
        if (lstRecipe.get(position).getThumbnail().isEmpty()) {
            holder.recipe_thumbnail.setImageResource(R.drawable.nopicture);
        } else{
            Picasso.get().load(lstRecipe.get(position).getThumbnail()).into(holder.recipe_thumbnail);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(applicationContext, Recipe_activity.class);
                intent.putExtra("id", lstRecipe.get(position).getId());
                intent.putExtra("title",lstRecipe.get(position).getTitle());
                intent.putExtra("img",lstRecipe.get(position).getThumbnail());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                applicationContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstRecipe.size();
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

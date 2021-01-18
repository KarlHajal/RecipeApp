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

public class RecyclerViewAdapterFavorites extends RecyclerView.Adapter<RecyclerViewAdapterFavorites.MyViewHolder>{
    private Context favContext;
    private List<Recipe> favData;

    RecyclerViewAdapterFavorites(Context favContext, List<Recipe> favData) {
        this.favContext = favContext;
        this.favData = favData;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterFavorites.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(favContext);
        view = mInflater.inflate(R.layout.cardview_item_favorite, parent, false);
        return new RecyclerViewAdapterFavorites.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapterFavorites.MyViewHolder holder, final int position) {
        holder.recipe_title.setText(favData.get(position).getTitle());
        holder.recipe_title.setTextSize(20);
        String thumbnailString = favData.get(position).getThumbnail();
        if (thumbnailString == null || thumbnailString.isEmpty()) {
            holder.img_recipe_thumbnail.setImageResource(R.drawable.nopicture);
        } else{
            Picasso.get().load(favData.get(position).getThumbnail()).into(holder.img_recipe_thumbnail);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(favContext, RecipeActivity.class);
                intent.putExtra("id", favData.get(position).getId());
                intent.putExtra("title",favData.get(position).getTitle());
                intent.putExtra("img",favData.get(position).getThumbnail());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                favContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView recipe_title;
        ImageView img_recipe_thumbnail;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            recipe_title = itemView.findViewById(R.id.fav_recipe_title);
            img_recipe_thumbnail = itemView.findViewById(R.id.fav_recipe_img);
            cardView = itemView.findViewById(R.id.favorites_cardview);
        }
    }
}

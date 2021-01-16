package com.example.recipeapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class RecyclerViewAdapterSearchResult extends RecyclerView.Adapter<RecyclerViewAdapterSearchResult.MyViewHolder> {
    private Context applicationContext;
    private List<Recipe> lstRecipe;
    FirebaseAuth mAuth;
    String uid;
    private static final String TAG = "RVA_SearchResult";

    public RecyclerViewAdapterSearchResult(Context applicationContext, List<Recipe> lstRecipe) {
        this.applicationContext = applicationContext;
        this.lstRecipe = lstRecipe;
        this.mAuth = FirebaseAuth.getInstance();
        this.uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
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
        final Recipe recipe = lstRecipe.get(position);
        final MyViewHolder holder_copy = holder;
        // todo check this : should check if the recipe is bookmarked by the user on the db
        Log.v(TAG, "asking for recipe " + recipe.getId());
        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference().child(uid).child(recipe.getId());
        recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "asking for recipe " + recipe.getId() + ", getting "+ String.valueOf(dataSnapshot));
                if (dataSnapshot.getValue() != null) {
                    holder_copy.recipe_bookmark.setImageResource(R.drawable.bookmarked);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.recipe_title.setText(recipe.getTitle());
        if (recipe.getThumbnail().isEmpty()) {
            holder.recipe_thumbnail.setImageResource(R.drawable.nopicture);
        } else{
            Picasso.get().load(recipe.getThumbnail()).into(holder.recipe_thumbnail);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(applicationContext, Recipe_activity.class);
                intent.putExtra("id", recipe.getId());
                intent.putExtra("title",recipe.getTitle());
                intent.putExtra("img",recipe.getThumbnail());
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
        ImageView recipe_bookmark;
        CardView cardView;
        public MyViewHolder(View view) {
            super(view);
            recipe_title = itemView.findViewById(R.id.search_result_recipe_title);
            recipe_thumbnail = itemView.findViewById(R.id.search_result_recipe_img);
            recipe_bookmark = itemView.findViewById(R.id.search_result_recipe_bookmark);
            cardView = itemView.findViewById(R.id.search_result_cardview);
        }
    }
}

package com.example.recipeapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RVAdapterRecipeInstructions extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context applicationContext;
    private final AnalysedInstructions instructions;
    private static ClickListener clickListener;
    private final ArrayList<String> positionToExtend = new ArrayList<>();
    private static final String TAG = "RVAdapterRecipeInstru";

    public RVAdapterRecipeInstructions(Context applicationContext, AnalysedInstructions instructions) {
        this.applicationContext = applicationContext;
        this.instructions = instructions;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(applicationContext);
        if (viewType == 0) {
            view = inflater.inflate(R.layout.item_instruction_step_simple, parent, false);
            return new RVAdapterRecipeInstructions.ViewHolderSimple(view);
        } else {
            view = inflater.inflate(R.layout.item_instruction_step_detail, parent, false);
            return new RVAdapterRecipeInstructions.ViewHolderDetails(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == 0) {
            ViewHolderSimple viewHolderSimple = (ViewHolderSimple) holder;
            viewHolderSimple.instruction_step.setText(this.instructions.get(position).getStepText());
        } else {
            ViewHolderDetails viewHolderDetails = (ViewHolderDetails) holder;
            viewHolderDetails.instruction_step.setText(this.instructions.get(position).getStepText());
            List<Equipment> equipments = instructions.get(position).getEquipments();
            if (!equipments.isEmpty())
                Picasso.get().load(equipments.get(0).getThumbnail()).into(viewHolderDetails.equipment_img);
        }

    }

    @Override
    public int getItemViewType(int position) {
        String st_i = String.valueOf(position);
        if(positionToExtend.contains(st_i)){
            return 1;
        }
        else{
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return this.instructions.size();
    }

    public void itemClicked(int position){
        Log.v(TAG, "itemClicked at pos " + position);
        String st_i = String.valueOf(position);
        if(positionToExtend.contains(st_i)){
            positionToExtend.remove(st_i);
            Log.v(TAG, "removing from list " + position);
        }
        else {
            positionToExtend.add(st_i);
            Log.v(TAG, "adding to list " + position);
        }
    }

    public static class ViewHolderSimple extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView instruction_step;
        private static final String TAG = "ViewHolderSimple";

        public ViewHolderSimple(View view) {
            super(view);
            Log.v(TAG, "constructor");
            view.setOnClickListener(this);
            instruction_step = itemView.findViewById(R.id.instruction_step);
        }

        @Override
        public void onClick(View v) {
            Log.v(TAG, "on click");
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public static class ViewHolderDetails extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView instruction_step;
        ImageView equipment_img;
        private static final String TAG = "ViewHolderDetails";

        public ViewHolderDetails(View view) {
            super(view);
            Log.v(TAG, "constructor");
            view.setOnClickListener(this);
            instruction_step = itemView.findViewById(R.id.instruction_step);
            equipment_img = itemView.findViewById(R.id.instruction_ingredient_img);
        }

        @Override
        public void onClick(View v) {
            Log.v(TAG, "on click");
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        RVAdapterRecipeInstructions.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}

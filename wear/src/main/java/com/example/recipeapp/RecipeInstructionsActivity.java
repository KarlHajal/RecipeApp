package com.example.recipeapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeInstructionsActivity extends AppCompatActivity {
    public static final String STOP_ACTIVITY = "STOP_ACTIVITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_instructions);
    }
}
package com.example.recipeapp;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeInstructionsActivity extends AppCompatActivity {
    public static final String STOP_ACTIVITY = "STOP_ACTIVITY";

    private TextView tv_instructionStep;
    private ImageButton bt_prev, bt_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_instructions);

        tv_instructionStep = (TextView) findViewById(R.id.tv_instruction_step);
        bt_prev = (ImageButton) findViewById(R.id.bt_prev);
        bt_next = (ImageButton) findViewById(R.id.bt_next);
    }
}
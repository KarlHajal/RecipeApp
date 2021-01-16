package com.example.recipeapp;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import static com.example.recipeapp.WearService.INSTRUCTIONS;

public class RecipeInstructionsActivity extends WearableActivity {
    public static final String STOP_ACTIVITY = "STOP_ACTIVITY";

    private TextView tv_instructionStep;
    private ImageButton bt_prev, bt_next;
    private int currentInstructionShown;

    private AnalysedInstructions instructions;

    // Tag for Logcat
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_instructions);

        // get views for instructions steps
        tv_instructionStep = (TextView) findViewById(R.id.tv_instruction_step);
        bt_prev = (ImageButton) findViewById(R.id.bt_prev);
        bt_next = (ImageButton) findViewById(R.id.bt_next);

        instructions = (AnalysedInstructions) this.getIntent().getParcelableExtra(INSTRUCTIONS);
        Log.v(TAG, "received instructions " + instructions.toString());
        currentInstructionShown = 0;
        updateInstructionsDisplay();

        // setting the onClickListener to prev + next button
        bt_prev.setOnClickListener(onClickListener);
        bt_next.setOnClickListener(onClickListener);

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void updateInstructionsDisplay() {
        Log.w(TAG, "updateInstructionsDisplay");
        if(instructions == null){
            Log.w(TAG, "updateInstructionsDisplay instructions == null");
            tv_instructionStep.setText(R.string.no_instructions);
            currentInstructionShown = 0;
        }
        else {
            int instructionSize = instructions.size();
            Log.w(TAG, "updateInstructionsDisplay instructions != null size " + instructionSize);
            if(currentInstructionShown >= instructionSize){
                currentInstructionShown = instructionSize-1;
            }
            if(currentInstructionShown<0){
                currentInstructionShown = 0;
            }
            String instructionsStep = instructions.get(currentInstructionShown).getStepText();
            tv_instructionStep.setText(instructionsStep);
        }
    }

    // click happening on prev or next button
    private final OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.w(TAG, "onClickListener");
            if(view.getId() == R.id.bt_prev){
                Log.w(TAG, "onClickListener bt_prev");
                currentInstructionShown--;
            }
            else if (view.getId() == R.id.bt_next){
                Log.w(TAG, "onClickListener bt_next");
                currentInstructionShown++;
            }
            updateInstructionsDisplay();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent stopratingintent = new Intent(this, RatingService.class);
        stopService(stopratingintent);
    }

}
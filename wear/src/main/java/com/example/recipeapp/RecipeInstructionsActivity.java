package com.example.recipeapp;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import static com.example.recipeapp.WearService.INSTRUCTIONS;

public class RecipeInstructionsActivity extends WearableActivity implements SensorEventListener {
    public static final String STOP_ACTIVITY = "STOP_ACTIVITY";

    private TextView tv_instructionStep;
    private ImageButton bt_prev, bt_next;
    private int currentInstructionShown;
    float totalAcc = 0;
    float[] prevAcc = new float[3];


    private AnalysedInstructions instructions;

    private SensorManager mSensorManager;
    private Sensor mSensor_acc;

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
        Log.i(TAG, "received instructions " + instructions.toString());
        currentInstructionShown = 0;
        updateInstructionsDisplay();

        // setting the onClickListener to prev + next button
        bt_prev.setOnClickListener(onClickListener);
        bt_next.setOnClickListener(onClickListener);

        // get sensor manager for accelerometer
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor_acc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(mSensor_acc == null){
            Log.w(TAG, "no TYPE_ACCELEROMETER sensor");
        }

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register accelerometer
        mSensorManager.registerListener(this, mSensor_acc, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister accelerometer
        sendTotalAccToMobile(totalAcc);
        totalAcc = 0;
        Log.w(TAG, "instructions paused");
        mSensorManager.unregisterListener(this);
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
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                // get accelerometer data
                float[] acc = new float[3];
                acc[0] = event.values[0];
                acc[1] = -event.values[1];
                acc[2] = event.values[2];
                totalAcc = (float) (totalAcc + Math.abs(prevAcc[0] - acc[0]) + Math.abs(prevAcc[1] - acc[1]) + Math.abs(prevAcc[2] - acc[2]));
                prevAcc[0] = acc[0];
                prevAcc[1] = acc[1];
                prevAcc[2] = acc[2];
                sendAccToMobile(acc);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void sendAccToMobile(float[] acc) {
        Log.v(TAG, "sendAccToMobile - sending new acc data : " + acc[0] +" "+ acc[1] +" "+ acc[2]);
        Intent intent = new Intent(this, WearService.class);
        intent.setAction(WearService.ACTION_SEND.ACCELERATION.name());
        intent.putExtra(WearService.ACCELERATION, acc);
        this.startService(intent);
    }
    private void sendTotalAccToMobile(float totalacc) {
        Log.v(TAG, "sendAccToMobile - sending total acc data : " + totalacc);
        Intent intent = new Intent(this, WearService.class);
        intent.setAction(WearService.ACTION_SEND.TOTACCELERATION.name());
        intent.putExtra(WearService.TOTACCELERATION, totalacc);
        this.startService(intent);
    }

}
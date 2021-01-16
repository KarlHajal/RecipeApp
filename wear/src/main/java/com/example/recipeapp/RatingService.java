package com.example.recipeapp;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class RatingService extends Service implements SensorEventListener {

    float totalAcc = 0;
    float[] prevAcc = new float[3];
    private SensorManager mSensorManager;
    private Sensor mSensor_acc;
    // Tag for Logcat
    private final String TAG = this.getClass().getSimpleName();
    public RatingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor_acc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(mSensor_acc == null){
            Log.w(TAG, "no TYPE_ACCELEROMETER sensor");
        }
        Log.w(TAG, "starting rating service");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendTotalAccToMobile(totalAcc);
        totalAcc = 0;
        Log.w(TAG, "instructions paused");
        mSensorManager.unregisterListener(this);
    }

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
//                sendAccToMobile(acc);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void sendTotalAccToMobile(float totalacc) {
        Log.i(TAG, "sendTotalAccToMobile - sending total acc data : " + totalacc);
        int rating = 0;
        if (totalacc>100) {
            rating = 5;
        } else if ((totalacc>80)&(totalacc<=100)) {
            rating = 4;
        } else if ((totalacc>60)&(totalacc<=80)) {
            rating = 3;
        } else if ((totalacc>30)&(totalacc<=60)) {
            rating = 2;
        } else {
            rating = 1;
        }
        Intent intent = new Intent(this, WearService.class);
        intent.setAction(WearService.ACTION_SEND.TOTACCELERATION.name());
        intent.putExtra(WearService.TOTACCELERATION, rating);
        this.startService(intent);
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
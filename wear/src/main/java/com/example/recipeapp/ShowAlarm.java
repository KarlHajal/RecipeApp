package com.example.recipeapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ShowAlarm extends BroadcastReceiver {

    private TextView mTextView;


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat mNotifyManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder myNotif = new NotificationCompat.Builder(context);
    }
}
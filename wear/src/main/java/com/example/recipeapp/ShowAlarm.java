package com.example.recipeapp;

import android.app.PendingIntent;
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
        myNotif.setContentTitle("Recipe Done");
        myNotif.setContentText("Your dish should now be ready!");

        Intent i1 = new Intent(context, RecipeInstructionsActivity.class);
        PendingIntent pd = PendingIntent.getActivity(context,0,i1,0);
        myNotif.setContentIntent(pd);
        myNotif.setAutoCancel(true);

        mNotifyManager.notify(1,myNotif.build());
    }
}
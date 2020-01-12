package com.example.multitimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {

    private static final String TAG = "MyActivity";


    //   public Context context;
    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("title");
        int id = intent.getIntExtra("id", 0);

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(title, "Yiss!!!");
        notificationHelper.getManager().notify(id, nb.build());

    }


}
